package stats.updateprocessor.nfl;

import service.ScoringRulesService;
import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import dao.ISportsDao;
import dao.IStatsDao;
import distributed.DistributedServices;
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
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Processor for Stats socket updates for NFL.
 */
public class UpdateProcessor extends BaseUpdateProcessor {
    /** An enum to indicate if the team is at home or visiting. */
    private static enum TeamLocationState {
        HOME, VISITING
    }

    private static ObjectMapper mapper = new ObjectMapper();
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    private static final XPathExpression NFL_EVENT_GAMECODE_GLOBAL_ID ;
    private static final XPathExpression NFL_EVENT_PLAY_DETAILS;
    private static final XPathExpression NFL_EVENT_HOME_TEAM;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM;
    private static final XPathExpression NFL_EVENT_HOME_PLAYER_STATS_HOME_PLAYER;
    private static final XPathExpression NFL_EVENT_VISITING_PLAYER_STATS_VISITING_PLAYER;
    private static final XPathExpression NFL_EVENT_PLAY;
    private static final XPathExpression STAT_ID;
    private static final XPathExpression GLOBAL_PLAYER_ID;
    private static final XPathExpression DETAILS;
    private static final XPathExpression XPR_ID;
    private static final XPathExpression XPE_AWAY_SCORE_BEFORE;
    private static final XPathExpression XPE_AWAY_SCORE_AFTER;
    private static final XPathExpression XPE_HOME_SCORE_BEFORE;
    private static final XPathExpression XPE_HOME_SCORE_AFTER;
    private static final XPathExpression XPE_POSSESSION_GLOBAL_ID;
    private static final XPathExpression XPE_END_POSSESSION_GLOBAL_ID;
    private static final XPathExpression XPE_EVENT_TYPE;
    private static final XPathExpression NFL_EVENT_GAMESTATE_TEAM_POSSESSION_GLOBAL_ID;
    private static final XPathExpression NFL_EVENT_GAMESTATE_YARDS_FROM_GOAL;
    private static final XPathExpression NFL_EVENT_GAMESTATE_QUARTER;
    private static final XPathExpression NFL_EVENT_GAMESTATE_MINUTES;
    private static final XPathExpression NFL_EVENT_HOME_TEAM_LINESCORE_SCORE;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM_LINESCORE_SCORE;
    private static final XPathExpression DEFENSE_SACKS;
    private static final XPathExpression INTERCEPTION_RETURNS_ATTEMPTS;
    private static final XPathExpression OPPONENT_FUMBLES_RECOVERED;
    private static final XPathExpression KICK_RETURNS_TDS;
    private static final XPathExpression PUNT_RETURNS_TDS;
    private static final XPathExpression INTERCEPTION_RETURNS_TDS;
    private static final XPathExpression OPPONENT_FUMBLES_TDS;
    private static final XPathExpression SAFETIES_SAFETIES;
    private static final XPathExpression NFL_EVENT_HOME_TEAM_TEAM_CODE_GLOBAL_ID;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM_FIELD_GOALS_BLOCKED;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM_TEAM_CODE_GLOBAL_ID;
    private static final XPathExpression NFL_EVENT_HOME_TEAM_FIELD_GOALS_BLOCKED;
    private static final XPathExpression PLAY_ID;
    private static final XPathExpression NFL_EVENT_GAMESTATE_STATUS;
    private static final XPathExpression YARDS;
    private static final XPathExpression POINTS;
    private static final XPathExpression PLAYER_CODE_GLOBAL_ID;
    private static final XPathExpression PASSING_YARDS;
    private static final XPathExpression RUSHING_YARDS;
    private static final XPathExpression RECEIVING_YARDS;
    private static final XPathExpression PASSING_TDS;
    private static final XPathExpression RUSHING_TDS;
    private static final XPathExpression RECEIVING_TDS;
    private static final XPathExpression RECEIVING_RECEPTIONS;
    private static final XPathExpression FUMBLES_LOST;
    private static final XPathExpression PUNT_RETURNING_TDS;
    private static final XPathExpression KICK_RETURNING_TDS;
    private static final XPathExpression TWO_POINT_PASSING_COMPLETIONS;
    private static final XPathExpression TWO_POINT_RUSHING_COMPLETIONS;
    private static final XPathExpression TWO_POINT_RECEIVING_COMPLETIONS;
    private static final XPathExpression HOME_TEAM;
    private static final XPathExpression AWAY_TEAM;
    private static final XPathExpression NFL_EVENT_HOME_TEAM_SAFETIES_SAFETIES;
    private static final XPathExpression NFL_EVENT_HOME_TEAM_OPPONENT_FUMBLES_TDS;
    private static final XPathExpression NFL_EVENT_HOME_TEAM_INTERCEPTION_RETURNS_TDS;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM_SAFETIES_SAFETIES;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM_OPPONENT_FUMBLES_TDS;
    private static final XPathExpression NFL_EVENT_VISITING_TEAM_INTERCEPTION_RETURNS_TDS;


    private static final int PLAYER_TYPE_HOME_TEAM = 0;
    private static final int PLAYER_TYPE_AWAY_TEAM = 1;
    private static final int PLAYER_TYPE_NON_DEF = 2;

    static {
        try {
            final XPathFactory xpathFactory = XPathFactory.newInstance();
            final XPath xpath = xpathFactory.newXPath();

            NFL_EVENT_GAMECODE_GLOBAL_ID = xpath.compile("//nfl-event/gamecode/@global-id");
            NFL_EVENT_PLAY_DETAILS = xpath.compile("//nfl-event/play/@details");
            NFL_EVENT_HOME_TEAM = xpath.compile("//nfl-event/home-team");
            NFL_EVENT_VISITING_TEAM = xpath.compile("//nfl-event/visiting-team");
            NFL_EVENT_HOME_PLAYER_STATS_HOME_PLAYER = xpath.compile("//nfl-event/home-player-stats/home-player");
            NFL_EVENT_VISITING_PLAYER_STATS_VISITING_PLAYER = xpath.compile("//nfl-event/visiting-player-stats/visiting-player");
            NFL_EVENT_PLAY = xpath.compile("//nfl-event/play");
            STAT_ID = xpath.compile("stat-id");
            GLOBAL_PLAYER_ID = xpath.compile("@global-player-id");
            DETAILS = xpath.compile("@details");
            XPR_ID = xpath.compile("@id");
            XPE_AWAY_SCORE_BEFORE = xpath.compile("@away-score-before");
            XPE_AWAY_SCORE_AFTER = xpath.compile("@away-score-after");
            XPE_HOME_SCORE_BEFORE = xpath.compile("@home-score-before");
            XPE_HOME_SCORE_AFTER = xpath.compile("@home-score-after");
            XPE_POSSESSION_GLOBAL_ID = xpath.compile("@possession-global-id");
            XPE_END_POSSESSION_GLOBAL_ID = xpath.compile("@end-possession-global-id");
            XPE_EVENT_TYPE = xpath.compile("@event-type");
            NFL_EVENT_GAMESTATE_TEAM_POSSESSION_GLOBAL_ID = xpath.compile("//nfl-event/gamestate/@team-possession-global-id");
            NFL_EVENT_GAMESTATE_YARDS_FROM_GOAL = xpath.compile("//nfl-event/gamestate/@yards-from-goal");
            NFL_EVENT_GAMESTATE_QUARTER = xpath.compile("//nfl-event/gamestate/@quarter");
            NFL_EVENT_GAMESTATE_MINUTES = xpath.compile("//nfl-event/gamestate/@minutes");
            NFL_EVENT_HOME_TEAM_LINESCORE_SCORE = xpath.compile("//nfl-event/home-team/linescore/@score");
            NFL_EVENT_VISITING_TEAM_LINESCORE_SCORE = xpath.compile("//nfl-event/visiting-team/linescore/@score");
            DEFENSE_SACKS = xpath.compile("defense/@sacks");
            INTERCEPTION_RETURNS_ATTEMPTS = xpath.compile("interception-returns/@attempts");
            OPPONENT_FUMBLES_RECOVERED = xpath.compile("opponent-fumbles/@recovered");
            KICK_RETURNS_TDS = xpath.compile("kick-returns/@tds");
            PUNT_RETURNS_TDS = xpath.compile("punt-returns/@tds");
            INTERCEPTION_RETURNS_TDS = xpath.compile("interception-returns/@tds");
            OPPONENT_FUMBLES_TDS = xpath.compile("opponent-fumbles/@tds");
            SAFETIES_SAFETIES = xpath.compile("safeties/@safeties");
            NFL_EVENT_HOME_TEAM_TEAM_CODE_GLOBAL_ID = xpath.compile("//nfl-event/home-team/team-code/@global-id");
            NFL_EVENT_VISITING_TEAM_FIELD_GOALS_BLOCKED = xpath.compile("//nfl-event/visiting-team/field-goals/@blocked");
            NFL_EVENT_VISITING_TEAM_TEAM_CODE_GLOBAL_ID = xpath.compile("//nfl-event/visiting-team/team-code/@global-id");
            NFL_EVENT_HOME_TEAM_FIELD_GOALS_BLOCKED = xpath.compile("//nfl-event/home-team/field-goals/@blocked");
            PLAY_ID = xpath.compile("//nfl-event/play/@id");
            NFL_EVENT_GAMESTATE_STATUS = xpath.compile("//nfl-event/gamestate/@status");
            YARDS = xpath.compile("@yards");
            POINTS = xpath.compile("@points");
            PLAYER_CODE_GLOBAL_ID = xpath.compile("player-code/@global-id");
            PASSING_YARDS = xpath.compile("passing/@yards");
            RUSHING_YARDS = xpath.compile("rushing/@yards");
            RECEIVING_YARDS = xpath.compile("receiving/@yards");
            PASSING_TDS = xpath.compile("passing/@tds");
            RUSHING_TDS = xpath.compile("rushing/@tds");
            RECEIVING_TDS = xpath.compile("receiving/@tds");
            RECEIVING_RECEPTIONS = xpath.compile("receiving/@receptions");
            FUMBLES_LOST = xpath.compile("fumbles/@lost");
            PUNT_RETURNING_TDS = xpath.compile("punt-returning/@tds");
            KICK_RETURNING_TDS = xpath.compile("kick-returning/@tds");
            TWO_POINT_PASSING_COMPLETIONS = xpath.compile("two-point-passing/@completions");
            TWO_POINT_RUSHING_COMPLETIONS = xpath.compile("two-point-rushing/@completions");
            TWO_POINT_RECEIVING_COMPLETIONS = xpath.compile("two-point-receiving/@completions");
            HOME_TEAM = xpath.compile("//nfl-event/home-team/team-code/@global-id");
            AWAY_TEAM = xpath.compile("//nfl-event/visiting-team/team-code/@global-id");
            NFL_EVENT_HOME_TEAM_SAFETIES_SAFETIES = xpath.compile("//nfl-event/home-team/safeties/@safeties");
            NFL_EVENT_HOME_TEAM_OPPONENT_FUMBLES_TDS = xpath.compile("//nfl-event/home-team/opponent-fumbles/@tds");
            NFL_EVENT_HOME_TEAM_INTERCEPTION_RETURNS_TDS = xpath.compile("//nfl-event/home-team/interception-returns/@tds");
            NFL_EVENT_VISITING_TEAM_SAFETIES_SAFETIES = xpath.compile("//nfl-event/visiting-team/safeties/@safeties");
            NFL_EVENT_VISITING_TEAM_OPPONENT_FUMBLES_TDS = xpath.compile("//nfl-event/visiting-team/opponent-fumbles/@tds");
            NFL_EVENT_VISITING_TEAM_INTERCEPTION_RETURNS_TDS = xpath.compile("//nfl-event/visiting-team/interception-returns/@tds");

        } catch (final XPathExpressionException ex) {
            throw new Error("Unable to compile Stat parsing xpath expressions.", ex);
        }
    }

    public UpdateProcessor(ScoringRulesService scoringRulesManager, IStatsDao statsDao, ISportsDao sportsDao, IFantasyPointTranslator translator, ITimeService timeService) {
        super(scoringRulesManager, statsDao, sportsDao, translator, timeService);
    }

    @Override
    public FantasyPointUpdateEvent process(String statData) {
        DocumentBuilder documentBuilder;

        FantasyPointUpdateEvent fantasyPointUpdateEvent = new FantasyPointUpdateEvent();
        try {
        	// Clean up incoming XML
            statData = statData.replaceAll("&", "&amp;");

            InputSource source = new InputSource(new StringReader(statData));

            documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(source);

            int gameId = Integer.parseInt (NFL_EVENT_GAMECODE_GLOBAL_ID.evaluate(doc));
            SportEvent sportEvent = sportsDao.findSportEvent(gameId);
            fantasyPointUpdateEvent.setSportEvent(sportEvent);

            fantasyPointUpdateEvent.setStatCorrection(isStatCorrectionMessage(doc));

            if(fantasyPointUpdateEvent.isStatCorrection()) {
                Logger.info("=========== STAT CORRECTION ===========");
            }
            else {
                Logger.info("=========== NORMAL ===========");
            }

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
            String eventDescription = NFL_EVENT_PLAY_DETAILS.evaluate(doc);
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
             * Iterate over each of the  aggregates that can contain box scores.  For each one
			 * extract all athletes and create a StatsFantasyPointUpdateEvent.
			 */
            Node homeTeamNode = (Node) NFL_EVENT_HOME_TEAM.evaluate(doc, XPathConstants.NODE);
            Node visitingTeamNode = (Node) NFL_EVENT_VISITING_TEAM.evaluate(doc, XPathConstants.NODE);

            updateAthleteBoxScore(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList(), doc, (Element) homeTeamNode, PLAYER_TYPE_HOME_TEAM);
            updateAthleteBoxScore(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList(), doc, (Element) visitingTeamNode, PLAYER_TYPE_AWAY_TEAM);

            NodeList homePlayers = (NodeList) NFL_EVENT_HOME_PLAYER_STATS_HOME_PLAYER.evaluate(doc, XPathConstants.NODESET);
            for(int i=0; i<homePlayers.getLength(); i++) {
                Element homePlayer = (Element) homePlayers.item(i);
                updateAthleteBoxScore(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList(), doc, homePlayer, PLAYER_TYPE_NON_DEF);
            }

            NodeList visitingPlayers = (NodeList) NFL_EVENT_VISITING_PLAYER_STATS_VISITING_PLAYER.evaluate(doc, XPathConstants.NODESET);
            for(int i=0; i<visitingPlayers.getLength(); i++) {
                Element visitingPlayer = (Element) visitingPlayers.item(i);
                updateAthleteBoxScore(fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList(), doc, visitingPlayer, PLAYER_TYPE_NON_DEF);
            }
        } catch (SAXException e) {
            Logger.error("Error parsing incoming NFL stat update - " + e.getMessage());
        } catch (IOException | JSONException | ParserConfigurationException | XPathExpressionException e) {
            Logger.error("Non-parsing error for incoming NFL stat update - " + e.getMessage());
        }

        /*
         * Update all the AthleteSportEventInfos that we've collected data for.
         */
        for(FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent: fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList()) {
            AthleteSportEventInfo athleteSportEventInfo = sportsDao.findAthleteSportEventInfo(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
            if(fantasyPointAthleteUpdateEvent.getFantasyPoints() != null) {
                athleteSportEventInfo.setFantasyPoints(fantasyPointAthleteUpdateEvent.getFantasyPoints());
            }

            if(fantasyPointAthleteUpdateEvent.getBoxscore() != null) {
                athleteSportEventInfo.setStats(fantasyPointAthleteUpdateEvent.getBoxscore());
            }

            if(fantasyPointAthleteUpdateEvent.getTimeline() != null) {
                athleteSportEventInfo.setTimeline(fantasyPointAthleteUpdateEvent.getTimeline());
            }

            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
            fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfo);
        }

        SportEvent sportEventToUpdate = sportsDao.findSportEvent(fantasyPointUpdateEvent.getSportEvent().getStatProviderId());
        sportEventToUpdate.setComplete(fantasyPointUpdateEvent.getSportEvent().isComplete());
        sportEventToUpdate.setUnitsRemaining(fantasyPointUpdateEvent.getSportEvent().getUnitsRemaining());
        sportsDao.saveSportEvent(sportEventToUpdate);
        Logger.info("Sport Event " + sportEventToUpdate.getTeams().get(0).getAbbreviation() + "/" + sportEventToUpdate.getTeams().get(0).getAbbreviation() +
                " has " + sportEventToUpdate.getUnitsRemaining() + " units remaining (" + sportEventToUpdate.isComplete() + ")");

        return fantasyPointUpdateEvent;
    }

    @Override
    public boolean shouldProcessMessage(final Document document) throws XPathExpressionException {
        String value = PLAY_ID.evaluate(document);
        return value != null && !value.equals("");
    }

    @Override
    public void processEventDetails(FantasyPointUpdateEvent fantasyPointUpdateEvent, Document document) throws IOException, XPathExpressionException {
        List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents = fantasyPointUpdateEvent.getFantasyPointAthleteUpdateEventList();
        boolean isStatCorrection = isStatCorrectionMessage(document);

        Map<Integer, FantasyPointAthleteUpdateEvent> foundAthletesMap = new HashMap<>();

        /*
         * Grab the offensive and defensive teams.
         */
        Integer homeTeam = Integer.parseInt(HOME_TEAM.evaluate(document));
        Integer awayTeam = Integer.parseInt(AWAY_TEAM.evaluate(document));

        Athlete athlete = sportsDao.findAthlete(homeTeam);
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEventHomeTeam = createStatsFantasyPointAthleteUpdateEventForDefense(fantasyPointUpdateEvent, homeTeam, athlete, isStatCorrection);

        athlete = sportsDao.findAthlete(awayTeam);
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEventAwayTeam = createStatsFantasyPointAthleteUpdateEventForDefense(fantasyPointUpdateEvent, awayTeam, athlete, isStatCorrection);

        fantasyPointAthleteUpdateEvents.add(fantasyPointAthleteUpdateEventHomeTeam);
        fantasyPointAthleteUpdateEvents.add(fantasyPointAthleteUpdateEventAwayTeam);

        /*
         * Iterate through all of the plays provided for this update.  Normally, there will be just one.  However, in
         * the event of a stat correction, all the plays that have occurred so far will be resent.
         *
         * This time through we want to collect all of the athletes involved and create StatsFantasyPointAthleteUpdateEvent (FPAUE) objects
         * for them.
         */
        NodeList plays = (NodeList) NFL_EVENT_PLAY.evaluate(document, XPathConstants.NODESET);
        for(int i=0; i<plays.getLength(); i++) {
            Element play = (Element) plays.item(i);
            String playId = XPR_ID.evaluate(play);

            String eventDescription = DETAILS.evaluate(play);
            Logger.info("\n\nEvent Description: " + eventDescription);

            /*
             * Collect all the StatsFantasyPointAthleteUpdateEvent objects for this play.
             */
            NodeList statIds = (NodeList) STAT_ID.evaluate(play, XPathConstants.NODESET);
            for (int j = 0; j < statIds.getLength(); j++) {
                Node n = statIds.item(j);

                String globalPlayerId = GLOBAL_PLAYER_ID.evaluate(n);
                if(globalPlayerId.equals("")) {
                    continue;
                }
                int statProviderId = Integer.parseInt(globalPlayerId);
                FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = foundAthletesMap.get(statProviderId);

                /*
                 * Create the FPAUE if we haven't already encountered this athlete.
                 */
                if(fantasyPointAthleteUpdateEvent == null) {
                    athlete = sportsDao.findAthlete(statProviderId);
                    if (athlete != null) {
                        try {
                            AthleteSportEventInfo athleteSportEventInfo = sportsDao.findAthleteSportEventInfo(athlete, fantasyPointUpdateEvent.getSportEvent());

                            fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
                            fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfo);

                            /*
                             * For stat corrections (multiple <play> aggregates) we need to wipe the athlete's timeline and box
                             * score since we'll be recompiling them.
                             */
                            if (isStatCorrection) {
                                fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(new ArrayList<>()));
                                fantasyPointAthleteUpdateEvent.setBoxscore(sportsDao.createInitialJsonForAthleteBoxscore(athlete.getPositions().get(0)));
                            }
                            else {
                                List<Map<String, Object>> timeline = mapper.readValue(athleteSportEventInfo.getTimeline(), boxScoreTypeReference);
                                markTimelineAsPublished(timeline);
                                athleteSportEventInfo.setTimeline(mapper.writeValueAsString(timeline));
                                fantasyPointAthleteUpdateEvent.setTimeline(athleteSportEventInfo.getTimeline());

                                if(athleteSportEventInfo.getStats().equals("[]")) {
                                    athleteSportEventInfo.setStats(sportsDao.createInitialJsonForAthleteBoxscore(athlete.getPositions().get(0)));
                                }
                                fantasyPointAthleteUpdateEvent.setBoxscore(athleteSportEventInfo.getStats());
                            }

                            fantasyPointAthleteUpdateEvents.add(fantasyPointAthleteUpdateEvent);
                            foundAthletesMap.put(statProviderId, fantasyPointAthleteUpdateEvent);
                        } catch (NotFoundException e) {
                            Logger.error(String.format("%s %s could not be found in the AthleteSportEventInfo table.", athlete.getFirstName(), athlete.getLastName()));
                        }
                    } else {
                        Logger.error(String.format("Athlete entry could not be found for id %s.", statProviderId));
                    }
                }

                /*
                 * For events where we don't have a FPAUE (i.e. Tackle or Assist) then skip updating fantasy point changes and timeline.
                 */
                HashMap<String, Object> extraData = new HashMap<>();
                extraData.put("play", play);
                extraData.put("statId", n);
                updateFantasyPointChange(Arrays.asList(fantasyPointAthleteUpdateEvent, fantasyPointAthleteUpdateEventHomeTeam, fantasyPointAthleteUpdateEventAwayTeam),
                        document, extraData);

                updateTimeline(Arrays.asList(fantasyPointAthleteUpdateEvent, fantasyPointAthleteUpdateEventHomeTeam, fantasyPointAthleteUpdateEventAwayTeam), eventDescription, playId, isStatCorrection);

                // Reset FP deltas for defenses

            }

            /*
             * Determine FP hit for teams based on point yielded.
             */
            int awayScoreBefore = Integer.parseInt(XPE_AWAY_SCORE_BEFORE.evaluate(play));
            int awayScoreAfter = Integer.parseInt(XPE_AWAY_SCORE_AFTER.evaluate(play));
            int homeScoreBefore = Integer.parseInt(XPE_HOME_SCORE_BEFORE.evaluate(play));
            int homeScoreAfter = Integer.parseInt(XPE_HOME_SCORE_AFTER.evaluate(play));

            BigDecimal awayDSTScoreFantasyPoints = determineFantasyPointsFromScoreForDefense(homeScoreBefore, homeScoreAfter);
            BigDecimal homeDSTScoreFantasyPoints = determineFantasyPointsFromScoreForDefense(awayScoreBefore, awayScoreAfter);

            fantasyPointAthleteUpdateEventHomeTeam.setFantasyPointDelta(fantasyPointAthleteUpdateEventHomeTeam.getFantasyPointDelta().add(homeDSTScoreFantasyPoints));
            fantasyPointAthleteUpdateEventAwayTeam.setFantasyPointDelta(fantasyPointAthleteUpdateEventAwayTeam.getFantasyPointDelta().add(awayDSTScoreFantasyPoints));

            updateTimeline(Arrays.asList(fantasyPointAthleteUpdateEventHomeTeam, fantasyPointAthleteUpdateEventAwayTeam), eventDescription, playId, isStatCorrection);

            if(isStatCorrection) {
                fantasyPointAthleteUpdateEventAwayTeam.setFantasyPointDelta(BigDecimal.ZERO);
                fantasyPointAthleteUpdateEventHomeTeam.setFantasyPointDelta(BigDecimal.ZERO);
            }
        }
    }

    @Override
    public void updateSportEventStatus(Document doc, FantasyPointUpdateEvent fantasyPointUpdateEvent) throws XPathExpressionException {
        String status = NFL_EVENT_GAMESTATE_STATUS.evaluate(doc);

        if(status.equals("4")) {
            fantasyPointUpdateEvent.getSportEvent().setComplete(true);
        }
    }

    /**
     * This might be able to be folded into a base class.  It's the same as MLB.
     *
     * @param fantasyPointAthleteUpdateEvents The events causing the timeline update.
     * @param eventDescription                A plain-english description of the event that occurred.
     * @throws IOException
     */
    @Override
    public void updateTimeline(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, String eventDescription, String playId, boolean isStatCorrection) throws IOException {
        for(FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent: fantasyPointAthleteUpdateEvents) {
            /*
             * This will be null when processing an event that does not involve an individual player of interest.  A
             * good example of this would be a stat-id of
             *      <stat-id id="19" description="Tackle" team-id="26" player-id="24941" global-team-id="361" global-player-id="332735"/>
             *
             * The event is a Tackle and the athlete is a defensive player of no fantasy interest.  The reason we're
             * still processing stat-ids like these is because it may be relevant to the defense, like a fumble recovery
             * or interception.
             */
            if(fantasyPointAthleteUpdateEvent == null)
                continue;

            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            List<Map<String, Object>> timeline = mapper.readValue(fantasyPointAthleteUpdateEvent.getTimeline(), typeRef);

            /*
             * Flag all existing timeline entries as published.  The published flag is used to help us
             * figure out which timeline entries have already been sent out to clients.  Without this, we'd
             * be sending the entire timeline, and that can get very large.
             */
//            for(Map<String, Object> timelineEntry: timeline) {
//                timelineEntry.put("published", true);
//            }

            BigDecimal delta = fantasyPointAthleteUpdateEvent.getFantasyPointDelta();
            if(delta == null || delta.compareTo(BigDecimal.ZERO) == 0) {
//                fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(timeline));
                continue;
            }

            Map<Integer, Map<String, Long>> timestamps = DistributedServices.getInstance().getMap(GlobalConstants.STATS_UPDATE_TIMELINE_TIMESTAMPS);
            Map<String, Long> athleteTimestamps = timestamps.get(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
            if(athleteTimestamps == null) {
                athleteTimestamps = new HashMap<>();
                timestamps.put(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId(), athleteTimestamps);
            }

            Map<String, Object> data = new HashMap<>();
            Long ts = athleteTimestamps.get(playId);
            if(ts == null) {
                ts = new Date().getTime();
                athleteTimestamps.put(playId, ts);
                timestamps.put(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId(), athleteTimestamps);
            }
            data.put("timestamp", ts);
            data.put("description", eventDescription);

            String fpChange = String.format("%s%s", (delta.compareTo(new BigDecimal("0")) > 0) ? "+" : "", delta);
            if(fpChange.endsWith("0") && !fpChange.endsWith(".0")) {
                fpChange = fpChange.substring(0, fpChange.length()-1);
            }

            data.put("fpChange", fpChange);
            data.put("athleteSportEventInfoId", fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());
            data.put("id", ts + "_" + fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getId());

            if(!isDuplicateTimelineEntry(timeline, data)) {
                data.put("published", isStatCorrection);        // We want stat correction message to be considered already published.
                timeline.add(0, data);
                fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(timeline));
            }
        }
    }

    @Override
    public void updateFantasyPointChange(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, Document document, Map<String, Object> extraData) throws XPathExpressionException {
        // Parse out the <play> aggregate and <stat-id> fields.
        Element play = (Element) extraData.get("play");
        Element statId = (Element) extraData.get("statId");

        /*
         * Figure out who's on defense.  We start by assuming that the home team is on defense, and then,
         * if the possession belongs to the home team, we switch the defense to the visiting/away team.
         */
        boolean defenseIsHome = true;
        Integer defense = Integer.parseInt(HOME_TEAM.evaluate(document));
        Integer teamWithPossessionId = Integer.parseInt(XPE_POSSESSION_GLOBAL_ID.evaluate(play));
        if(defense.equals(teamWithPossessionId)) {
            defense = Integer.parseInt(AWAY_TEAM.evaluate(document));
            defenseIsHome = false;
        }

        /*
         * Set default delta values.
         */
        if(fantasyPointAthleteUpdateEvents.get(0) != null) {
            fantasyPointAthleteUpdateEvents.get(0).setFantasyPointDelta(BigDecimal.ZERO);
        }
//        fantasyPointAthleteUpdateEvents.get(1).setFantasyPointDelta(BigDecimal.ZERO);
//        fantasyPointAthleteUpdateEvents.get(2).setFantasyPointDelta(BigDecimal.ZERO);

        /*
         * Determine score difference.
         */
        int possession = Integer.parseInt(XPE_POSSESSION_GLOBAL_ID.evaluate(play));
        int possessionAfter = Integer.parseInt(XPE_END_POSSESSION_GLOBAL_ID.evaluate(play));

        int eventId = Integer.parseInt(XPR_ID.evaluate(statId));
        int eventType = Integer.parseInt(XPE_EVENT_TYPE.evaluate(play));
        int statProviderId = Integer.parseInt(GLOBAL_PLAYER_ID.evaluate(statId));
        int yards = 0;
        int points = 0;

        /*
         * Try to parse out yards and points.  These may not exist, so we need to wrap them in try/catch blocks.
         */
        try {   yards = Integer.parseInt(YARDS.evaluate(statId));      } catch(NumberFormatException nfe) {  Logger.info("updateFantasyPointChange - Could not parse out yards from stat-id field.");  }
        try {   points = Integer.parseInt(POINTS.evaluate(statId));    } catch(NumberFormatException nfe) {  Logger.info("updateFantasyPointChange - Could not parse out points from stat-id field.");   }

        Map<String, Integer> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("eventType", eventType);
        eventData.put("yards", yards);
        eventData.put("points", points);
        eventData.put("defenseId", defense);
        eventData.put("possessionBefore", possession);
        eventData.put("possessionAfter", possessionAfter);

        /*
         * Determine FP change for this event.  The first index is the individual player involved, and the second
         * is the current team defense.  If the play is not relevant to the defense (i.e. non-scoring rush, incomplete
         * pass) then the value is 0.
         */
        List<BigDecimal> result = fantasyPointTranslator.determineFantasyPointIncrementForEvent(eventData);

        /*
         * Determine if the athlete involved in the play is our FPAUE.
         */
        if (fantasyPointAthleteUpdateEvents.get(0) != null &&
                fantasyPointAthleteUpdateEvents.get(0).getAthleteSportEventInfo().getAthlete().getStatProviderId() == statProviderId) {
            fantasyPointAthleteUpdateEvents.get(0).setFantasyPointDelta(result.get(0));
        }

        /*
         * Update DST.
         */
        if(defenseIsHome) {
            fantasyPointAthleteUpdateEvents.get(1).setFantasyPointDelta(fantasyPointAthleteUpdateEvents.get(1).getFantasyPointDelta().add(result.get(1)));
        }
        else {
            fantasyPointAthleteUpdateEvents.get(2).setFantasyPointDelta(fantasyPointAthleteUpdateEvents.get(2).getFantasyPointDelta().add(result.get(1)));
        }

//        if(fantasyPointAthleteUpdateEvents.get(0) != null) {
//            Logger.info(fantasyPointAthleteUpdateEvents.get(0).getAthleteSportEventInfo().getAthlete().getFirstName() + " " +
//                    fantasyPointAthleteUpdateEvents.get(0).getAthleteSportEventInfo().getAthlete().getLastName() + " received " +
//                    fantasyPointAthleteUpdateEvents.get(0).getFantasyPointDelta().doubleValue() + " fantasy points");
//        }
//        Logger.info(fantasyPointAthleteUpdateEvents.get(1).getAthleteSportEventInfo().getAthlete().getFirstName() + " " +
//                fantasyPointAthleteUpdateEvents.get(1).getAthleteSportEventInfo().getAthlete().getLastName() + " received " +
//                fantasyPointAthleteUpdateEvents.get(1).getFantasyPointDelta().doubleValue() + " fantasy points");
//        Logger.info(fantasyPointAthleteUpdateEvents.get(2).getAthleteSportEventInfo().getAthlete().getFirstName() + " " +
//                fantasyPointAthleteUpdateEvents.get(2).getAthleteSportEventInfo().getAthlete().getLastName() + " received " +
//                fantasyPointAthleteUpdateEvents.get(2).getFantasyPointDelta().doubleValue() + " fantasy points");
    }

    @Override
    public void updateIndicators(FantasyPointUpdateEvent fantasyPointUpdateEvent, Document document) throws XPathExpressionException {
        int teamWithPossession = Integer.parseInt(NFL_EVENT_GAMESTATE_TEAM_POSSESSION_GLOBAL_ID.evaluate(document));

        String yardsFromGoalStr = NFL_EVENT_GAMESTATE_YARDS_FROM_GOAL.evaluate(document);
        int yardsFromGoal = "".equals(yardsFromGoalStr) ? 0 : Integer.parseInt(yardsFromGoalStr);
        boolean redzone = yardsFromGoal <= 20;

        /*
         * Reset indicators for all athletes in this sport event.
         */
        Map<Integer, Integer> indicators = fantasyPointUpdateEvent.getIndicators();
        List<AthleteSportEventInfo> athleteSportEventInfoList = sportsDao.findAthleteSportEventInfos(fantasyPointUpdateEvent.getSportEvent());
        for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfoList) {
            int indicator = GlobalConstants.INDICATOR_TEAM_OFF_FIELD;
            if (!isFinalBoxscoreMessage(document) && athleteSportEventInfo.getAthlete().getTeam().getStatProviderId() == teamWithPossession && !redzone) {
                indicator = GlobalConstants.INDICATOR_TEAM_ON_FIELD;
            }
            else if(!isFinalBoxscoreMessage(document) && athleteSportEventInfo.getAthlete().getTeam().getStatProviderId() == teamWithPossession && redzone) {
                indicator = GlobalConstants.INDICATOR_SCORING_OPPORTUNITY;
            }

            /*
             * Deal with DSTs
             */
            if(!isFinalBoxscoreMessage(document) && athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.FB_DEFENSE) &&
                    athleteSportEventInfo.getAthlete().getTeam().getStatProviderId() == teamWithPossession) {
                indicator = GlobalConstants.INDICATOR_TEAM_OFF_FIELD;
            }
            else if(!isFinalBoxscoreMessage(document) && athleteSportEventInfo.getAthlete().getPositions().get(0).equals(Position.FB_DEFENSE) &&
                    athleteSportEventInfo.getAthlete().getTeam().getStatProviderId() != teamWithPossession) {
                indicator = redzone ? GlobalConstants.INDICATOR_SCORING_OPPORTUNITY : GlobalConstants.INDICATOR_TEAM_ON_FIELD;
            }

            indicators.put(athleteSportEventInfo.getId(), indicator);

            athleteSportEventInfo.setIndicator(indicator);
            sportsDao.saveAthleteSportEventInfo(athleteSportEventInfo);
        }
    }

    @Override
    public int updateUnitsRemaining(SportEvent sportEvent, Document document) throws XPathExpressionException {
        int quarter = Integer.parseInt(NFL_EVENT_GAMESTATE_QUARTER.evaluate(document));
        int minutes = Integer.parseInt(NFL_EVENT_GAMESTATE_MINUTES.evaluate(document));
        int unitsRemaining = (4-quarter) * 15 + minutes;

        sportEvent.setUnitsRemaining(unitsRemaining);

        int currentUnits = 60 - unitsRemaining;

        Logger.info("updateUnitsRemaining: " + quarter + " quarter, minute " + minutes + ", unitsRemaining " + unitsRemaining + ", currentUnits " + currentUnits);

        return currentUnits;
    }

    @Override
    public int[] extractGameScore(Document doc) throws XPathExpressionException {
        int homeScore = Integer.parseInt(NFL_EVENT_HOME_TEAM_LINESCORE_SCORE.evaluate(doc));
        int awayScore = Integer.parseInt(NFL_EVENT_VISITING_TEAM_LINESCORE_SCORE.evaluate(doc));

        Logger.info("extractGameScore: Score is currently " + homeScore + " - " + awayScore);

        return new int[]{homeScore, awayScore};
    }

    @Override
    public void updateAthleteBoxScore(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, Document doc, Element e, int type)
            throws XPathExpressionException, JSONException, IOException {
        int athleteId;
        BigDecimal fantasyPoints;
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent;
        List<Map<String, Object>> boxScore;

        /*
         * For a defense we are expecting the <home-team> or <visiting-team> aggregates as our Element e.
         */
        Map<String, BigDecimal> boxScoreValues = new LinkedHashMap<>();
        if(type == PLAYER_TYPE_HOME_TEAM || type == PLAYER_TYPE_AWAY_TEAM) {
            /*
             * Parse out relevant statistics from XML.
             */
            BigDecimal sacks = createBigDecimalFromStat(DEFENSE_SACKS, e);
            BigDecimal interceptions = createBigDecimalFromStat(INTERCEPTION_RETURNS_ATTEMPTS, e);
            BigDecimal fumbleRecoveries = createBigDecimalFromStat(OPPONENT_FUMBLES_RECOVERED, e);
            BigDecimal kickoffReturnTouchdowns = createBigDecimalFromStat(KICK_RETURNS_TDS, e);
            BigDecimal puntReturnTouchdowns = createBigDecimalFromStat(PUNT_RETURNS_TDS, e);
            BigDecimal interceptionReturnTouchdowns = createBigDecimalFromStat(INTERCEPTION_RETURNS_TDS, e);
            BigDecimal fumbleRecoveryTouchdowns = createBigDecimalFromStat(OPPONENT_FUMBLES_TDS, e);
//            BigDecimal blockedPuntFgReturnTouchdowns
            BigDecimal safety = createBigDecimalFromStat(SAFETIES_SAFETIES, e);
            BigDecimal blockedFieldGoals;
            BigDecimal pointsAllowed;
            if(type == PLAYER_TYPE_HOME_TEAM) {
                athleteId = Integer.parseInt(NFL_EVENT_HOME_TEAM_TEAM_CODE_GLOBAL_ID.evaluate(doc));
                blockedFieldGoals = createBigDecimalFromStat(NFL_EVENT_VISITING_TEAM_FIELD_GOALS_BLOCKED, doc.getDocumentElement());
                pointsAllowed = createBigDecimalFromStat(NFL_EVENT_VISITING_TEAM_LINESCORE_SCORE, doc.getDocumentElement());

                // Exclude safeties, fumble-recovery TDs, and interception-return TDs from points allowed.
                pointsAllowed = adjustPointsAllowedForSpecialCases(TeamLocationState.VISITING, doc, pointsAllowed);
            }
            else {
                athleteId = Integer.parseInt(NFL_EVENT_VISITING_TEAM_TEAM_CODE_GLOBAL_ID.evaluate(doc));
                blockedFieldGoals = createBigDecimalFromStat(NFL_EVENT_HOME_TEAM_FIELD_GOALS_BLOCKED, doc.getDocumentElement());
                pointsAllowed = createBigDecimalFromStat(NFL_EVENT_HOME_TEAM_LINESCORE_SCORE, doc.getDocumentElement());

                // Exclude safeties, fumble-recovery TDs, and interception-return TDs from points allowed.
                pointsAllowed = adjustPointsAllowedForSpecialCases(TeamLocationState.HOME, doc, pointsAllowed);
            }

            /*
             * Parse out the existing box score, now that we have an athlete id.  We need this right now because the
             * NFL socket doesn't give us the whole box score all over again (like MLB does).  Instead, we need to
             * keep track of it, so we'll just heap whatever stats we get on top of this structure.
             */
            fantasyPointAthleteUpdateEvent = getStatsFantasyPointAthleteUpdateEventForAthlete(fantasyPointAthleteUpdateEvents, athleteId);
            boxScore = mapper.readValue(fantasyPointAthleteUpdateEvent.getBoxscore(), boxScoreTypeReference);
            for(Map<String, Object> boxScoreEntry: boxScore) {
                BigDecimal amount = (boxScoreEntry.get(BOXSCORE_JSON_FIELD_AMOUNT) instanceof Integer) ? new BigDecimal((Integer) boxScoreEntry.get(BOXSCORE_JSON_FIELD_AMOUNT)) : new BigDecimal((Double) boxScoreEntry.get(BOXSCORE_JSON_FIELD_AMOUNT));
                boxScoreValues.put((String) boxScoreEntry.get(BOXSCORE_JSON_FIELD_NAME), amount);
            }

            /*
             * Put the stat values in a map keyed off the stat name so we can dynamically refer to them.
             */
            if(sacks != null)                               boxScoreValues.put(GlobalConstants.SCORING_NFL_SACK_LABEL, sacks);
            if(interceptions != null)                       boxScoreValues.put(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL, interceptions);
            if(fumbleRecoveries != null)                    boxScoreValues.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL, fumbleRecoveries);
            if(kickoffReturnTouchdowns != null)             boxScoreValues.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, kickoffReturnTouchdowns);
            if(puntReturnTouchdowns != null)                boxScoreValues.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, puntReturnTouchdowns);
            if(interceptionReturnTouchdowns != null)        boxScoreValues.put(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL, interceptionReturnTouchdowns);
            if(fumbleRecoveryTouchdowns != null)            boxScoreValues.put(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL, fumbleRecoveryTouchdowns);
            if(safety != null)                              boxScoreValues.put(GlobalConstants.SCORING_NFL_SAFETY_LABEL, safety);
            if(blockedFieldGoals != null)                   boxScoreValues.put(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL, blockedFieldGoals);

            boxScoreValues.put(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL, pointsAllowed);

            /**
             * Go through all the stats that we have, calculate and sum the fantasy points and generate a box score.
             */
            for(int i=0; i<GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE.length; i++) {
                Map<String, BigDecimal> currStatMap = new HashMap<>();
                if(boxScoreValues.containsKey(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i])) {
                    currStatMap.put(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i], boxScoreValues.get(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i]));

                    BigDecimal fantasyPointsForStat = fantasyPointTranslator.calculateFantasyPoints(currStatMap);
//                    fantasyPointsForStat = fantasyPointsForStat.setScale(2, BigDecimal.ROUND_CEILING);

                    Map<String, Object> boxScoreMap = new HashMap<>();
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_NAME, GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i]);
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_ABBR, GlobalConstants.SCORING_NFL_NAME_TO_ABBR_MAP.get(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i]));
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_AMOUNT, boxScoreValues.get(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i]));
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_FPP, fantasyPointsForStat);
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_ID, GlobalConstants.SCORING_NFL_NAME_TO_ID_MAP.get(GlobalConstants.STATS_ARRAY_FOR_NFL_DEFENSE[i]));

                    // Find the box score entry to replace.
                    int idx=0;
                    for(Map<String, Object> boxScoreEntry: boxScore) {
                        if(boxScoreEntry.get(BOXSCORE_JSON_FIELD_NAME).equals(boxScoreMap.get(BOXSCORE_JSON_FIELD_NAME))) {
                            boxScore.remove(idx);
                            boxScore.add(idx, boxScoreMap);
                            break;
                        }
                        idx++;
                    }
                }
            }
        }
        /*
         * Offensive position player.  Here we are expecting the <home-player> or <visiting-player> aggregate.
         */
        else {
            athleteId = Integer.parseInt(PLAYER_CODE_GLOBAL_ID.evaluate(e));

            /*
             * Parse out the existing box score, now that we have an athlete id.  We need this right now because the
             * NFL socket doesn't give us the whole box score all over again (like MLB does).  Instead, we need to
             * keep track of it, so we'll just heap whatever stats we get on top of this structure.
             */
            fantasyPointAthleteUpdateEvent = getStatsFantasyPointAthleteUpdateEventForAthlete(fantasyPointAthleteUpdateEvents, athleteId);
            if(fantasyPointAthleteUpdateEvent == null) {
                return;
            }

            /*
             * Parse out relevant statistics from XML.
             */
            BigDecimal passingYards = createBigDecimalFromStat(PASSING_YARDS, e);
            BigDecimal rushingYards = createBigDecimalFromStat(RUSHING_YARDS, e);
            BigDecimal receivingYards = createBigDecimalFromStat(RECEIVING_YARDS, e);
            BigDecimal passingTouchdowns = createBigDecimalFromStat(PASSING_TDS, e);
            BigDecimal rushingTouchdowns = createBigDecimalFromStat(RUSHING_TDS, e);
            BigDecimal receivingTouchdowns = createBigDecimalFromStat(RECEIVING_TDS, e);
            BigDecimal receptions = createBigDecimalFromStat(RECEIVING_RECEPTIONS, e);
            BigDecimal lostFumbles = createBigDecimalFromStat(FUMBLES_LOST, e);
            BigDecimal puntReturnTouchdown = createBigDecimalFromStat(PUNT_RETURNING_TDS, e);
            BigDecimal kickReturnTouchdown = createBigDecimalFromStat(KICK_RETURNING_TDS, e);
            BigDecimal twoPointPass = createBigDecimalFromStat(TWO_POINT_PASSING_COMPLETIONS, e);
            BigDecimal twoPointRush = createBigDecimalFromStat(TWO_POINT_RUSHING_COMPLETIONS, e);
            BigDecimal twoPointRec = createBigDecimalFromStat(TWO_POINT_RECEIVING_COMPLETIONS, e);
            BigDecimal twoPointConversions = BigDecimal.ZERO;
            if(twoPointPass != null)    twoPointConversions = twoPointConversions.add(twoPointPass);
            if(twoPointRush != null)    twoPointConversions = twoPointConversions.add(twoPointRush);
            if(twoPointRec != null)    twoPointConversions = twoPointConversions.add(twoPointRec);

            boxScore = mapper.readValue(fantasyPointAthleteUpdateEvent.getBoxscore(), boxScoreTypeReference);
            for(Map<String, Object> boxScoreEntry: boxScore) {
                BigDecimal amount;
                if(boxScoreEntry.get(BOXSCORE_JSON_FIELD_AMOUNT) instanceof Integer) {
                    amount = new BigDecimal((Integer) boxScoreEntry.get(BOXSCORE_JSON_FIELD_AMOUNT));
                }
                else {
                    amount = new BigDecimal((Double) boxScoreEntry.get(BOXSCORE_JSON_FIELD_AMOUNT));
                }
                boxScoreValues.put((String) boxScoreEntry.get(BOXSCORE_JSON_FIELD_NAME), amount);
            }

            /*
             * Put the stat values in a map keyed off the stat name so we can dynamically refer to them.
             */
            if(passingYards != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL, passingYards);
            if(rushingYards != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL, rushingYards);
            if(receivingYards != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL, receivingYards);
            if(passingTouchdowns != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL, passingTouchdowns);
            if(rushingTouchdowns != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL, rushingTouchdowns);
            if(receivingTouchdowns != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL, receivingTouchdowns);
            if(receptions != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_RECEPTION_LABEL, receptions);
            if(lostFumbles != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL, lostFumbles);
            if(puntReturnTouchdown != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL, puntReturnTouchdown);
            if(kickReturnTouchdown != null)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL, kickReturnTouchdown);
            if(twoPointConversions.compareTo(BigDecimal.ZERO) != 0)
                boxScoreValues.put(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL, twoPointConversions);


            /**
             * Go through all the stats that we have, calculate and sum the fantasy points and generate a box score.
             */
            for(int i=0; i<GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE.length; i++) {
                Map<String, BigDecimal> currStatMap = new HashMap<>();
                if(boxScoreValues.containsKey(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i])) {
                    currStatMap.put(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i], boxScoreValues.get(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i]));

                    BigDecimal fantasyPointsForStat = fantasyPointTranslator.calculateFantasyPoints(currStatMap);
                    fantasyPointsForStat = fantasyPointsForStat.setScale(2, RoundingMode.HALF_EVEN);

                    Map<String, Object> boxScoreMap = new HashMap<>();
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_NAME, GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i]);
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_ABBR, GlobalConstants.SCORING_NFL_NAME_TO_ABBR_MAP.get(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i]));
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_AMOUNT, boxScoreValues.get(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i]));
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_FPP, fantasyPointsForStat);
                    boxScoreMap.put(BOXSCORE_JSON_FIELD_ID, GlobalConstants.SCORING_NFL_NAME_TO_ID_MAP.get(GlobalConstants.STATS_ARRAY_FOR_NFL_OFFENSE[i]));

                    // Find the box score entry to replace.
                    int idx=0;
                    for(Map<String, Object> boxScoreEntry: boxScore) {
                        if(boxScoreEntry.get(BOXSCORE_JSON_FIELD_NAME).equals(boxScoreMap.get(BOXSCORE_JSON_FIELD_NAME))) {
                            boxScore.remove(idx);
                            boxScore.add(idx, boxScoreMap);
                            break;
                        }
                        idx++;
                    }
                }
            }
        }

        fantasyPointAthleteUpdateEvent = getStatsFantasyPointAthleteUpdateEventForAthlete(fantasyPointAthleteUpdateEvents, athleteId);

        // Set fantasy points
        fantasyPoints = fantasyPointTranslator.calculateFantasyPoints(boxScoreValues);
        fantasyPointAthleteUpdateEvent.setFantasyPoints(fantasyPoints);

        // Set box score
        String boxScoreString = mapper.writeValueAsString(boxScore);
        fantasyPointAthleteUpdateEvent.setBoxscore(boxScoreString);
    }

    /**
     * Determine the fantasy point hit that a defense takes based on the opponent score before and after.
     *
     * @param scoreBefore   The opponent's score before the play.
     * @param scoreAfter    The opponent's score after the play.
     * @return              A BigDecimal representing the difference between the two scores.
     */
    private BigDecimal determineFantasyPointsFromScoreForDefense(int scoreBefore, int scoreAfter) {
        return new BigDecimal(scoreAfter - scoreBefore).multiply(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_FACTOR);
    }

    /**
     * Generate a BigDecimal object from the provided XPathExpression and XML document.
     *
     * @param expression The expression to evaluate in order to get the bigdecimal.
     * @param element    The element to evaluate with the expression.
     * @return Either the constructed bigdecimal or null if a NumberFormatException is thrown in parsing the result.
     */
    private BigDecimal createBigDecimalFromStat(final XPathExpression expression, final Element element) {
        try {
            return new BigDecimal(expression.evaluate(element));
        } catch (final Exception e) {
            return null;
        }
    }

    /** Retrieve the proper FPAUE object for the provided athlete. */
    private FantasyPointAthleteUpdateEvent getStatsFantasyPointAthleteUpdateEventForAthlete(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, int athleteId) {
        for(FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent: fantasyPointAthleteUpdateEvents) {
            if(fantasyPointAthleteUpdateEvent.getAthleteSportEventInfo().getAthlete().getStatProviderId() == athleteId) {
                return fantasyPointAthleteUpdateEvent;
            }
        }

        return null;
    }

    /** Determine if the incoming message is normal or a stat correction.  Stat corrections will have multiple <play> aggregates in them. */
    private boolean isStatCorrectionMessage(Document document) throws XPathExpressionException {
        NodeList players = (NodeList) NFL_EVENT_PLAY.evaluate(document, XPathConstants.NODESET);
        return players.getLength() > 1;
    }

    /** Generate a new StatsFantasyPointAthleteUpdateEvent object for the provided athlete and team. */
    private FantasyPointAthleteUpdateEvent createStatsFantasyPointAthleteUpdateEventForDefense(FantasyPointUpdateEvent fantasyPointUpdateEvent,
                                                                                                    Integer homeTeam, Athlete athlete, boolean isStatCorrection) throws IOException {
        FantasyPointAthleteUpdateEvent fantasyPointAthleteUpdateEvent = null;
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};

        try {
            if(athlete != null) {
                AthleteSportEventInfo athleteSportEventInfo = sportsDao.findAthleteSportEventInfo(athlete, fantasyPointUpdateEvent.getSportEvent());

                fantasyPointAthleteUpdateEvent = new FantasyPointAthleteUpdateEvent();
                fantasyPointAthleteUpdateEvent.setFantasyPointDelta(BigDecimal.ZERO);
                fantasyPointAthleteUpdateEvent.setAthleteSportEventInfo(athleteSportEventInfo);

                if(isStatCorrection) {
                    List<Map<String, Object>> timeline = new ArrayList<>();
                    timeline.add(createInitialDefenseTimelineEntry(athleteSportEventInfo, "0", true));

                    fantasyPointAthleteUpdateEvent.setTimeline(mapper.writeValueAsString(timeline));
                    fantasyPointAthleteUpdateEvent.setBoxscore(sportsDao.createInitialJsonForAthleteBoxscore(athlete.getPositions().get(0)));
                }
                else {
                    // Is the defense's timeline empty (game just started)?
                    List<Map<String, Object>> timeline = mapper.readValue(athleteSportEventInfo.getTimeline(), typeRef);
                    if(timeline.isEmpty()) {
                        timeline.add(createInitialDefenseTimelineEntry(athleteSportEventInfo, "0", false));
                    } else {
                        markTimelineAsPublished(timeline);
                    }

                    athleteSportEventInfo.setTimeline(mapper.writeValueAsString(timeline));

                    fantasyPointAthleteUpdateEvent.setTimeline(athleteSportEventInfo.getTimeline());
                    fantasyPointAthleteUpdateEvent.setBoxscore(athleteSportEventInfo.getStats());
                }
            }
            else {
                Logger.error(String.format("Athlete entry could not be found for home team id %s.", homeTeam));
            }
        }
        catch(NotFoundException e) {
            Logger.error(String.format("%s %s could not be found in the AthleteSportEventInfo table.", athlete.getFirstName(), athlete.getLastName()));
        }

        return fantasyPointAthleteUpdateEvent;
    }

    /**
     * Determines if the incoming message is a Final Boxscore message.  These are sent when the game has completed.
     *
     * @param document The XML document to search.
     * @return True, if the message is a Final Boxscore.  False, otherwise.
     * @throws XPathExpressionException
     */
    private boolean isFinalBoxscoreMessage(Document document) throws XPathExpressionException {
        String gameStatus = NFL_EVENT_GAMESTATE_STATUS.evaluate(document);
        return gameStatus != null && gameStatus.equals("4");
    }

    /**
     * Checks if the timeline entry is a duplicate.
     *
     * @param timeline      The current timeline.
     * @param entry         The entry to be added.
     * @return              True, if the entry already exists.  False, otherwise.
     */
    private boolean isDuplicateTimelineEntry(List<Map<String, Object>> timeline, Map<String, Object> entry) {
        for(Map<String, Object> currEntry: timeline) {
//            if(currEntry.get("timestamp").equals(entry.get("timestamp"))
//                    && currEntry.get("description").equals(entry.get("description"))
//                    && currEntry.get("fpChange").equals(entry.get("fpChange")) && currEntry.get("athleteSportEventInfoId").equals(entry.get("athleteSportEventInfoId"))) {
//                return true;
//            }
            if(currEntry.get("id").equals(entry.get("id"))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Defenses should start with a single timeline entry crediting them with 12 FPs for no points allowed.
     *
     * @param athleteSportEventInfo     The ASEI representing the defense.
     * @param playId                    The id value of the play.  This is used for the timestamps map.
     * @param published                 Flag to tell if we want to consider this already published or not (used with stat corrections).
     * @return                          A Map containing the data of the timeline entry.
     */
    private Map<String, Object> createInitialDefenseTimelineEntry(AthleteSportEventInfo athleteSportEventInfo, String playId, boolean published) {
        Map<String, Object> entry = new HashMap<>();

        Map<Integer, Map<String, Long>> timestamps = DistributedServices.getInstance().getMap(GlobalConstants.STATS_UPDATE_TIMELINE_TIMESTAMPS);
        Map<String, Long> athleteTimestamps = timestamps.get(athleteSportEventInfo.getId());
        if(athleteTimestamps == null) {
            athleteTimestamps = new HashMap<>();
            timestamps.put(athleteSportEventInfo.getId(), athleteTimestamps);
        }

        Long ts = athleteTimestamps.get(playId);
        if(ts == null) {
            ts = new Date().getTime();
            athleteTimestamps.put(playId, ts);
            timestamps.put(athleteSportEventInfo.getId(), athleteTimestamps);
        }
        entry.put("id", ts + "_" + athleteSportEventInfo.getId());
        entry.put("published", published);
        entry.put("timestamp", ts);
        entry.put("description", "The game has started, 0 points allowed.");
        entry.put("fpChange", "+12");
        entry.put("athleteSportEventInfoId", athleteSportEventInfo.getId());

        return entry;
    }

    private BigDecimal adjustPointsAllowedForSpecialCases(final TeamLocationState homeVisiting, final Document doc, final BigDecimal pointsAllowed) {
        // Exclude safeties, fumble-recovery TDs, and interception-return TDs from points allowed.
        final BigDecimal oppSafeties;
        final BigDecimal oppFumbleRecoveryTds;
        final BigDecimal oppInterceptionReturnTds;
        switch (homeVisiting) {
            case HOME: {
                oppSafeties = createBigDecimalFromStat(NFL_EVENT_HOME_TEAM_SAFETIES_SAFETIES, doc.getDocumentElement());
                oppFumbleRecoveryTds = createBigDecimalFromStat(NFL_EVENT_HOME_TEAM_OPPONENT_FUMBLES_TDS, doc.getDocumentElement());
                oppInterceptionReturnTds = createBigDecimalFromStat(NFL_EVENT_HOME_TEAM_INTERCEPTION_RETURNS_TDS, doc.getDocumentElement());
                break;
            }
            case VISITING: {
                oppSafeties = createBigDecimalFromStat(NFL_EVENT_VISITING_TEAM_SAFETIES_SAFETIES, doc.getDocumentElement());
                oppFumbleRecoveryTds = createBigDecimalFromStat(NFL_EVENT_VISITING_TEAM_OPPONENT_FUMBLES_TDS, doc.getDocumentElement());
                oppInterceptionReturnTds = createBigDecimalFromStat(NFL_EVENT_VISITING_TEAM_INTERCEPTION_RETURNS_TDS, doc.getDocumentElement());
                break;
            }
            default:
                throw new Error("Code Branch unexpected.");
        }

        BigDecimal adjustedPointsAllowed = pointsAllowed;
        if (oppSafeties != null) adjustedPointsAllowed = pointsAllowed.subtract(oppSafeties.multiply(new BigDecimal("2")));
        if (oppFumbleRecoveryTds != null)
            adjustedPointsAllowed = pointsAllowed.subtract(oppFumbleRecoveryTds.multiply(new BigDecimal("6")));
        if (oppInterceptionReturnTds != null)
            adjustedPointsAllowed = pointsAllowed.subtract(oppInterceptionReturnTds.multiply(new BigDecimal("6")));

        return adjustedPointsAllowed;
    }

    private void markTimelineAsPublished(List<Map<String, Object>> timeline) {
        for(Map<String, Object> entry: timeline) {
            entry.put("published", true);
        }
    }
}
