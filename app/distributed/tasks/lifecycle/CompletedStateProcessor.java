package distributed.tasks.lifecycle;

import dao.IContestDao;
import models.contest.Contest;

/**
 * Created by mgiles on 8/18/14.
 */
public class CompletedStateProcessor implements IStateProcessor {

    IContestDao contestDao;

    public CompletedStateProcessor(IContestDao contestDao) {
        this.contestDao = contestDao;
    }

    @Override
    public void process(Contest contest) throws Exception {
        if (contest.getReconciledTime() != null) {
            contest.proceedNext();
            contestDao.closeContest(contest);
        }
    }
}
