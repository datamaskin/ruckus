package distributed.tasks.lifecycle;

import service.IContestListService;
import dao.IContestDao;
import models.contest.Contest;
import models.contest.ContestType;
import models.contest.Entry;
import models.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by mwalsh on 7/25/14.
 */
public class OpenStateProcessor implements IStateProcessor {

    private Instant now;
    private IContestDao contestDao;
    private IContestListService contestListManager;
    private IRandomizer randomizer;

    public OpenStateProcessor(Instant now, IContestDao contestDao,
                              IContestListService contestListManager, IRandomizer randomizer) {
        this.now = now;
        this.contestDao = contestDao;
        this.contestListManager = contestListManager;
        this.randomizer = randomizer;
    }

    @Override
    public void process(Contest contest) throws Exception {
        //Contest is GPP
        if (contest.getContestType().getId() == ContestType.GPP.getId()) {

            //We're within an hour of start time
            Instant hourPrior = contest.getStartTime().toInstant().minus(1, ChronoUnit.HOURS);
            if (now.isAfter(hourPrior) && now.isBefore(contest.getStartTime().toInstant())) {
                //Proceed to entries locked state
                contest.proceedNext();
                contestDao.updateContest(contest);
            }

        } else if(contest.getContestType().equals(ContestType.ANONYMOUS_H2H)){
            Instant fiveMinutesPrior = contest.getStartTime().toInstant().minus(5, ChronoUnit.MINUTES);
            if (now.isAfter(fiveMinutesPrior)) {
                List<Entry> entries = contestDao.findEntries(contest);
                findMatchup(contest, entries);
            }
        } else {
            //Non GPP contest and entries have not filled, so cancel it
            if (contest.getCurrentEntries() < contest.getCapacity()
                    && now.isAfter(contest.getStartTime().toInstant().minus(2, ChronoUnit.MINUTES))) {
                contestDao.cancelContest(contest);
                contestListManager.removeContest(contest.getUrlId());
            }

        }
    }

    private void findMatchup(final Contest contest, final List<Entry> entries) {
        while(entries.size() > 1){
            int randoSpot = randomizer.getRandomInt(entries.size());

            Entry rando = entries.remove(randoSpot);
            Entry entry = entries.remove(0);

            if(entry.getUser().getId() == rando.getUser().getId()){
                entries.add(entry);
                entries.add(rando);
            } else {
                contestDao.createNewContest(contest.getContestType(), contest.getLeague(), "",
                        2, contest.isPublic(), contest.getEntryFee(), contest.getAllowedEntries(),
                        contest.getSalaryCap(), contest.getSportEventGrouping(), contest.getContestPayouts(), null);
                contestDao.joinContest(entry.getUser(), contest, entry.getLineup());
                contestDao.joinContest(rando.getUser(), contest, rando.getLineup());
            }

            if(listContainsOneUser(entries)){
                return;
            }

        }
    }

    private boolean listContainsOneUser(List<Entry> entries) {
        User user = null;
        for(Entry entry: entries){
            if(user != null && user.getId() != entry.getUser().getId()){
                return false;
            }
            user = entry.getUser();
        }
        return true;
    }

}
