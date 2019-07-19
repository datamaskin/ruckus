package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Contest;
import models.contest.ContestState;
import models.contest.ContestType;
import models.contest.Entry;
import models.sports.AthleteSportEventInfo;
import models.sports.SportEvent;
import models.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager to support the data needs for the ContestLiveDrillin endpoint (not including the projection graph).
 */
public class ContestLiveDrillinService extends AbstractCachingService {
    private ObjectMapper mapper;

    private TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
    };
    private TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {
    };

    private User user;

    public ContestLiveDrillinService() {
        mapper = new ObjectMapper();
    }

    @Cacheable(value = "contestLiveDrillin", key = "#contest.id")
    public String getInitialLoadAsJson(Contest contest) throws IOException {
        if (!contest.getContestState().equals(ContestState.active)
                && !contest.getContestState().equals(ContestState.complete)
                && !contest.getContestState().equals(ContestState.history)) {
            return new ObjectMapper().writeValueAsString(new HashMap<>());
        }

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> contestData = new HashMap<>();
        contestData.put("league", contest.getLeague().getAbbreviation());
        contestData.put("entryFee", contest.getEntryFee());
        contestData.put("currentEntries", contest.getCurrentEntries());
        contestData.put("prizePool", contest.calculatePrizePool());
        contestData.put("payouts", contest.getContestPayouts());
        contestData.put("contestState", contest.getContestState().getName());
        contestData.put("startTime", contest.getStartTime().getTime());
        if (contest.getContestType().equals(ContestType.H2H)) {
            List<Entry> entries = DaoFactory.getContestDao().findEntries(contest);
            if (entries.size() == 1) {
                contestData.put("opp", "");
            } else if (entries.get(0).getUser().equals(user)) {
                contestData.put("opp", entries.get(1).getUser().getUserName());
            } else {
                contestData.put("opp", entries.get(0).getUser().getUserName());
            }
        }
        data.put("contest", contestData);

        List<Map<String, Object>> entries = new ArrayList<>();
        for (Entry entry : DaoFactory.getContestDao().findEntries(contest)) {
            Map<String, Object> entryMap = mapper.readValue(getEntryUpdateAsJson(entry), typeReference);
            entries.add(entryMap);
        }

        data.put("entries", entries);

        return mapper.writeValueAsString(data);
    }

    @Cacheable(value = "contestLiveDrillinEntryUpdate", key = "#entry.id")
    public String getEntryUpdateAsJson(Entry entry) throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entry.getId());
        data.put("user", entry.getUser().getUserName());
        data.put("unitsRemaining", DaoFactory.getContestDao().calculateUnitsRemaining(entry));

        BigDecimal fpp = new BigDecimal(entry.getPoints());
        fpp = fpp.setScale(2, RoundingMode.HALF_EVEN);
        data.put("fpp", fpp);

        return mapper.writeValueAsString(data);
    }

    @Cacheable(value = "contestLiveDrillinSportEventUpdate", key = "#sportEvent.id")
    public String getSportEventUpdateAsJson(SportEvent sportEvent) throws IOException {
        Map<String, Object> data = mapper.readValue(sportEvent.getShortDescription(), typeReference);
        data.put("id", sportEvent.getId());
        data.put("unitsRemaining", sportEvent.getUnitsRemaining());

        return mapper.writeValueAsString(data);
    }

    @Cacheable(value = "contestLiveDrillinAthleteSportEventInfoUpdate", key = "#athleteSportEventInfo.id")
    public String getAthleteSportEventInfoUpdateAsJson(AthleteSportEventInfo athleteSportEventInfo) throws IOException {
        List<Map<String, Object>> boxscore = mapper.readValue(athleteSportEventInfo.determineStatsForDisplay(), listTypeReference);
        List<Map<String, Object>> timeline = mapper.readValue(athleteSportEventInfo.getTimeline(), listTypeReference);

        Map<String, Object> data = new HashMap<>();
        data.put("athleteSportEventInfoId", athleteSportEventInfo.getId());

        BigDecimal fpp = new BigDecimal(athleteSportEventInfo.getFantasyPoints().doubleValue());
        fpp = fpp.setScale(2, RoundingMode.HALF_EVEN);
        data.put("fpp", fpp);
        data.put("stats", boxscore);
        data.put("timeline", determineNewTimelineEntries(timeline));
        data.put("firstName", athleteSportEventInfo.getAthlete().getFirstName());
        data.put("lastName", athleteSportEventInfo.getAthlete().getLastName());
        data.put("indicator", athleteSportEventInfo.getIndicator());
        data.put("unitsRemaining", athleteSportEventInfo.getSportEvent().getUnitsRemaining());

        return mapper.writeValueAsString(data);
    }

    private List<Map<String, Object>> determineNewTimelineEntries(List<Map<String, Object>> timeline) {
        List<Map<String, Object>> prunedTimeline = new ArrayList<>();
        for (Map<String, Object> timelineEntry : timeline) {
            boolean published = (boolean) timelineEntry.get("published");
            if (!published) {
                prunedTimeline.add(timelineEntry);
            }
        }

        return prunedTimeline;
    }

    @CacheEvict(value = "contestLiveDrillin", key = "#contest.id")
    public void updateDrillin(Contest contest) {
        //TODO: need to update a topic somewhere. This call will invalidate the cache
    }

    @CacheEvict(value = "contestLiveDrillinEntryUpdate", key = "#entry.id")
    public void updateDrillinEntry(Entry entry) {

    }

    @CacheEvict(value = "contestLiveDrillinSportEventUpdate", key = "#sportEvent.id")
    public void updateDrillinSportEvent(SportEvent sportEvent) {

    }

    @CacheEvict(value = "contestLiveDrillinAthleteSportEventInfoUpdate", key = "#athleteSportEventInfo.id")
    public void updateDrillinAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {

    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "contestLiveDrillin", allEntries = true),
            @CacheEvict(value = "contestLiveDrillinEntryUpdate", allEntries = true),
            @CacheEvict(value = "contestLiveDrillinSportEventUpdate", allEntries = true),
            @CacheEvict(value = "contestLiveDrillinAthleteSportEventInfoUpdate", allEntries = true)})
    public void flushAllCaches() {
    }
}
