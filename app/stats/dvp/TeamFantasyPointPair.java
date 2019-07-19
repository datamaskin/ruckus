package stats.dvp;

import models.sports.Team;

import java.math.BigDecimal;

/**
 * Used to pair a team with its total fantasy points accumulated.  This class is used so
 * we can more easily sort the team ranks without needing to implement a sorting algorithm.
 */
public class TeamFantasyPointPair {
    private Team team;
    private BigDecimal fantasyPoints;

    public TeamFantasyPointPair(Team team, BigDecimal fantasyPoints) {
        this.team = team;
        this.fantasyPoints = fantasyPoints;
    }

    public Team getTeam() {
        return team;
    }

    public BigDecimal getFantasyPoints() {
        return fantasyPoints;
    }
}
