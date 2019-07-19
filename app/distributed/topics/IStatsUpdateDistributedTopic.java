package distributed.topics;

import com.hazelcast.core.Message;
import models.contest.Contest;
import models.contest.Entry;
import models.contest.Lineup;
import models.sports.AthleteSportEventInfo;
import models.sports.SportEvent;

import java.util.Map;
import java.util.Set;

/**
 * Interface for DistributedTopic implementations responsible for receiving and processing Stats updates.
 */
public interface IStatsUpdateDistributedTopic {
    /**
     * Acts as the starting point for entire workflow of message processing.
     *
     * @param message       The String representing the message coming in from Stats.
     */
    void process(String message);

    /**
     * Publish an athlete that has changed as a result of a stats update.
     *
     * @param athleteSportEventInfo The AthleteSportEventInfo object to publish.
     */
    void publishAthleteSportEventInfoChanges(AthleteSportEventInfo athleteSportEventInfo);

    /**
     * Publish an entry that has changed as a result of a stats update.
     *
     * @param entry The entry to publish.
     */
    void publishEntryChanges(Entry entry);

    /**
     * Publishes contest whose entries have changed.
     *
     * @param uniqueContests The set of contests that have at least one entry updated as a result of
     *                       the latest stats update.
     */
    void publishEntryChangesForContest(Set<Contest> uniqueContests);

    /**
     * Publishes the lineup whose entry has changed.
     *
     * @param uniqueLineups The set of lineups that have at least one entry updated as a result of
     *                      the latest stats update.
     */
    void publishLineupChanges(Set<Lineup> uniqueLineups);

    /**
     * Publishes the SportEvents that changed.
     *
     * @param sportEvent
     */
    void publishSportEventChanges(SportEvent sportEvent);

    /**
     * Publishes changes to athlete status indicators.
     *
     * @param indicators    The map of indicators for athletes in the sport event.
     * @param sportEvent    The sport event being processed.
     */
    void publishGeneralAthleteChanges(Map<Integer, Integer> indicators, SportEvent sportEvent);
}
