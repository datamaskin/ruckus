package stats.updateprocessor;

import models.sports.SportEvent;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 7/17/14.
 */
public interface IUpdateProcessor {

    /**
     * Main method for performing processing on the provided Stats update.
     *
     * @param statData      The XML update as a String.
     * @return              A FantasyPointUpdateEvent object.
     */
    FantasyPointUpdateEvent process(String statData);

    /**
     * Make a determination whether the incoming message is of interest to us.
     *
     * @param document      A DOM document representing the message.
     * @return              True, if the message is of interest and should be processed.  False, otherwise.
     */
    boolean shouldProcessMessage(Document document) throws XPathExpressionException;

    void processEventDetails(FantasyPointUpdateEvent fantasyPointUpdateEvent, Document document) throws IOException, XPathExpressionException;

    /**
     * Updates the complete flag for the SportEvent if it's status is Final.
     *
     * @param doc                       The incoming XML document containing the game status.
     * @param fantasyPointUpdateEvent
     */
    void updateSportEventStatus(Document doc, FantasyPointUpdateEvent fantasyPointUpdateEvent) throws XPathExpressionException;

    /**
     * Updates the timeline by taking the fantasy point difference and event description and creating a map out of
     * them, then adding it to the front of the list representing the timeline.
     *
     * @param fantasyPointAthleteUpdateEvents
     * @param eventDescription                  A plain-english description of the event that occurred.
     * @param playId                            The stats-provided id of the play.
     * @param isStatCorrection                  Flag to determine if the play being processed is part of a stat correction.
     * @throws IOException When Jackson parsing goes bad.
     */
    void updateTimeline(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, String eventDescription, String playId, boolean isStatCorrection) throws IOException;

    void updateFantasyPointChange(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, Document document, Map<String, Object> extraData) throws XPathExpressionException;

    /**
     * Evaluates the event details to determine how to manipulate athlete indicator lights.
     *
     * @param fantasyPointUpdateEvent
     */
    void updateIndicators(FantasyPointUpdateEvent fantasyPointUpdateEvent, Document document) throws XPathExpressionException;

    /**
     * Updates the units remaining in the SportEvent and returns the current unit of time within the sport event.
     *
     * @param sportEvent        The SportEvent to update.
     * @param document          The DOM document containing data.
     * @return                  The current unit of time that the game is in.
     * @throws XPathExpressionException
     */
    int updateUnitsRemaining(SportEvent sportEvent, Document document) throws XPathExpressionException;

    /**
     * Extracts the score of the game.
     *
     * @param doc       The document containing the game score.
     * @return          An integer array containing the home (index 0) and away (index 1) score.
     * @throws XPathExpressionException
     */
    int[] extractGameScore(Document doc) throws XPathExpressionException;

    /**
     * Records the stats update into the live_data_feed for the simulator.
     *
     * @param statData      The String representation of the incoming XML message.
     * @param gameId        The stat provider id of the game.
     */
    void recordStatsUpdate(String statData, int gameId);

    /**
     * Generate FantasyPointUpdateEvents from the document.
     *
     * @param fantasyPointAthleteUpdateEvents
     * @param doc
     * @param e
     * @param type
     */
    void updateAthleteBoxScore(List<FantasyPointAthleteUpdateEvent> fantasyPointAthleteUpdateEvents, Document doc, Element e, int type)
            throws XPathExpressionException, JSONException, IOException;
}
