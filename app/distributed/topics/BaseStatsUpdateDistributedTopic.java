package distributed.topics;

import com.hazelcast.core.Member;
import play.Play;
import play.libs.F;
import service.ContestLiveAthleteService;
import service.ContestLiveDrillinService;
import service.EdgeCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Message;
import common.GlobalConstants;
import dao.DaoFactory;
import dao.IContestDao;
import dao.ISportsDao;
import distributed.DistributedServices;
import distributed.DistributedTopic;
import models.contest.Contest;
import models.contest.Entry;
import models.contest.Lineup;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.SportEvent;
import org.xml.sax.SAXException;
import play.Logger;
import stats.socketfeed.parser.StatsSaxParsingHandler;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.IUpdateProcessor;
import stats.updateprocessor.FantasyPointAthleteUpdateEvent;
import stats.updateprocessor.FantasyPointUpdateEvent;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static play.libs.F.Promise.promise;

/**
 * Created by dmaclean on 7/17/14.
 */
public class BaseStatsUpdateDistributedTopic extends DistributedTopic implements IStatsUpdateDistributedTopic {
    /**
     * Used to accumulate XML message updates from the Stats socket.
     */
    protected StringBuilder sb = new StringBuilder();

    /**
     * Class that will perform translation duties for fantasy points.
     */
    protected IFantasyPointTranslator translator;

    /**
     * Class that will process stats updates.
     */
    protected IUpdateProcessor updateProcessor;

    protected ISportsDao sportsDao;

    protected IContestDao contestDao;

    /**
     * Manages eviction of edge caches.
     */
    protected EdgeCacheService edgeCacheService;

    protected ObjectMapper mapper = new ObjectMapper();

    protected StatsSaxParsingHandler saxParsingHandler;
    protected SAXParser saxParser;
    protected String socketXmlRootNodeName;

    protected ContestLiveDrillinService contestLiveDrillinService;
    protected ContestLiveAthleteService contestLiveAthleteService;

    protected Executor entryPublishExecutor;

    public BaseStatsUpdateDistributedTopic(String uniqueTopic, IFantasyPointTranslator translator, IUpdateProcessor updateProcessor,
                                           ISportsDao sportsDao, IContestDao contestDao, String socketXmlRootNodeName) {
        super(uniqueTopic);

        this.translator = translator;
        this.updateProcessor = updateProcessor;
        this.sportsDao = sportsDao;
        this.contestDao = contestDao;
        this.edgeCacheService = new EdgeCacheService();
        this.socketXmlRootNodeName = socketXmlRootNodeName;

        saxParsingHandler = new StatsSaxParsingHandler(socketXmlRootNodeName);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            Logger.error("Failed to initialized sax parser", e);
            e.printStackTrace();
        }

        contestLiveAthleteService = DistributedServices.getContext().getBean("ContestLiveAthleteManager", ContestLiveAthleteService.class);
        contestLiveDrillinService = DistributedServices.getContext().getBean("ContestLiveDrillinManager", ContestLiveDrillinService.class);

        entryPublishExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void handleMessage(Message<String> message) {
        /*
         * Ensure that only the master node is executing this update.
         */
        if(DistributedServices.isMaster()) {
            process(message.getMessageObject());
        }
    }

    @Override
    public void process(String message) {
        // Ignore pings
        if (message.startsWith("Ping")) {
            return;
        }

        sb.append(message);
        if(!message.equals("</" + socketXmlRootNodeName + ">")) {
            return;
        }

        try {
            saxParsingHandler.getXmlDocs().clear();
            saxParser.parse(new ByteArrayInputStream(sb.toString().getBytes()), saxParsingHandler);
            sb.setLength(0);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        List<String> xmlDocs = saxParsingHandler.getXmlDocs();

        /*
         * Delegate to a promise so long stat corrections don't hog up the processing thread.
         */
        promise(() -> {
            for(String xml: xmlDocs) {

                long procStart = System.currentTimeMillis();
                FantasyPointUpdateEvent fantasyPointUpdateEvent = updateProcessor.process(xml);
                long procEnd = System.currentTimeMillis();
                Logger.info("UpdateProcessor execution took " + (procEnd-procStart)/1000.0 + " seconds");

                if (fantasyPointUpdateEvent == null) {
                    continue;
                }

                /*
                 * Publish changes to athlete indicators.
                 */
                publishGeneralAthleteChanges(fantasyPointUpdateEvent.getIndicators(), fantasyPointUpdateEvent.getSportEvent());

                Map<Lineup, BigDecimal> lineupPoints = new HashMap<>();

                SportEvent sportEvent = fantasyPointUpdateEvent.getSportEvent();
                for (FantasyPointAthleteUpdateEvent event : fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList()) {
                    AthleteSportEventInfo athleteSportEventInfo = event.getAthleteSportEventInfo();
                    Athlete athlete = athleteSportEventInfo.getAthlete();

                    /*
                     * Skip to the next update if the associated athlete isn't set up for fantasy rosters or if the
                     * update doesn't have any effect, meaning their fantasy points remain the same, as long as the
                     * incoming message is not a stat correction.
                     */
                    if (!fantasyPointUpdateEvent.isStatCorrection() && (event.getFantasyPointDelta() == null || event.getFantasyPointDelta().compareTo(BigDecimal.ZERO) == 0)) {
                        Logger.info("-------SKIPPING EVENT " + fantasyPointUpdateEvent.getEventDescription());
                        continue;
                    }

                    /*
                     * Publish AthleteSportEventInfo changes
                     */
                    long startPublishASEI = System.currentTimeMillis();
                    publishAthleteSportEventInfoChanges(athleteSportEventInfo);
                    long endPublishASEI = System.currentTimeMillis();
                    Logger.info("Executing publishAthleteSportEventInfoChanges took " + (endPublishASEI-startPublishASEI)/1000.0 + " seconds");

                    Set<Lineup> uniqueLineups = new HashSet<>();
    //                long startFindEntries = System.currentTimeMillis();
                    List<Entry> entries = DaoFactory.getContestDao().findEntries(athleteSportEventInfo);
    //                long endFindEntries = System.currentTimeMillis();
    //                Logger.info("Executing findEntries took " + (endFindEntries - startFindEntries)/1000.0 + " seconds.  Found " + entries.size() + " entries.");

                    performCacheEvictionsByEntry(entries, athleteSportEventInfo);

                    long totalUpdateEntryFPs = 0;
                    long totalEviction = 0;
                    long totalPublishEntry = 0;
                    long startIterateEntries = System.currentTimeMillis();

                    /*
                     * Calculate the new point total for the entry by adding the delta in
                     * the FantasyPointUpdateEvent to the current point total for the entry.
                     */
                    long startUpdateEntryFPs = System.currentTimeMillis();
                    contestDao.updateEntryFantasyPoints(entries);
    //                    entry.updateEntryFantasyPoints();
                    long endUpdateEntryFPs = System.currentTimeMillis();
                    totalUpdateEntryFPs += endUpdateEntryFPs - startUpdateEntryFPs;

                    // Pull entries again because their fantasy point values changed.
                    long startFindEntries = System.currentTimeMillis();
                    entries = DaoFactory.getContestDao().findEntries(athleteSportEventInfo);
                    long endFindEntries = System.currentTimeMillis();
                    Logger.info("Executing findEntries took " + (endFindEntries - startFindEntries)/1000.0 + " seconds.  Found " + entries.size() + " entries.");

                    for (Entry entry : entries) {
                        /*
                         * publish entry to topic.
                         */
                        long startPublishEntry = System.currentTimeMillis();
                        publishEntryChanges(entry);
                        long endPublishEntry = System.currentTimeMillis();
                        totalPublishEntry += endPublishEntry-startPublishEntry;

                        uniqueLineups.add(entry.getLineup());
                    }
                    long endIterateEntries = System.currentTimeMillis();
                    Logger.info("Executing iteration of entries took " + (endIterateEntries - startIterateEntries)/1000.0 + " seconds.");
                    Logger.info("          Executing entry eviction in " + (totalEviction)/1000.0 + " seconds.");
                    Logger.info("          Executing update entries in " + (totalUpdateEntryFPs)/1000.0 + " seconds.");
                    Logger.info("          Executing publish entries in " + (totalPublishEntry)/1000.0 + " seconds.");

                    for(Lineup lineup: uniqueLineups) {
                        BigDecimal increment = lineupPoints.get(lineup);
                        if(increment == null) {
                            increment = BigDecimal.ZERO;
                        }
                        lineupPoints.put(lineup, increment.add(event.getFantasyPointDelta()));
                    }
                }

                /*
                 * Update performance data for lineups.
                 */
                if(Play.application().configuration().getBoolean("graphUpdates")) {
                    long startLineup = System.currentTimeMillis();
                    for (Map.Entry<Lineup, BigDecimal> entry : lineupPoints.entrySet()) {
                        Lineup lineup = entry.getKey();
                        BigDecimal points = entry.getValue();
                        try {
                            lineup.updatePerformanceData(points, fantasyPointUpdateEvent.getCurrentUnitOfTime());
                            contestDao.saveLineup(lineup);
                        } catch (Exception e) {
                            Logger.error("Unable to update performance data for lineup " + lineup.getId() + ": " + e.getMessage());
                        }
                    }
                    long endLineup = System.currentTimeMillis();
                    Logger.info("Executing updatePerformanceData took " + (endLineup - startLineup) / 1000.0 + " seconds.");
                }

                /*
                 * publish lineups to topic.
                 */
                long startPublishLineupChanges = System.currentTimeMillis();
                publishLineupChanges(lineupPoints.keySet());
                long endPublishLineupChanges = System.currentTimeMillis();
                Logger.info("Executing publishLineupChanges on " + lineupPoints.keySet().size() + " took " + (endPublishLineupChanges-startPublishLineupChanges)/1000.0 + " seconds.");

                /*
                 * publish sport event to topic.
                 */
                edgeCacheService.evict(Arrays.asList(sportEvent));
                publishSportEventChanges(sportEvent);

                return null;

            }
            long end = System.currentTimeMillis();
            Logger.info("Executing Stats Update took " + (end - start) / 1000.0 + " seconds");

            return null;
        });
    }

    private void performCacheEvictionsByEntry(List<Entry> entries, AthleteSportEventInfo athleteSportEventInfo) {
        /*
         * Perform evictions for Spring cache.
         */
        promise(() -> {
            for(Entry entry: entries) {
                edgeCacheService.evict(entry, athleteSportEventInfo);
            }

            return null;
        });
    }

    @Override
    public void publishAthleteSportEventInfoChanges(AthleteSportEventInfo athleteSportEventInfo) {
        try {
            String topicLabelAthletes = GlobalConstants.FANTASY_POINT_UPDATE_ATHLETE + athleteSportEventInfo.getId();
            String topicLabelDrillin = GlobalConstants.FANTASY_POINT_UPDATE_ATHLETE_DRILLIN + athleteSportEventInfo.getId();

            String jsonForDrillin = contestLiveDrillinService.getAthleteSportEventInfoUpdateAsJson(athleteSportEventInfo);
            String jsonForAthletes = contestLiveAthleteService.createJsonForAthleteSportEventInfo(null, athleteSportEventInfo, false);

            DistributedServices.getInstance().getTopic(topicLabelDrillin).publish(jsonForDrillin);
            DistributedServices.getInstance().getTopic(topicLabelAthletes).publish(jsonForAthletes);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void publishEntryChanges(Entry entry) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    String topicLabel = GlobalConstants.FANTASY_POINT_UPDATE_ENTRY + entry.getId();

                    String jsonForEntry = contestLiveDrillinService.getEntryUpdateAsJson(entry);

                    DistributedServices.getInstance().getTopic(topicLabel).publish(/*mapper.writeValueAsString(entry)*/ jsonForEntry);
                } catch (JsonProcessingException e) {
                    Logger.error(e.getMessage());
                }
            }
        };
        entryPublishExecutor.execute(r);
    }

    @Override
    public void publishEntryChangesForContest(Set<Contest> uniqueContests) {
        for (Contest contest : uniqueContests) {
            try {
                String topicLabel = GlobalConstants.FANTASY_POINT_UPDATE_CONTEST + contest.getId();
                DistributedServices.getInstance().getTopic(topicLabel).publish(mapper.writeValueAsString(contest));
            } catch (JsonProcessingException e) {
                Logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void publishLineupChanges(Set<Lineup> uniqueLineups) {
        for (Lineup lineup : uniqueLineups) {
            try {
                String topicLabel = GlobalConstants.FANTASY_POINT_UPDATE_LINEUP + lineup.getId();
                DistributedServices.getInstance().getTopic(topicLabel).publish(mapper.writeValueAsString(lineup));
            } catch (JsonProcessingException e) {
                Logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void publishSportEventChanges(SportEvent sportEvent) {
        try {
            String topicLabel = GlobalConstants.FANTASY_POINT_UPDATE_SPORT_EVENT + sportEvent.getId();

            String json = contestLiveDrillinService.getSportEventUpdateAsJson(sportEvent);

            DistributedServices.getInstance().getTopic(topicLabel).publish(/*mapper.writeValueAsString(sportEvent)*/ json);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void publishGeneralAthleteChanges(Map<Integer, Integer> indicators, SportEvent sportEvent) {
        for(Map.Entry<Integer, Integer> entry: indicators.entrySet()) {
            Map<String, Object> update = new HashMap<>();
            update.put("athleteSportEventInfoId", entry.getKey());
            update.put("indicator", entry.getValue());
            update.put("unitsRemaining", sportEvent.getUnitsRemaining());

            String topicLabel = GlobalConstants.ATHLETE_GENERAL_UPDATE_TOPIC + entry.getKey();
            try {
                String json = mapper.writeValueAsString(update);
                DistributedServices.getInstance().getTopic(topicLabel).publish(json);
            } catch (JsonProcessingException e) {
                Logger.error(e.getMessage());
            }
        }
    }
}
