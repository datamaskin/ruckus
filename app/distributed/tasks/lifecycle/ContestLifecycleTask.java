package distributed.tasks.lifecycle;

import service.ChatService;
import service.IContestListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.IContestDao;
import distributed.DistributedServices;
import distributed.tasks.DistributedTask;
import models.contest.*;
import utils.ITimeService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/26/14.
 */
public class ContestLifecycleTask extends DistributedTask {

    private IContestDao contestDao;
    private ITimeService timeService;
    private IContestListService contestListManager;
    private IRandomizer randomizer;
    private final ObjectMapper mapper;

    public ContestLifecycleTask(IContestDao contestDao, ITimeService timeService,
                                IContestListService contestListManager, IRandomizer randomizer) {
        this.contestDao = contestDao;
        this.timeService = timeService;
        this.contestListManager = contestListManager;
        this.randomizer = randomizer;
        this.mapper = new ObjectMapper();
    }

    public String publicExecute() throws Exception {
        return execute();
    }

    @Override
    protected String execute() throws Exception {

        List<Contest> contests = contestDao.findNonTerminalContests();

        Instant now = timeService.getNow().plus(5, ChronoUnit.SECONDS);

        for (Contest contest : contests) {
            ContestState beforeState = contest.getContestState();

            if (contest.getContestState().getId() == new ContestStateOpen().getId()) {

                new OpenStateProcessor(now, contestDao, contestListManager, randomizer).process(contest);

            } else if (contest.getContestState().getId() == new ContestStateEntriesLocked().getId()) {

                new EntriesLockedStateProcessor(now, contestDao).process(contest);

            } else if (contest.getContestState().getId() == new ContestStateRosterLocked().getId()) {

                new RosterLockedStateProcessor(now, contestDao).process(contest);

            } else if (contest.getContestState().getId() == new ContestStateActive().getId()) {

                new ActiveStateProcessor(contestDao, contestListManager, new ChatService()).process(contest);

            } else if (contest.getContestState().getId() == new ContestStateComplete().getId()) {

                new CompletedStateProcessor(contestDao).process(contest);

            }

            /*
             * Check if the contest changed states.  If so, broadcast it out to its topic.
             */
            if(beforeState.getId() != contest.getContestState().getId()) {
                Map<String, Object> contestStateUpdate = new HashMap<>();
                contestStateUpdate.put("contestId", contest.getUrlId());
                contestStateUpdate.put("contestStateId", contest.getContestState().getId());
                contestStateUpdate.put("contestStateName", contest.getContestState().getName());

                DistributedServices.getInstance().getTopic(GlobalConstants.CONTEST_STATE_UPDATE_TOPIC + contest.getId()).publish(mapper.writeValueAsString(contestStateUpdate));
            }
        }

        return "ok";

    }

}
