package utils.projectiongraph;

import dao.DaoFactory;
import dao.IStatsDao;
import models.contest.Lineup;
import models.contest.LineupSpot;
import models.sports.Athlete;
import models.stats.nfl.StatsNflProjection;
import models.stats.nfl.StatsNflProjectionDefense;

import java.math.BigDecimal;
import java.util.List;

/**
 * Base class for League-specific ProjectionGraphHelper classes.
 */
public abstract class BaseProjectionGraphHelper implements IProjectionGraphHelper {
    protected Lineup lineup;
    protected IStatsDao statsDao;

    /**
     * Default constructor
     *
     * @param lineup        The lineup we are using to construct projection graph data.
     */
    public BaseProjectionGraphHelper(Lineup lineup) {
        this.lineup = lineup;

        this.statsDao = DaoFactory.getStatsDao();
    }

    /**
     * Retrieves individual projections from the database and sums them for a total projection score.
     *
     * @param writeToProjectionTable        Flag to determine if we should write this to stats_projections.
     * @return      A BigDecimal representing the projected score of the lineup.
     */
    public abstract BigDecimal calculateTotalLineupProjection(boolean writeToProjectionTable);

    public Lineup getLineup() {
        return lineup;
    }

    public void setLineup(Lineup lineup) {
        this.lineup = lineup;
    }
}
