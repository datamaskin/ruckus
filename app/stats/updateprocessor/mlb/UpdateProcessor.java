package stats.updateprocessor.mlb;

import service.ScoringRulesService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ISportsDao;
import dao.IStatsDao;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.Position;
import models.sports.SportEvent;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import play.Logger;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.BaseUpdateProcessor;
import stats.updateprocessor.FantasyPointAthleteUpdateEvent;
import stats.updateprocessor.FantasyPointUpdateEvent;
import utils.ITimeService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by dmaclean on 7/17/14.
 */
public class UpdateProcessor extends BaseUpdateProcessor {

    private static final String GAMECODE_GLOBAL_ID = "//MLB-event/gamecode/@global-id";
    private static final String GAME_STATUS = "//MLB-event/gamestate/game/@status";
    private static final String EVENT_DETAILS = "//MLB-event/event-details";
    private static final String EVENT_DETAILS_CODE = "//MLB-event/event-details/event/@code";
    private static final String EVENT_DESCRIPTION = "//MLB-event/event-details/description/@text";
    //    private static final String TOTAL_INNINGS = "//MLB-event/baseball-mlb-boxscore-home-team-stats/baseball-mlb-boxscore-team-stats/total-innings/@total";
    private static final String MESSAGE_TYPE = "//MLB-event/event-details/event/@name";
    private static final String TOTAL_INNINGS = "//MLB-event/gamestate/game/@segment-number";
    private static final String HOME_SCORE = "//MLB-event/home-score[@type-id=1]/@number";
    private static final String AWAY_SCORE = "//MLB-event/visiting-score[@type-id=1]/@number";
    private static final String BOX_SCORE_PITCHER_AGGREGATE = "baseball-mlb-boxscore-pitching-lineup";
    private static final String BOX_SCORE_HOME_PITCHER = "//MLB-event/baseball-mlb-boxscore-home-team-pitching-lineup/" + BOX_SCORE_PITCHER_AGGREGATE;
    private static final String BOX_SCORE_VISITOR_PITCHER = "//MLB-event/baseball-mlb-boxscore-visiting-team-pitching-lineup/" + BOX_SCORE_PITCHER_AGGREGATE;
    private static final String BOX_SCORE_BATTER_AGGREGATE = "baseball-mlb-boxscore-batting-lineup";
    private static final String BOX_SCORE_HOME_BATTER = "//MLB-event/baseball-mlb-boxscore-home-team-batting-lineup/" + BOX_SCORE_BATTER_AGGREGATE;
    private static final String BOX_SCORE_VISITOR_BATTER = "//MLB-event/baseball-mlb-boxscore-visiting-team-batting-lineup/" + BOX_SCORE_BATTER_AGGREGATE;

    private static final int PITCHER_TYPE = 1;
    private static final int BATTER_TYPE = 2;
    private static ObjectMapper mapper = new ObjectMapper();
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static XPathFactory xpathFactory = XPathFactory.newInstance();
    private List<AthleteSportEventInfo> athleteSportEventInfoUpdateList = new ArrayList<>();

    private static XPath xpath = xpathFactory.newXPath();

    private String[] aggregates = {
            BOX_SCORE_HOME_BATTER,
            BOX_SCORE_VISITOR_BATTER,
            BOX_SCORE_HOME_PITCHER,
            BOX_SCORE_VISITOR_PITCHER
    };

    private String[] statsForPitchers = {
            GlobalConstants.SCORING_MLB_WIN_LABEL,
            GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL,
            GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL,
            GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL,
            GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL,
            GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL,
            GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL,
            GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL,
    };

    private String[] statsForBatters = {
            GlobalConstants.SCORING_MLB_HOMERUN_LABEL,
            GlobalConstants.SCORING_MLB_RUN_LABEL,
            GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL,
            GlobalConstants.SCORING_MLB_TRIPLE_LABEL,
            GlobalConstants.SCORING_MLB_DOUBLE_LABEL,
            GlobalConstants.SCORING_MLB_SINGLE_LABEL,
            GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL,
            GlobalConstants.SCORING_MLB_WALK_LABEL,
            GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL,
            GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL
    };

    public UpdateProcessor(ScoringRulesService scoringRulesManager, IStatsDao statsDao, ISportsDao sportsDao, IFantasyPointTranslator fantasyPointTranslator, ITimeService timeService) {
        super(scoringRulesManager, statsDao, sportsDao, fantasyPointTranslator, timeService);
    }

    @Override
    public FantasyPointUpdateEvent process(String statData) {
        DocumentBuilder documentBuilder;

        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
        Integer eventId = 0;
        try {
            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            int gameId = Integer.parseInt(xpath.evaluate(GAMECODE_GLOBAL_ID, doc));
            SportEvent sportEvent = sportsDao.findSportEvent(gameId);
            fantasyPointUpdateEvent.setSportEvent(sportEvent);

            /*
             * Record the stats update for later use by the simulator.
             */
            recordStatsUpdate(statData, gameId);

            if (!shouldProcessMessage(doc)) {
                return null;
            }

            /*
             * If the message is indicating a completed game, update the status of the SportEvent.
             */
            updateSportEventStatus(doc, fantasyPointUpdateEvent);

            updateIndicators(fantasyPointUpdateEvent, doc);

            processEventDetails(fantasyPointUpdateEvent, doc);

            /*
             * Extract the event description.
             */
            String eventDescription = xpath.evaluate(EVENT_DESCRIPTION, doc);
            fantasyPointUpdateEvent.setEventDescription(eventDescription);

            /*
             * Determine the time remaining in the sport event.
             */
            int currentUnitOfTime = updateUnitsRemaining(sportEvent, doc);
            fantasyPointUpdateEvent.setCurrentUnitOfTime(currentUnitOfTime);

            /*
             Update the score for the game.
             */
            int[] gameScore = extractGameScore(doc);
            sportsDao.updateGameScore(gameScore, sportEvent);
            fantasyPointUpdateEvent.setHomeScore(gameScore[0]);
            fantasyPointUpdateEvent.setAwayScore(gameScore[1]);

			/*
             * Iterate over each of the four possible aggregates that can contain box scores.  For each one
			 * extract all athletes and create a FantasyPointUpdateEvent.
			 */
            for (int i = 0; i < aggregates.length; i++) {
                NodeList players = (NodeList) xpath.evaluate(aggregates[i], doc, XPathConstants.NODESET);
                for (int j = 0; j < players.getLength(); j++) {
                    Node n = players.item(j);
                    if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
                        int type = BATTER_TYPE;
                        if (aggregates[i].equals(BOX_SCORE_HOME_PITCHER) || aggregates[i].equals(BOX_SCORE_VISITOR_PITCHER)) {
                            type = PITCHER_TYPE;
                        }

                        Element player = (Element) n;
                        updateAthleteBoxScore(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList(), doc, player, type);
                    }
                }
            }
        } catch (SAXException e) {
            Logger.error("Error parsing incoming MLB stat update - " + e.getMessage());
        } catch (IOException | JSONException | ParserConfigurationException | XPathExpressionException e) {
            Logger.error("Non-parsing error for incoming MLB stat update - " + e.getMessage());
        }

        /*
         * Update all the AthleteSportEventInfos that we've collected data for.
         */
        for (FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent : fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList()) {
            AthleteSportEventInfo athleteSportEventInfo = sportsDao.findAthleteSportEventInfo(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
            if (fantasyPointAthleteUpdateEvent.getFantasyPoints() != null) {
                athleteSportEventInfo.setFantasyPoints(fantasyPointAthleteUpdateEvent.getFantasyPoints());
            }

            if (fantasyPointAthleteUpdateEvent.getBoxscore() != null) {
                athleteSportEventInfo.setStats(fantasyPointAthleteUpdateEvent.getBoxscore());
            }

            if (fantasyPointAthleteUpdateEvent.getTimeline() != null) {
                athleteSportEventInfo.setTimeline(fantasyPointAthleteUpdateEvent.getTimeline());
            }

            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
        }

        SportEvent sportEventToUpdate = sportsDao.findSportEvent(fantasyPointUpdateEvent.getSportEvent().getStatProviderId());
        sportEventToUpdate.setComplete(fantasyPointUpdateEvent.getSportEvent().isComplete());
        sportEventToUpdate.setUnitsRemaining(fantasyPointUpdateEvent.getSportEvent().getUnitsRemaining());
        Logger.info("There are now " + sportEventToUpdate.getUnitsRemaining() + " innings left in " + sportEventToUpdate.getTeams().get(0).getAbbreviation() + "/" + sportEventToUpdate.getTeams().get(1).getAbbreviation());
        sportsDao.saveSportEvent(sportEventToUpdate);

        return fantasyPointUpdateEvent;
    }

    @Override
    public boolean shouldProcessMessage(Document document) throws XPathExpressionException {
        int eventId;

        try {
            String gameStatus = xpath.evaluate(GAME_STATUS, document);
            if (gameStatus != null && gameStatus.equals("Final")) {
                return true;
            }

            String eventName = xpath.evaluate(MESSAGE_TYPE, document);
            if (eventName.equals("Lineup Change")) {
                return false;
            }

            eventId = Integer.parseInt(xpath.evaluate("//MLB-event/event-details/event/@id", document));
        } catch (NumberFormatException e) {
            return false;
        }

        return !(eventId == 1 || eventId == 9);
    }

    @Override
    public void updateSportEventStatus(Document doc, FantasyPointUpdateEvent fantasyPointUpdateEvent) throws XPathExpressionException {
        XPath xpath = xpathFactory.newXPath();
        String status = xpath.evaluate(GAME_STATUS, doc);
        if (status.equals(GlobalConstants.STATS_INC_GAME_STATUS_FINAL)) {
            SportEvent sportEvent = fantasyPointUpdateEvent.getSportEvent();

            if (sportEvent != null) {
                sportEvent.setComplete(true);
                sportsDao.saveSportEvent(sportEvent);
            }
        }
    }

    @Override
    public void updateTimeline(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, String eventDescription, String playId, boolean isStatCorrection) throws IOException {
        for (FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent : fantasyPointAthleteUpdateEvents) {
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {
            };
            List<Map<String, Object>> timeline;
            if (fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getTimeline() != null) {
                timeline = mapper.readValue(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getTimeline(), typeRef);
            } else {
                timeline = new ArrayList<>();
            }
            BigDecimal delta = fantasyPointAthleteUpdateEvent.getFantasyPointDelta();
            if (delta == null || delta.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", Date.from(timeService.getNow()).getTime());
            data.put("description", eventDescription);

            String fpChange = String.format("%s%s", (delta.compareTo(new BigDecimal("0")) > 0) ? "+" : "", delta);
            if (fpChange.endsWith("0")) {
                fpChange = fpChange.substring(0, fpChange.length() - 1);
            }

            data.put("fpChange", fpChange);
            data.put("athleteSportEventInfoId", fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
            timeline.add(0, data);
            fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(timeline));
        }
    }

    @Override
    public void updateFantasyPointChange(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, Document document, Map<String, Object> extraData) throws XPathExpressionException {
        if(fantasyPointAthleteUpdateEvents.isEmpty()) {
            return;
        }

        int eventId = Integer.parseInt(xpath.evaluate("//MLB-event/event-details/event/@id", document));
        int outsBefore = Integer.parseInt(xpath.evaluate("//MLB-event/event-details/event/@outs-bef", document));
        int outsAfter = Integer.parseInt(xpath.evaluate("//MLB-event/event-details/event/@outs-aft", document));
        int rbi = Integer.parseInt(xpath.evaluate("//MLB-event/event-details/event/@rbi", document));

        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("outsBefore", outsBefore);
        eventData.put("outsAfter", outsAfter);
        eventData.put("rbi", rbi);

        List<BigDecimal> changes = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);
        if (changes.isEmpty()) {
            Logger.info("No FP deltas for event id " + eventData.get("eventId"));
        } else {
            for (FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent : fantasyPointAthleteUpdateEvents) {
                if (fantasyPointAthleteUpdateEvent.getType().equals(FantasyPointAthleteUpdateEvent.BATTER)) {
                    fantasyPointAthleteUpdateEvent.setFantasyPointDelta(changes.get(0));
                } else if (fantasyPointAthleteUpdateEvent.getType().equals(FantasyPointAthleteUpdateEvent.PITCHER)) {
                    fantasyPointAthleteUpdateEvent.setFantasyPointDelta(changes.get(1));
                }
            }
        }
    }

    @Override
    public void processEventDetails(FantasyPointUpdateEvent fantasyPointUpdateEvent, Document document) throws IOException, XPathExpressionException {
        String EVENT_DETAILS_PITCHER_ID = "//MLB-event/event-details/pitcher/@global-id";
        String EVENT_DETAILS_BATTER_ID = "//MLB-event/event-details/batter/@global-id";
        String EVENT_DETAILS_RUNNERS = "//MLB-event/event-details/runners";

        String eventDescription = xpath.evaluate(EVENT_DESCRIPTION, document);

        List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList();

        /*
         * Get runners
         */
        NodeList players = (NodeList) xpath.evaluate(EVENT_DETAILS_RUNNERS, document, XPathConstants.NODESET);
        for (int j = 0; j < players.getLength(); j++) {
            Node n = players.item(j);
            if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
                String runnerId = null;
                FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent;
                try {
                    runnerId = xpath.evaluate("//runner/@global-id", n);
                    Athlete athlete = sportsDao.findAthlete(Integer.parseInt(runnerId));
                    AthleteSportEventInfo runnerASEI = sportsDao.findAthleteSportEventInfo(athlete, fantasyPointUpdateEvent.getSportEvent());

                    fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
                    fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(runnerASEI);
                    fantasyPointAthleteUpdateEvent.setType(FantasyPointAthleteUpdateEvent.RUNNER);
                    fantasyPointAthleteUpdateEvents.add(fantasyPointAthleteUpdateEvent);
                } catch (Exception e) {
                    Logger.warn("Unable to parse runner id " + runnerId);
                }
            }
        }

        /*
         * Get pitcher
         */
        String pitcherId = null;
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEventPitcher;
        try {
            pitcherId = xpath.evaluate(EVENT_DETAILS_PITCHER_ID, document);
            Athlete pitcherAthlete = sportsDao.findAthlete(Integer.parseInt(pitcherId));
            AthleteSportEventInfo pitcherASEI = sportsDao.findAthleteSportEventInfo(pitcherAthlete, fantasyPointUpdateEvent.getSportEvent());

            fantasyPointAthleteUpdateEventPitcher = new FantasyPointAthleteUpdateEvent();
            fantasyPointAthleteUpdateEventPitcher.setAthleteSportEventInfo(pitcherASEI);
            fantasyPointAthleteUpdateEventPitcher.setType(FantasyPointAthleteUpdateEvent.PITCHER);
            fantasyPointAthleteUpdateEvents.add(fantasyPointAthleteUpdateEventPitcher);
        } catch (Exception e) {
            Logger.warn("Unable to parse pitcher id " + pitcherId);
        }

        /*
         * Get batter
         */
        String batterId;
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEventBatter;
        try {
            batterId = xpath.evaluate(EVENT_DETAILS_BATTER_ID, document);
            Athlete batterAthlete = sportsDao.findAthlete(Integer.parseInt(batterId));
            AthleteSportEventInfo batterASEI = sportsDao.findAthleteSportEventInfo(batterAthlete, fantasyPointUpdateEvent.getSportEvent());

            fantasyPointAthleteUpdateEventBatter = new FantasyPointAthleteUpdateEvent();
            fantasyPointAthleteUpdateEventBatter.setAthleteSportEventInfo(batterASEI);
            fantasyPointAthleteUpdateEventBatter.setType(FantasyPointAthleteUpdateEvent.BATTER);
            fantasyPointAthleteUpdateEvents.add(fantasyPointAthleteUpdateEventBatter);
        } catch (Exception e) {
            Logger.warn("Unable to parse batter id " + pitcherId);
        }

        /*
         * Determine fantasy point change.
         */
        updateFantasyPointChange(fantasyPointAthleteUpdateEvents, document, null);
        updateTimeline(fantasyPointAthleteUpdateEvents, eventDescription, null, false);
    }

    @Override
    public void updateIndicators(FantasyPointUpdateEvent fantasyPointUpdateEvent, Document document) throws XPathExpressionException {
        String GAME_STATE_PITCHER_ID = "//MLB-event/gamestate/pitcher/@global-id";
        String GAME_STATE_BATTER_ID = "//MLB-event/gamestate/batter/@global-id";
        String GAME_STATE_RUNNER1_ID = "//MLB-event/gamestate/runner[1]/@global-id";
        String GAME_STATE_RUNNER2_ID = "//MLB-event/gamestate/runner[2]/@global-id";
        String GAME_STATE_RUNNER3_ID = "//MLB-event/gamestate/runner[3]/@global-id";

        int homeTeam = getHomeAwayTeam("home", document);
        int awayTeam = getHomeAwayTeam("visiting", document);
        boolean homeTeamBatting = isTeamBatting("home", document);
        boolean awayTeamBatting = isTeamBatting("away", document);

        /*
         * Reset indicators for all athletes in this sport event.
         */
        Map<Integer, Integer> indicators = fantasyPointUpdateEvent.getIndicators();
        List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(fantasyPointUpdateEvent.getSportEvent());
        for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfoList) {
            int indicator = GlobalConstants.INDICATOR_TEAM_OFF_FIELD;
            if (!isFinalBoxscoreMessage(document) && ((athleteSportEventInfo.getAthlete().getTeam().getStatProviderId() == homeTeam && homeTeamBatting) ||
                    (athleteSportEventInfo.getAthlete().getTeam().getStatProviderId() == awayTeam && awayTeamBatting))
                    && !athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.BS_PITCHER)) {
                indicator = GlobalConstants.INDICATOR_TEAM_ON_FIELD;
            }

            indicators.put(athleteSportEventInfo.getAthlete().getStatProviderId(), indicator);

            athleteSportEventInfo.setIndicator(indicator);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
        }

        /*
         * Set indicators for all athletes participating in the play.
         */
        if (!isFinalBoxscoreMessage(document)) {
            String[] ids = {
                    xpath.evaluate(GAME_STATE_PITCHER_ID, document),
                    xpath.evaluate(GAME_STATE_BATTER_ID, document),
                    xpath.evaluate(GAME_STATE_RUNNER1_ID, document),
                    xpath.evaluate(GAME_STATE_RUNNER2_ID, document),
                    xpath.evaluate(GAME_STATE_RUNNER3_ID, document)
            };

            for (int i = 0; i < ids.length; i++) {
                if (ids[i].equals("")) {
                    continue;
                }

                Athlete athlete = sportsDao.findAthlete(Integer.parseInt(ids[i]));
                AthleteSportEventInfo athleteSportEventInfo = null;
                try {
                    athleteSportEventInfo = sportsDao.findAthleteSportEventInfo(athlete, fantasyPointUpdateEvent.getSportEvent());
                } catch (Exception e) {
                    Logger.warn(e.getMessage());
                    continue;
                }
                indicators.put(athleteSportEventInfo.getAthlete().getStatProviderId(), GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);

                athleteSportEventInfo.setIndicator(GlobalConstants.INDICATOR_SCORING_OPPORTUNITY);
                sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
            }
        }
    }

    @Override
    public int updateUnitsRemaining(SportEvent sportEvent, Document document) throws XPathExpressionException {
        int currentUnitOfTime = Integer.parseInt(xpath.evaluate(TOTAL_INNINGS, document));
        int unitsRemaining = 9 - currentUnitOfTime;
        unitsRemaining = unitsRemaining < 0 ? 0 : unitsRemaining;
        if (sportEvent != null) {
            Logger.info(String.format("Time units remaining updated - There are now %s innings left in the %s/%s game",
                    sportEvent.getUnitsRemaining(), sportEvent.getTeams().get(0).getAbbreviation(), sportEvent.getTeams().get(1).getAbbreviation()));

            sportEvent.setUnitsRemaining(unitsRemaining);
        }
        return currentUnitOfTime;
    }

    @Override
    public int[] extractGameScore(Document doc) throws XPathExpressionException {
        int homeScore = Integer.parseInt(xpath.evaluate(HOME_SCORE, doc));
        int awayScore = Integer.parseInt(xpath.evaluate(AWAY_SCORE, doc));

        return new int[]{homeScore, awayScore};
    }

    @Override
    public void updateAthleteBoxScore(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, Document doc, Element e, int type)
            throws XPathExpressionException, JSONException, IOException{

        BigDecimal fantasyPoints = new BigDecimal("0.0");

        String xpathPrefix = String.format("//%s", (type == PITCHER_TYPE) ? BOX_SCORE_PITCHER_AGGREGATE : BOX_SCORE_BATTER_AGGREGATE);

        /*
         * Determine the athlete id
         */
        int athleteId = Integer.parseInt(xpath.evaluate(xpathPrefix + "/player-code/@global-id", e));

        List<Map<String, Object>> boxScore = new ArrayList<>();
        Map<String, BigDecimal> boxScoreValues = new LinkedHashMap<>();

        if (type == PITCHER_TYPE) {
            /*
             * Determine how many of each stat the pitcher has accumulated.
             */
            BigDecimal inningsPitched = new BigDecimal(xpath.evaluate(xpathPrefix + "/innings-pitched/@innings", e));
            BigDecimal strikeouts = new BigDecimal(xpath.evaluate(xpathPrefix + "/strike-outs/@strike-outs", e));
            BigDecimal earnedRuns = new BigDecimal(xpath.evaluate(xpathPrefix + "/earned-runs/@earned-runs", e));
            BigDecimal hits = new BigDecimal(xpath.evaluate(xpathPrefix + "/hits/@hits", e));
            BigDecimal walks = new BigDecimal(xpath.evaluate(xpathPrefix + "/walks/@walks", e));
            BigDecimal hitBatsmen = new BigDecimal(xpath.evaluate(xpathPrefix + "/hit-batsmen/@number", e));
            boolean winningPitcher = xpath.evaluate(xpathPrefix + "/winning-pitcher/@winning-pitcher", e).equals("true");
            boolean completeGame = xpath.evaluate(xpathPrefix + "/complete-game/@complete-game", e).equals("true");

            /*
             * Put the stat values in a map keyed off the stat name so we can dynamically refer to them.
             */
            boxScoreValues.put(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL, inningsPitched);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL, strikeouts);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL, earnedRuns);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL, hits);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL, walks);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL, hitBatsmen);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_WIN_LABEL, winningPitcher ? new BigDecimal("1") : new BigDecimal("0"));
            boxScoreValues.put(GlobalConstants.SCORING_MLB_COMPLETE_GAME_LABEL, completeGame ? new BigDecimal("1") : new BigDecimal("0"));

            /**
             * Go through all the stats that we have, calculate and sum the fantasy points and generate a box score.
             */
            for (int i = 0; i < statsForPitchers.length; i++) {
                Map<String, BigDecimal> currStatMap = new HashMap<>();
                currStatMap.put(statsForPitchers[i], boxScoreValues.get(statsForPitchers[i]));

                BigDecimal fantasyPointsForStat = fantasyPointTranslator.calculateFantasyPoints(currStatMap);
                fantasyPointsForStat = fantasyPointsForStat.setScale(2, RoundingMode.HALF_EVEN);

                Map<String, Object> boxScoreMap = new HashMap<>();
                boxScoreMap.put(BOXSCORE_JSON_FIELD_NAME, statsForPitchers[i]);
                boxScoreMap.put(BOXSCORE_JSON_FIELD_ABBR, GlobalConstants.SCORING_MLB_NAME_TO_ABBR_MAP.get(statsForPitchers[i]));
                boxScoreMap.put(BOXSCORE_JSON_FIELD_AMOUNT, boxScoreValues.get(statsForPitchers[i]));
                boxScoreMap.put(BOXSCORE_JSON_FIELD_FPP, fantasyPointsForStat);
                boxScore.add(boxScoreMap);
            }
        } else {
            /*
             * Determine how many of each stat the batter has accumulated.
             */
            BigDecimal doubles = new BigDecimal(xpath.evaluate(xpathPrefix + "/doubles/@doubles", e));
            BigDecimal triples = new BigDecimal(xpath.evaluate(xpathPrefix + "/triples/@triples", e));
            BigDecimal homeruns = new BigDecimal(xpath.evaluate(xpathPrefix + "/home-runs/@home-runs", e));
            BigDecimal hits = new BigDecimal(xpath.evaluate(xpathPrefix + "/hits/@hits", e));
            BigDecimal singles = new BigDecimal(hits.intValue() - (doubles.intValue() + triples.intValue() + homeruns.intValue()));
            BigDecimal rbis = new BigDecimal(xpath.evaluate(xpathPrefix + "/runs-batted-in/@runs-batted-in", e));
            BigDecimal runs = new BigDecimal(xpath.evaluate(xpathPrefix + "/runs/@runs", e));
            BigDecimal walks = new BigDecimal(xpath.evaluate(xpathPrefix + "/walks/@walks", e));
            BigDecimal hitByPitch = new BigDecimal(xpath.evaluate(xpathPrefix + "/hit-by-pitch/@number", e));
            BigDecimal stolenBases = new BigDecimal(xpath.evaluate(xpathPrefix + "/stolen-bases/@stolen-bases", e));
            BigDecimal caughtStealing = new BigDecimal(xpath.evaluate(xpathPrefix + "/caught-stealing/@caught-stealing", e));

            /*
             * Put the stat values in a map keyed off the stat name so we can dynamically refer to them.
             */
            boxScoreValues.put(GlobalConstants.SCORING_MLB_DOUBLE_LABEL, doubles);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_TRIPLE_LABEL, triples);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_HOMERUN_LABEL, homeruns);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_HIT_LABEL, hits);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_SINGLE_LABEL, singles);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL, rbis);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_RUN_LABEL, runs);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_WALK_LABEL, walks);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL, hitByPitch);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_STOLEN_BASE_LABEL, stolenBases);
            boxScoreValues.put(GlobalConstants.SCORING_MLB_CAUGHT_STEALING_LABEL, caughtStealing);

            /**
             * Go through all the stats that we have, calculate and sum the fantasy points and generate a box score.
             */
            for (int i = 0; i < statsForBatters.length; i++) {
                Map<String, BigDecimal> currStatMap = new HashMap<>();
                currStatMap.put(statsForBatters[i], boxScoreValues.get(statsForBatters[i]));

                BigDecimal fantasyPointsForStat = fantasyPointTranslator.calculateFantasyPoints(currStatMap);

                Map<String, Object> boxScoreMap = new HashMap<>();
                boxScoreMap.put(BOXSCORE_JSON_FIELD_NAME, statsForBatters[i]);
                boxScoreMap.put(BOXSCORE_JSON_FIELD_ABBR, GlobalConstants.SCORING_MLB_NAME_TO_ABBR_MAP.get(statsForBatters[i]));
                boxScoreMap.put(BOXSCORE_JSON_FIELD_AMOUNT, boxScoreValues.get(statsForBatters[i]));
                boxScoreMap.put(BOXSCORE_JSON_FIELD_FPP, fantasyPointsForStat);
                boxScore.add(boxScoreMap);
            }
        }

        for (FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent : fantasyPointAthleteUpdateEvents) {
            if (fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getAthlete().getStatProviderId() == athleteId) {
                // Set fantasy points
                fantasyPoints = fantasyPointTranslator.calculateFantasyPoints(boxScoreValues);
                fantasyPointAthleteUpdateEvent.setFantasyPoints(fantasyPoints);

                // Set box score
                String boxScoreString = mapper.writeValueAsString(boxScore);
                fantasyPointAthleteUpdateEvent.setBoxscore(boxScoreString);
            }
        }
    }

    /**
     * Determine the Stats id of the home or away team.
     *
     * @param type     Indicates whether we want the home or visiting team.
     * @param document The XML document to search.
     * @return The Stats is of the team we want.
     * @throws XPathExpressionException
     */
    private int getHomeAwayTeam(String type, Document document) throws XPathExpressionException {
        return Integer.parseInt(xpath.evaluate(String.format("//MLB-event/%s-team/team-code/@global-id", type), document));
    }

    /**
     * Determine if the provided team (home or visitor) is batting.
     *
     * @param type     Indicates whether we want the home or visiting team.
     * @param document The XML document to search.
     * @return True, if the specified team is b
     * @throws XPathExpressionException
     */
    private boolean isTeamBatting(String type, Document document) throws XPathExpressionException {
        return xpath.evaluate(String.format("//MLB-event/%s-team/due-up/@due", type), document).equals("false");
    }

    /**
     * Determines if the incoming message is an Event Edit message.  These are sent while the game is in
     * progress and contains multiple event detail aggregates.
     *
     * @param document      The XML document to search.
     * @return True, if the message is an Event Edit.  False, otherwise.
     * @throws XPathExpressionException
     */
//    private boolean isEventEditMessage(Document document) throws XPathExpressionException {
//        NodeList eventDetails = (NodeList) xpath.evaluate(EVENT_DETAILS, document, XPathConstants.NODESET);
//        Logger.info("isEventEditMessage - # event details aggregates is " + eventDetails.getLength() );
//        String gameStatus = xpath.evaluate(GAME_STATUS, document);
//
//        return !isFinalBoxscoreMessage(document) && eventDetails.getLength() > 1;
//    }

    /**
     * Determines if the incoming message is a Final Boxscore message.  These are sent when the game has completed.
     *
     * @param document The XML document to search.
     * @return True, if the message is a Final Boxscore.  False, otherwise.
     * @throws XPathExpressionException
     */
    private boolean isFinalBoxscoreMessage(Document document) throws XPathExpressionException {
        String gameStatus = xpath.evaluate(GAME_STATUS, document);
        if (gameStatus != null && gameStatus.equals("Final")) {
            return true;
        }

        return false;
    }
}
