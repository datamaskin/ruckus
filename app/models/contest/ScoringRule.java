package models.contest;

import models.sports.League;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Represents a single rule for scoring an event in a SportEvent.
 */
@Entity
public class ScoringRule {

    @Id
    private int id;

    @Constraints.Required
    @Column(nullable = false)
    private String ruleName;

    @ManyToOne
    @Constraints.Required
    @Column(nullable = false)
    private League league;

    @Constraints.Required
    @Column(nullable = false, columnDefinition = "Decimal(10,2)")
    private BigDecimal scoringFactor;

    public ScoringRule() {
    }

    public ScoringRule(String ruleName, League league, BigDecimal scoringFactor) {
        this.ruleName = ruleName;
        this.league = league;
        this.scoringFactor = scoringFactor;
    }

    public BigDecimal getScoringFactor() {
        return scoringFactor;
    }

    public void setScoringFactor(BigDecimal scoringFactor) {
        this.scoringFactor = scoringFactor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }
}
