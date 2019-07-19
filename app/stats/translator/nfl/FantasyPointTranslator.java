package stats.translator.nfl;

import service.ScoringRulesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.GlobalConstants;
import play.Logger;
import stats.translator.IFantasyPointTranslator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Translates raw socket data from the stat provider into fantasy points.
 */
public class FantasyPointTranslator implements IFantasyPointTranslator {

    private ScoringRulesService scoringRulesManager;

    public FantasyPointTranslator(ScoringRulesService scoringRulesManager) {
        this.scoringRulesManager = scoringRulesManager;
    }

    @Override
    public BigDecimal calculateFantasyPoints(Map<String, BigDecimal> stats) {
        BigDecimal total = new BigDecimal(0.0);
        Map<String, BigDecimal> scoringRules;
        try {
            scoringRules = scoringRulesManager.retrieveScoringRulesAsMaps().get(GlobalConstants.SPORT_NFL);
        } catch (JsonProcessingException e) {
            Logger.error(e.getMessage());
            return new BigDecimal(-1);
        }

        for (Map.Entry<String, BigDecimal> entry : stats.entrySet()) {
            if(entry.getKey().equals(GlobalConstants.SCORING_NFL_POINTS_ALLOWED_LABEL)) {
                total = total.add(GlobalConstants.SCORING_NFL_DEFENSE_INITIAL_POINTS);
            }
            total = total.add(scoringRules.get(entry.getKey()).multiply(entry.getValue()));
        }

        total = total.setScale(2, RoundingMode.HALF_EVEN);
        return total;
    }

    @Override
    public List<BigDecimal> determineFantasyPointIncrementForEvent(Map<String, Integer> eventData) {
        int eventId = eventData.get("eventId");
        Integer eventType = eventData.get("eventType");
        int yards = eventData.get("yards");
        int points = eventData.get("points");
        int defenseId = eventData.get("defenseId");
        int possessionBefore = eventData.get("possessionBefore");
        int possessionAfter = eventData.get("possessionAfter");

        List<BigDecimal> fantasyPoints = new ArrayList<>();
        BigDecimal fp = new BigDecimal("0");
        BigDecimal defense = new BigDecimal("0");

        try {
            Map<String, BigDecimal> scoringRules = scoringRulesManager.retrieveScoringRulesAsMaps().get(GlobalConstants.SPORT_NFL);

            /*
             * Rush
             */
            if(eventId == 1) {
                fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_RUSHING_YARDS_LABEL).multiply(new BigDecimal(yards)));
                if(points == 6) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_RUSHING_TOUCHDOWN_LABEL));
                }
            }
            /*
             * Completed Pass
             */
            else if(eventId == 2) {
                fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_PASSING_YARDS_LABEL).multiply(new BigDecimal(yards)));
                if(points == 6) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_PASSING_TOUCHDOWN_LABEL));
                }
            }
            /*
             * Kickoff return
             */
            else if(eventId == 4 || eventId == 31) {
                if(points == 6) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL));
                    defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_KICK_RETURN_TOUCHDOWN_LABEL));
                }
            }
            /*
             * Punt return
             */
            else if(eventId == 5) {
                if(points == 6) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL));
                    defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_PUNT_RETURN_TOUCHDOWN_LABEL));
                }
            }
            /*
             * Interception
             */
            else if(eventId == 6) {
                fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_INTERCEPTION_LABEL));
            }
            /*
             * Lost fumble
             */
            else if(eventId == 8) {
                fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_LOST_FUMBLE_LABEL));
            }
            /*
             * Safety
             */
            else if(eventId == 9) {
                defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_SAFETY_LABEL));
            }
            /*
             * Blocked field goal or punt
             */
            else if(eventId == 10 || eventId == 11) {
                defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_BLOCKED_KICK_LABEL));
            }
            /*
             * Two-point conversion (passing and rushing)
             * Receptions handled as event 17.
             */
            else if(eventId == 13 || eventId == 14) {
                if(points > 0) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL));
                }
            }
            /*
             * Reception
             *
             * This covers non-scoring, two-point-conversion, and touchdown plays.
             */
            else if(eventId == 17) {
                fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_RECEPTION_LABEL));
                fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_RECEIVING_YARDS_LABEL).multiply(new BigDecimal(yards)));

                if(points == 2) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_TWO_POINT_CONVERSION_LABEL));
                }
                else if(points == 6) {
                    fp = fp.add(scoringRules.get(GlobalConstants.SCORING_NFL_RECEIVING_TOUCHDOWN_LABEL));
                }
            }
            /*
             * Sack
             */
            else if(eventId == 21) {
                defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_SACK_LABEL));
            }
            /*
             * Interception return (DEF)
             */
            else if(eventId == 23) {
                defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_DEF_INTERCEPTION_LABEL));

                if(points == 6) {
                    defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_INTERCEPTION_RETURN_TD_LABEL));
                }
            }
            /*
             * Blocked/Missed field goal or punt
             */
            else if(eventId == 29 || eventId == 30) {
                if(points == 6) {
                    defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_BLOCKED_PUNT_FG_RETURN_TD_LABEL));
                }
            }
            /*
             * Fumble recovery
             */
            else if(eventId == 32) {
                if(possessionBefore != possessionAfter && eventType != 7) {
                    defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_LABEL));

                    if(points == 6) {
                        defense = defense.add(scoringRules.get(GlobalConstants.SCORING_NFL_FUMBLE_RECOVERY_TD_LABEL));
                    }
                }
            }

            fantasyPoints.add(fp);
            fantasyPoints.add(defense);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return fantasyPoints;
    }


}
