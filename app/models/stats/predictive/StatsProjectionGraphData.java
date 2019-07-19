package models.stats.predictive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.Contest;
import models.contest.ContestPayout;
import models.contest.Entry;
import models.contest.Lineup;
import play.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class representing an instance of the contest data to be sent to a user who connects
 * to the /contestlive endpoint.
 */
public class StatsProjectionGraphData {
    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<List<BigDecimal>> typeReference = new TypeReference<List<BigDecimal>>() {
    };

    private int id;
    private String league;
    private int entryFee;
    private int currentEntries;
    private int numPaid;
    private String type;
    private int duplicateEntries;
    private Date startTime;
    private int unitsRemaining;
    private int salaryCap;
    private BigDecimal projectedFirstMoneyPoints;
    private BigDecimal projectedLastMoneyPoints;
    private BigDecimal currentPoints;
    private BigDecimal projectedPoints;
    private int currentPosition;
    private int projectedPosition;
    private int currentWinnings; // As cents
    private int projectedWinnings;
    private List<List<BigDecimal>> currentPerformanceData;
    private List<List<BigDecimal>> projectedPerformanceData;

    /**
     * Default constructor.
     */
    public StatsProjectionGraphData() {
    }

    /**
     * Constructor that fully populates the model.
     *
     * @param contest The Contest that this instance will be based on.
     */
    public StatsProjectionGraphData(Contest contest, Lineup lineup) {
        List<Entry> allEntries = DaoFactory.getContestDao().findEntries(contest);
        List<Entry> entriesForLineup = DaoFactory.getContestDao().findEntries(lineup, contest);
        Entry entry = entriesForLineup.get(0);

        league = contest.getLeague().getAbbreviation();
        entryFee = contest.getEntryFee();
        currentEntries = contest.getCurrentEntries();

        int max = Integer.MIN_VALUE;
        for (ContestPayout contestPayout : contest.getContestPayouts()) {
            if (contestPayout.getTrailingPosition() > max) {
                max = contestPayout.getTrailingPosition();
            }
        }
        numPaid = max;
        type = contest.getContestType().getAbbr();
        duplicateEntries = entriesForLineup.size();

        startTime = contest.getStartTime();
        salaryCap = contest.getSalaryCap();

        unitsRemaining = DaoFactory.getContestDao().calculateUnitsRemaining(entriesForLineup.get(0));
        projectedFirstMoneyPoints = new BigDecimal(100);
        projectedLastMoneyPoints = new BigDecimal(80);
        currentPoints = new BigDecimal(entry.getPoints());     // If multi-entry, which entry?
        currentPoints = currentPoints.setScale(2, RoundingMode.HALF_EVEN);

        projectedPoints = new BigDecimal(95);

        int bestPosition = Integer.MAX_VALUE;
        for (Entry currEntry : entriesForLineup) {
            int p = allEntries.indexOf(currEntry);
            if (p < bestPosition) {
                bestPosition = p;
            }
        }
        currentPosition = bestPosition + 1;
        projectedPosition = 1;
        currentWinnings = determineCurrentWinnings(contest.getContestPayouts(), currentPosition);
//        projectedWinnings = currentWinnings;

        /*
         * Bring in the current performance data kept track of in the lineup.  We also need to set up the projected
         * performance data.  For now we'll just carry over the last value from current data.
         */
        currentPerformanceData = new ArrayList<>();
        projectedPerformanceData = new ArrayList<>();
        processPerformanceData(lineup);
    }

    /**
     * Convert current performance data in lineup into an array consumable by the front end, and fill out the
     * projected performance.
     *
     * @param lineup The lineup containing performance data.
     */
    private void processPerformanceData(Lineup lineup) {
        try {
            ArrayList<BigDecimal> current = mapper.readValue(lineup.getPerformanceData(), typeReference);
            ArrayList<BigDecimal> projected = mapper.readValue(lineup.getProjectedPerformanceData(), typeReference);

            int i = 0;
            for (BigDecimal value : current) {
                List<BigDecimal> l = new ArrayList<>();
                l.add(new BigDecimal(i));
                l.add(value);
                currentPerformanceData.add(l);

                i++;
            }
            for (BigDecimal value : projected) {
                List<BigDecimal> l = new ArrayList<>();
                l.add(new BigDecimal(i));
                l.add(value);
                projectedPerformanceData.add(l);

                i++;
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * Determines the winnings that an entry would get if their position were to hold.
     *
     * @param contestPayouts The list of all payouts for the contest.
     * @param position       The position of the entry.
     * @return The payout amount that the entry is in line for.
     */
    private int determineCurrentWinnings(List<ContestPayout> contestPayouts, int position) {
        for (ContestPayout payout : contestPayouts) {
            if (payout.getLeadingPosition() <= position && position <= payout.getTrailingPosition()) {
                return payout.getPayoutAmount();
            }
        }

        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public int getCurrentEntries() {
        return currentEntries;
    }

    public void setCurrentEntries(int currentEntries) {
        this.currentEntries = currentEntries;
    }

    public int getNumPaid() {
        return numPaid;
    }

    public void setNumPaid(int numPaid) {
        this.numPaid = numPaid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDuplicateEntries() {
        return duplicateEntries;
    }

    public void setDuplicateEntries(int duplicateEntries) {
        this.duplicateEntries = duplicateEntries;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getUnitsRemaining() {
        return unitsRemaining;
    }

    public void setUnitsRemaining(int unitsRemaining) {
        this.unitsRemaining = unitsRemaining;
    }

    public BigDecimal getProjectedFirstMoneyPoints() {
        return projectedFirstMoneyPoints;
    }

    public void setProjectedFirstMoneyPoints(BigDecimal projectedFirstMoneyPoints) {
        this.projectedFirstMoneyPoints = projectedFirstMoneyPoints;
    }

    public BigDecimal getProjectedLastMoneyPoints() {
        return projectedLastMoneyPoints;
    }

    public void setProjectedLastMoneyPoints(BigDecimal projectedLastMoneyPoints) {
        this.projectedLastMoneyPoints = projectedLastMoneyPoints;
    }

    public BigDecimal getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(BigDecimal currentPoints) {
        this.currentPoints = currentPoints;
    }

    public BigDecimal getProjectedPoints() {
        return projectedPoints;
    }

    public void setProjectedPoints(BigDecimal projectedPoints) {
        this.projectedPoints = projectedPoints;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getProjectedPosition() {
        return projectedPosition;
    }

    public void setProjectedPosition(int projectedPosition) {
        this.projectedPosition = projectedPosition;
    }

    public int getCurrentWinnings() {
        return currentWinnings;
    }

    public void setCurrentWinnings(int currentWinnings) {
        this.currentWinnings = currentWinnings;
    }

    public int getProjectedWinnings() {
        return projectedWinnings;
    }

    public void setProjectedWinnings(int projectedWinnings) {
        this.projectedWinnings = projectedWinnings;
    }

    public List<List<BigDecimal>> getCurrentPerformanceData() {
        return currentPerformanceData;
    }

    public void setCurrentPerformanceData(List<List<BigDecimal>> currentPerformanceData) {
        this.currentPerformanceData = currentPerformanceData;
    }

    public List<List<BigDecimal>> getProjectedPerformanceData() {
        return projectedPerformanceData;
    }

    public void setProjectedPerformanceData(List<List<BigDecimal>> projectedPerformanceData) {
        this.projectedPerformanceData = projectedPerformanceData;
    }

    public int getSalaryCap() {
        return salaryCap;
    }

    public void setSalaryCap(int salaryCap) {
        this.salaryCap = salaryCap;
    }
}
