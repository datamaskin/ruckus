package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.ClientMessage;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.contest.Contest;
import models.contest.ContestType;
import models.contest.Entry;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import play.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgiles on 6/19/14.
 */

public class ContestListService extends AbstractCachingService implements IContestListService {

    private ContestAthletesService contestAthletesManager;

    public ContestListService(ContestAthletesService contestAthletesManager) {
        this.contestAthletesManager = contestAthletesManager;
    }

    @Override
    @Cacheable(value = "contest")
    public String getContestAsJson(String contestId) throws JsonProcessingException {
        Logger.debug("{} not in cache, creating", contestId);
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
        Map<Object, Object> m = new HashMap<>();
        m.put("id", contestId);
        Map<String, String> contestType = new HashMap<>();
        contestType.put("name", contest.getContestType().getName());
        contestType.put("abbr", contest.getContestType().getAbbr());
        m.put("contestType", contestType);
        m.put("displayName", contest.getDisplayName());
        m.put("league", contest.getLeague().getAbbreviation());
        m.put("allowedEntries", contest.getAllowedEntries());
        m.put("guaranteed", contest.isGuaranteed());
        m.put("currentEntries", contest.getCurrentEntries());
        m.put("capacity", contest.getCapacity());
        m.put("payout", contest.getContestPayouts());
        m.put("entryFee", contest.getEntryFee());
        m.put("grouping", contest.getSportEventGrouping().getSportEventGroupingType().getId());
        m.put("salaryCap", contest.getSalaryCap());
        m.put("startTime", contest.getStartTime());
        m.put("prizePool", contest.calculatePrizePool());
        if (contest.getContestType().equals(ContestType.H2H)) {
            List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
            if (entries.size() > 0) {
                Entry entry = entries.get(0);
                m.put("opp", entry.getUser().getUserName());
            } else {
                m.put("opp", "H2H");
            }
        }
        return new ObjectMapper().writeValueAsString(m);
    }

    @Override
    public void notifyOfNewContest(String contestId) throws JsonProcessingException {
        //notify topic -- will be cached the next time it is accessed
        ClientMessage msg = new ClientMessage();
        msg.setType(GlobalConstants.CONTEST_ADD);
        msg.setJsonPayload(getContestAsJson(contestId));
        DistributedServices.getInstance().getTopic(GlobalConstants.CONTEST_UPDATE_TOPIC).publish(new ObjectMapper().writeValueAsString(msg));
    }

    @Override
    @CacheEvict(value = "contest", beforeInvocation = true)
    public void removeContest(String contestId) throws JsonProcessingException {
        Contest contest = DaoFactory.getContestDao().findContest(contestId);
//        ContestAthletesManager athleteCache = (ContestAthletesManager) DistributedServices.getContext().getBean("ContestAthletesManager");
        contestAthletesManager.removeContestAthletes(contestId);
        //notify topic
        ClientMessage msg = new ClientMessage();
        Map<String, Object> map = new HashMap<>();
        map.put("id", contestId);
        map.put("currentEntries", contest.getCurrentEntries());

        msg.setType(GlobalConstants.CONTEST_REMOVE);
        msg.setPayload(map);
        DistributedServices.getInstance().getTopic(GlobalConstants.CONTEST_UPDATE_TOPIC).publish(new ObjectMapper().writeValueAsString(msg));
    }

    @Override
    @CacheEvict(value = "contest", beforeInvocation = true)
    public void updateContestEntries(String contestId) throws JsonProcessingException, JSONException {
        JSONObject json = new JSONObject(getContestAsJson(contestId));
        //notify topic
        ClientMessage msg = new ClientMessage();
        msg.setType(GlobalConstants.CONTEST_UPDATE);
        Map<String, Object> map = new HashMap<>();
        map.put("id", json.get("id"));
        map.put("currentEntries", json.get("currentEntries"));
        msg.setPayload(map);
        DistributedServices.getInstance().getTopic(GlobalConstants.CONTEST_UPDATE_TOPIC).publish(new ObjectMapper().writeValueAsString(msg));
    }

    @Override
    @CacheEvict(value = "contest", allEntries = true)
    public void flushAllCaches() {
    }
}
