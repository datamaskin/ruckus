package models.contest;

import models.stats.predictive.StatsProjectionGraphData;

/**
 * Model representing the high-level aggregation of all data necessary for an update in
 * the /contestlivedrillin socket endpoint.
 */
public class ContestLiveDetail {
    /**
     * Data required to construct the projection graph.
     */
    private StatsProjectionGraphData statsProjectionGraphData;

    /**
     * Entry that has been updated.
     */
    private Entry entry;

    public ContestLiveDetail(StatsProjectionGraphData statsProjectionGraphData, Entry entry) {
        this.statsProjectionGraphData = statsProjectionGraphData;
        this.entry = entry;
    }

    public StatsProjectionGraphData getStatsProjectionGraphData() {
        return statsProjectionGraphData;
    }

    public void setStatsProjectionGraphData(StatsProjectionGraphData statsProjectionGraphData) {
        this.statsProjectionGraphData = statsProjectionGraphData;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
}
