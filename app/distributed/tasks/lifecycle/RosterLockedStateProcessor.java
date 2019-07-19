package distributed.tasks.lifecycle;

import dao.IContestDao;
import models.contest.Contest;

import java.time.Instant;

/**
 * Created by mwalsh on 7/25/14.
 */
public class RosterLockedStateProcessor implements IStateProcessor {

    private Instant now;
    private IContestDao contestDao;

    public RosterLockedStateProcessor(Instant now, IContestDao contestDao) {
        this.now = now;
        this.contestDao = contestDao;
    }

    @Override
    public void process(Contest contest) throws Exception {
        if (now.isAfter(contest.getStartTime().toInstant())) {
            //Proceed to active state
            contest.proceedNext();
            contestDao.updateContest(contest);
        }
    }

}
