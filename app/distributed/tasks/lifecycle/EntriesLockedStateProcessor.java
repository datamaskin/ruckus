package distributed.tasks.lifecycle;

import dao.IContestDao;
import models.contest.Contest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by mwalsh on 7/25/14.
 */
public class EntriesLockedStateProcessor implements IStateProcessor {

    private Instant now;

    private IContestDao contestDao;

    public EntriesLockedStateProcessor(Instant now, IContestDao contestDao) {
        this.now = now;
        this.contestDao = contestDao;
    }

    @Override
    public void process(Contest contest) throws Exception {
        Instant twoMinPrior = contest.getStartTime().toInstant().minus(2, ChronoUnit.MINUTES);
        if (now.isAfter(twoMinPrior)) {
            //Proceed to active state
            contest.proceedNext();
            contestDao.updateContest(contest);
        }
    }

}
