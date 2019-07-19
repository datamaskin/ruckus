package distributed.tasks.lifecycle;

import service.EdgeCacheService;
import service.IChatService;
import service.IContestListService;
import dao.IContestDao;
import models.contest.Contest;
import models.sports.SportEvent;

/**
 * Created by mwalsh on 7/25/14.
 */
public class ActiveStateProcessor implements IStateProcessor {

    private IContestDao contestDao;
    private IContestListService contestListManager;
    private IChatService chatManager;
    private EdgeCacheService edgeCacheService;

    public ActiveStateProcessor(IContestDao contestDao,
                                IContestListService contestListManager,
                                IChatService chatManager) {
        this.contestDao = contestDao;
        this.contestListManager = contestListManager;
        this.chatManager = chatManager;
        edgeCacheService = new EdgeCacheService();
    }

    @Override
    public void process(Contest contest) throws Exception {
        boolean completed = true;
        for (SportEvent event : contest.getSportEventGrouping().getSportEvents()) {
            if (!event.isComplete()) {
                completed = false;
                break;
            }
        }

        if (completed) {
            contest.proceedNext();
            contestDao.updateContest(contest);
            contestListManager.removeContest(contest.getUrlId());
            chatManager.destroyChannel(contest.getUrlId());
            edgeCacheService.evictOnContestComplete();
        }

    }

}
