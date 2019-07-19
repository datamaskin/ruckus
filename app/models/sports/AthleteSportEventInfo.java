package models.sports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.GlobalConstants;
import play.Logger;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model to represent information for the athlete in the context of a single sport event.  This would
 * include information such as fantasy points and salary.
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sport_event_id", "athlete_id"})
})
public class AthleteSportEventInfo {

    public static final String ID = "id";

    @Id
    @Column(name = AthleteSportEventInfo.ID)
    private int id;

    /**
     * The SportEvent that this instance is associated with.
     */
    @OneToOne
    @Constraints.Required
    @Column(nullable = false)
    private SportEvent sportEvent;

    /**
     * The Athlete that this instance is associated with.
     */
    @ManyToOne
    @Constraints.Required
    @Column(nullable = false)
    private Athlete athlete;

    /**
     * The number of fantasy points accumulated by the athlete for the specified SportEvent so far.
     */
    @Column(nullable = false, columnDefinition = "Decimal(10,2)")
    private BigDecimal fantasyPoints;

    /**
     * The JSON representation of the box score for an athlete.
     */
    @Column(columnDefinition = "text")
    private String stats;

    /**
     * The JSON representation of a list of events that caused the athlete's fantasy point total to change.
     */
    @Column(columnDefinition = "text")
    private String timeline;

    private int indicator;

    public AthleteSportEventInfo() {
    }

    public AthleteSportEventInfo(SportEvent sportEvent, Athlete athlete, BigDecimal fantasyPoints, String stats, String timeline) {
        this.sportEvent = sportEvent;
        this.athlete = athlete;
        this.fantasyPoints = fantasyPoints;
        this.stats = stats;
        this.timeline = timeline;
        this.indicator = GlobalConstants.INDICATOR_TEAM_OFF_FIELD;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public BigDecimal getFantasyPoints() {
        return fantasyPoints;
    }

    public void setFantasyPoints(BigDecimal fantasyPoints) {
        this.fantasyPoints = fantasyPoints;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public int getIndicator() {
        return indicator;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public String determineStatsForDisplay() {
        if(sportEvent.getLeague().equals(League.MLB)) {
            return stats;
        }

        TypeReference<List<Map<String, Object>>> listTypeReference = new TypeReference<List<Map<String, Object>>>() {};
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> result;
        String resultStr = stats;

        try {
            List<Map<String, Object>> statsList = mapper.readValue(stats, listTypeReference);
            if(sportEvent.getLeague().equals(League.NFL) && athlete.getPositions().get(0).equals(Position.FB_DEFENSE)) {
                if(statsList.isEmpty()) {
                    return mapper.writeValueAsString(new ArrayList<>());
                }

                int touchdowns = 0;
                BigDecimal touchdownsFpp = BigDecimal.ZERO;

                result = new ArrayList<>();
                /*
                 * initialize the array that's going to hold our display-able box score.  Each individual touchdown
                 * type is condensed into a general "Touchdowns" category.
                 */
                for(int i=0; i<7; i++)  result.add(null);

                for(Map<String, Object> entry: statsList) {
                    if(entry.get("name").equals(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL) ||
                            entry.get("name").equals(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL) ||
                            entry.get("name").equals(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL)) {
                        touchdowns += (int) entry.get("amount");
                        BigDecimal updatedFpp = (entry.get("fpp") instanceof Integer) ? new BigDecimal((Integer) entry.get("fpp")) : new BigDecimal((Double) entry.get("fpp"));
                        touchdownsFpp = touchdownsFpp.add(updatedFpp);
                    }
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL))         result.set(0, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_SAFETY_LABEL))                 result.set(2, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL))        result.set(3, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL))       result.set(4, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL))           result.set(5, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_SACK_LABEL))                   result.set(6, entry);
                }

                touchdownsFpp = touchdownsFpp.setScale(2, RoundingMode.HALF_EVEN);

                Map<String, Object> generalTouchdownEntry = new HashMap<>();
                generalTouchdownEntry.put("name", GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL);
                generalTouchdownEntry.put("abbr", GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
                generalTouchdownEntry.put("id", GlobalConstants.SCORING_NFL_NAME_TO_ID_MAP.get(GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL));
                generalTouchdownEntry.put("fpp", touchdownsFpp);
                generalTouchdownEntry.put("amount", touchdowns);

                result.set(1, generalTouchdownEntry);

                /*
                 * The list resulting from this loop will be what is sent for display to the client.  We don't want to
                  * show any stats that have a 0 for the amount (with the exception of Points Allowed).  Therefore, we check
                  * to see if the stat has amount 0 and, if so, remove it from the list.
                  *
                  * For Points Allowed, we only want to hide the 0 if the game hasn't started yet.  We can determine this
                  * by checking the number of fantasy points there are (At kickoff, each defense starts with 12 points).
                  * Also, since the only way for the team's fantasy points to go down is to be scored against, we know that
                  * we'll either always have a non-zero Points Against value, or we'll have a non-zero fantasy point total.
                  * Either way, we know it's OK to display it.
                 */
                int index = 0;
                while(index < result.size()) {
                    Map<String, Object> entry = result.get(index);
                    boolean nonPointsAllowedZeroAmount = !entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL) && entry.get("amount") instanceof Integer &&
                            (Integer) entry.get("amount") == 0;
                    boolean pointsAllowedZeroAmount = entry.get("name").equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)
                            && entry.get("amount") instanceof Integer && (Integer) entry.get("amount") == 0
                            && fantasyPoints.compareTo(BigDecimal.ZERO) == 0;
                    if(entry == null || nonPointsAllowedZeroAmount || pointsAllowedZeroAmount)
                        result.remove(index);
                    else
                        index++;
                }

                resultStr = mapper.writeValueAsString(result);
            }
            else if(sportEvent.getLeague().equals(League.NFL) && !athlete.getPositions().get(0).equals(Position.FB_DEFENSE)) {
                if(statsList.isEmpty()) {
                    return mapper.writeValueAsString(new ArrayList<>());
                }

                int nonPassingTouchdowns = 0;
                BigDecimal nonPassingTouchdownFpp = BigDecimal.ZERO;

                result = new ArrayList<>();
                for(int i=0; i<8; i++)  result.add(null);

                for(Map<String, Object> entry: statsList) {
                    if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL) ||
                            entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL) ||
                            entry.get("name").equals(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL) ||
                            entry.get("name").equals(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL)) {
                        nonPassingTouchdowns += (int) entry.get("amount");

                        BigDecimal updatedFpp = (entry.get("fpp") instanceof Integer) ? new BigDecimal((Integer) entry.get("fpp")) : new BigDecimal((Double) entry.get("fpp"));
                        nonPassingTouchdownFpp = nonPassingTouchdownFpp.add(updatedFpp);
                    }
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL))          result.set(1, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL))       result.set(2, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL))              result.set(3, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RECEPTION_LABEL))                  result.set(4, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL))            result.set(5, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL))              result.set(6, entry);
                    else if(entry.get("name").equals(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL))                result.set(7, entry);
                }

                nonPassingTouchdownFpp = nonPassingTouchdownFpp.setScale(2, RoundingMode.HALF_EVEN);

                Map<String, Object> generalTouchdownEntry = new HashMap<>();
                generalTouchdownEntry.put("name", GlobalConstants.SCORING_NFL_GENERAL_TOUCHDOWN_LABEL);
                generalTouchdownEntry.put("abbr", GlobalConstants.SCORING_NFL_NON_PASSING_TOUCHDOWN_ABBR);
                generalTouchdownEntry.put("fpp", nonPassingTouchdownFpp);
                generalTouchdownEntry.put("amount", nonPassingTouchdowns);

                result.set(0, generalTouchdownEntry);

                int index = 0;
                while(index < result.size()) {
                    Map<String, Object> entry = result.get(index);
                    if(entry == null || (entry.get("amount") instanceof Integer && (Integer) entry.get("amount") == 0))
                        result.remove(index);
                    else
                        index++;
                }

                resultStr = mapper.writeValueAsString(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultStr;
    }

    /**
     * Determines whether the athlete's matchup (i.e. NE vs BAL) should be displayed as NE@BAL or NEvBAL.
     *
     * @return      The matchup, properly formatted for home or away.
     */
    public String determineMatchupDisplay() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};

        List<Team> teams = sportEvent.getTeams();

        try {
            Map<String, Object> shortDescription = mapper.readValue(sportEvent.getShortDescription(), typeReference);
            Integer homeTeamId = Integer.parseInt((String) shortDescription.get("homeId"));
            Integer athleteTeamId = athlete.getTeam().getStatProviderId();

            if(athleteTeamId.equals(homeTeamId)) {
                return String.format("%sv%s", teams.get(0).getStatProviderId() == athleteTeamId ? teams.get(0).getAbbreviation().toUpperCase() : teams.get(1).getAbbreviation().toUpperCase(),
                        teams.get(1).getStatProviderId() == athleteTeamId ? teams.get(0).getAbbreviation().toUpperCase() : teams.get(1).getAbbreviation().toUpperCase());
            }
            else {
                return String.format("%s@%s", teams.get(0).getStatProviderId() == athleteTeamId ? teams.get(0).getAbbreviation().toUpperCase() : teams.get(1).getAbbreviation().toUpperCase(),
                        teams.get(1).getStatProviderId() == athleteTeamId ? teams.get(0).getAbbreviation().toUpperCase() : teams.get(1).getAbbreviation().toUpperCase());
            }
        } catch (IOException e) {
            Logger.error("Unable to generate matchup display correctly", e);
        }

        return String.format("%sv%s", teams.get(0).getAbbreviation(), teams.get(1).getAbbreviation());
    }

    /**
     * Determine the opponent for this athlete in the associated sport event.
     *
     * @return
     */
    public String determineOpponent() {
        List<Team> teams = sportEvent.getTeams();
        if(athlete.getTeam().getId() == teams.get(0).getId()) {
            return teams.get(1).getAbbreviation();
        }
        else {
            return teams.get(0).getAbbreviation();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AthleteSportEventInfo that = (AthleteSportEventInfo) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
