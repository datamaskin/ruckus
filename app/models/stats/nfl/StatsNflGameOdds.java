package models.stats.nfl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by mwalsh on 7/4/14.
 */

@Entity
public class StatsNflGameOdds {

    @Id
    private int id;
    @Column(unique = true)
    private int statsEventId;

    private Date linesChangeDate;
    private int openingFavoriteTeamId;
    private int currentFavoriteTeamId;
    private Integer openingFavoriteMoney;
    private Integer currentFavoriteMoney;
    private Float openingFavoritePoints;
    private Float currentFavoritePoints;
    private Integer openingAwayMoney;
    private Integer currentAwayMoney;
    private Integer openingHomeMoney;
    private Integer currentHomeMoney;
    private Integer openingOverMoney;
    private Integer currentOverMoney;
    private Float openingTotal;
    private Float currentTotal;
    private Integer openingUnderMoney;
    private Integer currentUnderMoney;
    private Integer openingUnderdogMoney;
    private Integer currentUnderdogMoney;

    public int getStatsEventId() {
        return statsEventId;
    }

    public void setStatsEventId(int statsEventId) {
        this.statsEventId = statsEventId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLinesChangeDate() {
        return linesChangeDate;
    }

    public void setLinesChangeDate(Date linesChangeDate) {
        this.linesChangeDate = linesChangeDate;
    }

    public int getOpeningFavoriteTeamId() {
        return openingFavoriteTeamId;
    }

    public void setOpeningFavoriteTeamId(int openingFavoriteTeamId) {
        this.openingFavoriteTeamId = openingFavoriteTeamId;
    }

    public int getCurrentFavoriteTeamId() {
        return currentFavoriteTeamId;
    }

    public void setCurrentFavoriteTeamId(int currentFavoriteTeamId) {
        this.currentFavoriteTeamId = currentFavoriteTeamId;
    }

    public Integer getOpeningFavoriteMoney() {
        return openingFavoriteMoney;
    }

    public void setOpeningFavoriteMoney(Integer openingFavoriteMoney) {
        this.openingFavoriteMoney = openingFavoriteMoney;
    }

    public Integer getCurrentFavoriteMoney() {
        return currentFavoriteMoney;
    }

    public void setCurrentFavoriteMoney(Integer currentFavoriteMoney) {
        this.currentFavoriteMoney = currentFavoriteMoney;
    }

    public Float getOpeningFavoritePoints() {
        return openingFavoritePoints;
    }

    public void setOpeningFavoritePoints(Float openingFavoritePoints) {
        this.openingFavoritePoints = openingFavoritePoints;
    }

    public Float getCurrentFavoritePoints() {
        return currentFavoritePoints;
    }

    public void setCurrentFavoritePoints(Float currentFavoritePoints) {
        this.currentFavoritePoints = currentFavoritePoints;
    }

    public Integer getOpeningAwayMoney() {
        return openingAwayMoney;
    }

    public void setOpeningAwayMoney(Integer openingAwayMoney) {
        this.openingAwayMoney = openingAwayMoney;
    }

    public Integer getCurrentAwayMoney() {
        return currentAwayMoney;
    }

    public void setCurrentAwayMoney(Integer currentAwayMoney) {
        this.currentAwayMoney = currentAwayMoney;
    }

    public Integer getOpeningHomeMoney() {
        return openingHomeMoney;
    }

    public void setOpeningHomeMoney(Integer openingHomeMoney) {
        this.openingHomeMoney = openingHomeMoney;
    }

    public Integer getCurrentHomeMoney() {
        return currentHomeMoney;
    }

    public void setCurrentHomeMoney(Integer currentHomeMoney) {
        this.currentHomeMoney = currentHomeMoney;
    }

    public Integer getOpeningOverMoney() {
        return openingOverMoney;
    }

    public void setOpeningOverMoney(Integer openingOverMoney) {
        this.openingOverMoney = openingOverMoney;
    }

    public Integer getCurrentOverMoney() {
        return currentOverMoney;
    }

    public void setCurrentOverMoney(Integer currentOverMoney) {
        this.currentOverMoney = currentOverMoney;
    }

    public Float getOpeningTotal() {
        return openingTotal;
    }

    public void setOpeningTotal(Float openingTotal) {
        this.openingTotal = openingTotal;
    }

    public Float getCurrentTotal() {
        return currentTotal;
    }

    public void setCurrentTotal(Float currentTotal) {
        this.currentTotal = currentTotal;
    }

    public Integer getOpeningUnderMoney() {
        return openingUnderMoney;
    }

    public void setOpeningUnderMoney(Integer openingUnderMoney) {
        this.openingUnderMoney = openingUnderMoney;
    }

    public Integer getCurrentUnderMoney() {
        return currentUnderMoney;
    }

    public void setCurrentUnderMoney(Integer currentUnderMoney) {
        this.currentUnderMoney = currentUnderMoney;
    }

    public Integer getOpeningUnderdogMoney() {
        return openingUnderdogMoney;
    }

    public void setOpeningUnderdogMoney(Integer openingUnderdogMoney) {
        this.openingUnderdogMoney = openingUnderdogMoney;
    }

    public Integer getCurrentUnderdogMoney() {
        return currentUnderdogMoney;
    }

    public void setCurrentUnderdogMoney(Integer currentUnderdogMoney) {
        this.currentUnderdogMoney = currentUnderdogMoney;
    }
}
