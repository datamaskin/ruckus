package stats.translator.mlb;

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

    /**
     * Perform calculation of fantasy points based on the map of stats provided.
     *
     * @param stats A map of stats to translate into Victis fantasy points.
     * @return A BigDecimal representing the number of fantasy points that the stats are worth.
     */
    @Override
    public BigDecimal calculateFantasyPoints(Map<String, BigDecimal> stats) {
        BigDecimal total = new BigDecimal(0.0);
        Map<String, BigDecimal> scoringRules;
        try {
            scoringRules = scoringRulesManager.retrieveScoringRulesAsMaps().get(GlobalConstants.SPORT_MLB);
        } catch (JsonProcessingException e) {
            Logger.error(e.getMessage());
            return new BigDecimal(-1);
        }

        for (Map.Entry<String, BigDecimal> entry : stats.entrySet()) {
            if (!scoringRules.containsKey(entry.getKey()) || entry.getValue() == null) {
                continue;
            }

            if (entry.getKey().equals(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL)) {
                total = total.add(getFantasyPointsForInningsPitched(entry.getValue(), scoringRules));
            } else {
                total = total.add(scoringRules.get(entry.getKey()).multiply(entry.getValue()));
            }
        }

        return total;
    }

    @Override
    public List<BigDecimal> determineFantasyPointIncrementForEvent(Map<String, Integer> eventData) {
        int eventId = eventData.get("eventId");
        int outsBefore = eventData.get("outsBefore");
        int outsAfter = eventData.get("outsAfter");
        int rbi = eventData.get("rbi");

        List<BigDecimal> fantasyPointChanges = new ArrayList<>();

        try {
            Map<String, BigDecimal> scoringRules = scoringRulesManager.retrieveScoringRulesAsMaps().get(GlobalConstants.SPORT_MLB);

            /*
             * Single
             */
            if (eventId == 4) {
                BigDecimal batter = scoringRules.get(GlobalConstants.SCORING_MLB_SINGLE_LABEL);
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }

                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Double
             */
            else if (eventId == 6) {
                BigDecimal batter = scoringRules.get(GlobalConstants.SCORING_MLB_DOUBLE_LABEL);
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }

                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Triple
             */
            else if (eventId == 7) {
                BigDecimal batter = scoringRules.get(GlobalConstants.SCORING_MLB_TRIPLE_LABEL);
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }

                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Home run
             */
            else if (eventId == 8) {
                BigDecimal batter = scoringRules.get(GlobalConstants.SCORING_MLB_HOMERUN_LABEL);
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }

                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_LABEL);
                pitcher = pitcher.add(scoringRules.get(GlobalConstants.SCORING_MLB_EARNED_RUN_LABEL));

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Sacrifice Hit
             */
            else if (eventId == 11) {
                BigDecimal batter = new BigDecimal("0");
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Sacrifice Fly
             */
            else if (eventId == 12) {
                BigDecimal batter = new BigDecimal("0");
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Hit by pitch
             */
            else if (eventId == 13) {
                BigDecimal batter = scoringRules.get(GlobalConstants.SCORING_MLB_HIT_BY_PITCH_LABEL);
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }

                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_PITCHER_HIT_BY_PITCH_LABEL);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }
            /*
             * Walk
             */
            else if (eventId == 14 || eventId == 15) {
                BigDecimal batter = scoringRules.get(GlobalConstants.SCORING_MLB_WALK_LABEL);
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }

                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_PITCHER_WALK_LABEL);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }

            /*
             * Strikeout
             */
            else if (eventId == 16) {
                BigDecimal batter = new BigDecimal("0");
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL).add(scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP));

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }

            /*
             * Grounded into double play
             */
            else if (eventId == 19) {
                BigDecimal batter = new BigDecimal("0");
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP).multiply(new BigDecimal("2"));

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }

            /*
             * Reached base on interference
             */
            else if (eventId == 20 || eventId == 24 || eventId == 25 || eventId == 26 || eventId == 27 || eventId == 28 || eventId == 29) {
                BigDecimal batter = new BigDecimal("0");
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }
                BigDecimal pitcher = new BigDecimal("0");

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }

            /*
             * Strikeout and wild pitch
             */
            else if (eventId == 21 || eventId == 22 || eventId == 23) {
                BigDecimal batter = new BigDecimal("0");
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_STRIKEOUT_LABEL);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }

            /*
             * Reach base on ball hitting runner
             *
             * Fly out
             */
            else if (eventId == 30 || eventId == 31 || eventId == 32 || eventId == 33 || eventId == 34 || eventId == 35 || eventId == 36 || eventId == 37 || eventId == 38) {
                BigDecimal batter = new BigDecimal("0");
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP);

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }

            /*
             * GDP on Bunt
             */
            else if (eventId == 41) {
                BigDecimal batter = new BigDecimal("0");
                if (rbi > 0) {
                    batter = batter.add(scoringRules.get(GlobalConstants.SCORING_MLB_RUN_BATTED_IN_LABEL).multiply(new BigDecimal(rbi)));
                }
                BigDecimal pitcher = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP).multiply(new BigDecimal("2"));

                fantasyPointChanges.add(batter);
                fantasyPointChanges.add(pitcher);
            }


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return fantasyPointChanges;
    }

    /**
     * Special handler for Innings Pitched since it is a pain in the ass to deal with.
     *
     * @param inningsPitched The number of innings pitched.
     * @param scoringRules   A map of scoring rules.
     * @return The total fantasy points earned from innings pitched.
     */
    public BigDecimal getFantasyPointsForInningsPitched(BigDecimal inningsPitched, Map<String, BigDecimal> scoringRules) {
        BigDecimal pointsPerOut = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).divide(new BigDecimal("3"), 1, RoundingMode.HALF_UP);
        int fullInnings = inningsPitched.intValue();
        int partialInnings = inningsPitched.remainder(new BigDecimal("1")).multiply(new BigDecimal("10")).intValue();

        BigDecimal total = scoringRules.get(GlobalConstants.SCORING_MLB_INNING_PITCHED_LABEL).multiply(new BigDecimal(fullInnings));
        total = total.add(pointsPerOut.multiply(new BigDecimal(partialInnings)));

        return total;
    }
}
