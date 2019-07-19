package distributed.tasks.nfl;

import dao.DaoFactory;
import dao.IContestDao;
import dao.ISportsDao;
import distributed.tasks.DistributedTask;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.Entry;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.SportEvent;
import stats.retriever.nfl.INflBoxscoreRetriever;
import utils.ITimeService;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 8/19/14.
 */
public class StatsNflBoxscoreReconciliationTask extends DistributedTask {
    private ITimeService timeService;
    private INflBoxscoreRetriever boxscoreRetriever;

    public StatsNflBoxscoreReconciliationTask(ITimeService timeService, INflBoxscoreRetriever boxscoreRetriever) {
        this.timeService = timeService;
        this.boxscoreRetriever = boxscoreRetriever;
    }

    @Override
    protected String execute() throws Exception {
        IContestDao contestDao = DaoFactory.getContestDao();
        ISportsDao sportsDao = DaoFactory.getSportsDao();

        List<Contest> contests = contestDao.findContests(ContestState.complete);
        for(Contest contest: contests) {
            boolean allCompleted = true;

            for(SportEvent sportEvent: contest.getSportEventGrouping().getSportEvents()) {
                List<Map<Integer, BigDecimal>> updates = boxscoreRetriever.reconcileBoxscores(sportEvent);

                /*
                 * updates will be empty if the game is not in state 4 (Final)
                 */
                if(!updates.isEmpty()) {
                    // Update ASEIs and entries
                    for(Map<Integer, BigDecimal> update: updates) {
                        for(Map.Entry<Integer, BigDecimal> entry: update.entrySet()) {
                            Athlete athlete = sportsDao.findAthlete(entry.getKey());
                            if(athlete == null)
                                continue;
                            AthleteSportEventInfo athleteSportEventInfo = sportsDao.findAthleteSportEventInfo(athlete, sportEvent);
                            athleteSportEventInfo.setFantasyPoints(entry.getValue());
                            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
                        }
                    }
                }
                /*
                 * Game is not final, so this contest shouldn't be considered reconciled yet.
                 */
                else {
                    allCompleted = false;
                }
            }

            /*
             * All sport events have been reconciled, so flag the contest as reconciled so the life cycle manager
             * will move it into historical state.
             */
            if(allCompleted) {
                List<Entry> entries = contestDao.findEntries(contest);
                for(Entry entry: entries) {
                    entry.updateEntryFantasyPoints();
                }

                contest.setReconciledTime(Date.from(timeService.getNow()));
                contestDao.saveContest(contest);
            }
        }

        return "";
    }
}
