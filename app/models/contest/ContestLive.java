package models.contest;

import models.sports.League;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * Model class representing an instance of the contest data to be sent to a user who connects
 * to the /contestlive endpoint.
 */
public class ContestLive {
    private String id;
    private League league;
    private int entryFee;
    private int numEntries;
    private int numPaid;
    private String type;
    private int duplicateEntries;
    private Date startTime;
    private int minRemaining;
    private BigDecimal projectedFirstMoneyPoints;
    private BigDecimal projectedLastMoneyPoints;
    private BigDecimal currentPoints;
    private BigDecimal projectedPoints;
    private int currentPosition;
    private int projectedPosition;
    private int currentWinnings; // As cents
    private int projectedWinnings;
    private ArrayList<ArrayList<Integer>> currentPerformanceData;
    private ArrayList<ArrayList<Integer>> projectedPerformanceData;

    public ContestLive() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public int getNumEntries() {
        return numEntries;
    }

    public void setNumEntries(int numEntries) {
        this.numEntries = numEntries;
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

    public int getMinRemaining() {
        return minRemaining;
    }

    public void setMinRemaining(int minRemaining) {
        this.minRemaining = minRemaining;
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

    public ArrayList<ArrayList<Integer>> getCurrentPerformanceData() {
        return currentPerformanceData;
    }

    public void setCurrentPerformanceData(ArrayList<ArrayList<Integer>> currentPerformanceData) {
        this.currentPerformanceData = currentPerformanceData;
    }

    public ArrayList<ArrayList<Integer>> getProjectedPerformanceData() {
        return projectedPerformanceData;
    }

    public void setProjectedPerformanceData(ArrayList<ArrayList<Integer>> projectedPerformanceData) {
        this.projectedPerformanceData = projectedPerformanceData;
    }
}
