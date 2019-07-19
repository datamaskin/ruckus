package models.contest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.sports.League;
import models.sports.SportEventGrouping;
import models.user.User;
import play.Logger;
import utils.projectiongraph.IProjectionGraphHelper;
import utils.projectiongraph.MLBProjectionGraphHelper;
import utils.projectiongraph.NFLProjectionGraphHelper;

import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwalsh on 6/9/14.
 */

@Entity
public class Lineup {

    public static final String ID = "id";
    public static final String LINEUP_NAME = "name";
    public static final String LEAGUE_ID = "league_id";

    @Id
    private int id;

    @Column(nullable = false, name = LINEUP_NAME)
    private String name;

    @ManyToOne
    @Column(nullable = false)
    private User user;

    @ManyToOne
    @Column(name = LEAGUE_ID)
    private League league;

    @OneToMany
    private List<Entry> entries;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LineupSpot> lineupSpots;

    private String performanceData;

    private String projectedPerformanceData;

    @ManyToOne
    private SportEventGrouping sportEventGrouping;

    public Lineup(String name, User user, League league, SportEventGrouping sportEventGrouping) {
        this.name = name;
        this.user = user;
        this.league = league;
        this.sportEventGrouping = sportEventGrouping;

        ObjectMapper mapper = new ObjectMapper();
        List<BigDecimal> performanceData = new ArrayList<>();
        List<BigDecimal> projectedPerformanceData = new ArrayList<>();
        BigDecimal pointsPerUnitTime = null;
        if (league.getAbbreviation().equals(League.MLB.getAbbreviation())) {
            pointsPerUnitTime = new BigDecimal(10.0 / 9.0);
            pointsPerUnitTime = pointsPerUnitTime.setScale(2, RoundingMode.HALF_EVEN);

            for (int i = 1; i < 10; i++) {
                projectedPerformanceData.add(new BigDecimal(i).multiply(pointsPerUnitTime));
                projectedPerformanceData.add(new BigDecimal(i).multiply(pointsPerUnitTime));
            }

            projectedPerformanceData.add(new BigDecimal(9).multiply(pointsPerUnitTime));
            projectedPerformanceData.add(new BigDecimal(9).multiply(pointsPerUnitTime));
        }

        try {
            this.performanceData = mapper.writeValueAsString(performanceData);
            this.projectedPerformanceData = mapper.writeValueAsString(projectedPerformanceData);
        } catch (JsonProcessingException e) {
            Logger.error("Error initializing lineup performance data: " + e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<LineupSpot> getLineupSpots() {
        return lineupSpots;
    }

    public void setLineupSpots(List<LineupSpot> lineupSpots) {
        this.lineupSpots = lineupSpots;
    }

    public String getPerformanceData() {
        return performanceData;
    }

    public void setPerformanceData(String performanceData) {
        this.performanceData = performanceData;
    }

    public String getProjectedPerformanceData() {
        return projectedPerformanceData;
    }

    public void setProjectedPerformanceData(String projectedPerformanceData) {
        this.projectedPerformanceData = projectedPerformanceData;
    }

    public SportEventGrouping getSportEventGrouping() {
        return sportEventGrouping;
    }

    public void setSportEventGrouping(SportEventGrouping sportEventGrouping) {
        this.sportEventGrouping = sportEventGrouping;
    }

    /**
     * Update the performance data and projected performance data based on the incoming
     * fantasy point update.
     *
     * @param fantasyPointUpdate        The value of the update for the fantasy-relevant play.
     * @param unit                      The unit of time that the play occurred at.
     * @throws IOException
     */
    public void updatePerformanceData(BigDecimal fantasyPointUpdate, int unit) throws IOException {
        int graphCapacity = 20;

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<ArrayList<BigDecimal>> typeReference = new TypeReference<ArrayList<BigDecimal>>() {
        };

        List<BigDecimal> data = mapper.readValue(performanceData, typeReference);
        List<BigDecimal> projectedData = mapper.readValue(projectedPerformanceData, typeReference);

        /**
         * Major League Baseball
         */
        IProjectionGraphHelper projectionGraphHelper = null;
        if (league.equals(League.MLB)) {
            projectionGraphHelper = new MLBProjectionGraphHelper(this);
        }
        else if(league.equals(League.NFL)) {
            projectionGraphHelper = new NFLProjectionGraphHelper(this);
        }
        projectionGraphHelper.updatePerformanceData(data, projectedData, fantasyPointUpdate, unit, graphCapacity);

        performanceData = mapper.writeValueAsString(data);
        projectedPerformanceData = mapper.writeValueAsString(projectedData);
    }
}
