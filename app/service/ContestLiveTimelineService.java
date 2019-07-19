package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Lineup;
import models.sports.AthleteSportEventInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.io.IOException;
import java.util.*;

/**
 * Provides a complete scoring timeline for all athletes within a lineup.
 */
public class ContestLiveTimelineService extends AbstractCachingService {

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {
    };
    private TimelineComparator timelineComparator = new TimelineComparator();

    /**
     * Create an aggregate timeline from all the timelines of individual athletes in the provided lineup.  The list
     * will be sorted such that the most recent update is the first.
     *
     * @param lineupId The id of the lineup to create a timeline for.
     * @return A JSON string representing the timeline (list of JSON objects).
     * @throws IOException When Jackson parsing goes wrong.
     */
    @Cacheable(value = "lineupTimeline")
    public String getLineupTimeline(int lineupId) throws IOException {
        Lineup lineup = DaoFactory.getContestDao().findLineup(lineupId);
        List<AthleteSportEventInfo> athleteSportEventInfoList = DaoFactory.getSportsDao().findAthleteSportEventInfos(lineup);
        List<Map<String, Object>> timelineData = new ArrayList<>();

        for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfoList) {
            List<Map<String, Object>> currTimeline = mapper.readValue(athleteSportEventInfo.getTimeline(), typeReference);
            for (Map<String, Object> timelineEntry : currTimeline) {
                timelineData.add(timelineEntry);
            }
        }
        Collections.sort(timelineData, timelineComparator);

        return mapper.writeValueAsString(timelineData);
    }

    /**
     * Removes the cache entry with the provided lineupId.
     *
     * @param lineupId The id of the lineup whose timeline we need to evict.
     */
    @CacheEvict(value = "lineupTimeline")
    public void removeLineupTimeline(int lineupId) {
    }

    @Override
    @CacheEvict(value = "lineupTimeline", allEntries = true)
    public void flushAllCaches() {
    }

    /**
     * Comparator for Java Map representations of two timeline entries.
     */
    private class TimelineComparator implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> timeline1, Map<String, Object> timeline2) {
            Date date1 = new Date((Long) timeline1.get("timestamp"));
            Date date2 = new Date((Long) timeline2.get("timestamp"));

            if (date1.before(date2)) {
                return 1;
            } else if (date1.after(date2)) {
                return -1;
            }

            return 0;
        }
    }
}
