package stats.manager;

import models.sports.Position;

import java.util.Date;
import java.util.Map;

/**
 * Created by dmaclean on 7/14/14.
 */
public interface IStatsDefenseVsPositionManager {
    /**
     * Generates the DvP calculations for the specified season.
     *
     * @param season        The season of interest.
     */
    void calculateDefenseVsPosition(int season);

    /**
     * Generates the DvP calculations for the specified season and position.
     *
     * @param season        The season of interest.
     * @param position      The position of interest.
     * @return
     */
    Map<Integer, Integer> calculateDefenseVsPosition(int season, Position position);

    /**
     * Determine the defense-vs-position rankings for teams as of the provided start date for the desired position.
     *
     * @param startTime     The point in time we want to evaluate DvP from.
     * @param position      The position we want to evaluate for.
     * @return
     */
    Map<Integer, Integer> calculateDefenseVsPosition(Date startTime, Position position);
}
