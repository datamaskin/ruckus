package stats.manager.mlb;

import common.GlobalConstants;
import models.sports.Athlete;
import play.Logger;
import stats.manager.StatsEventManager;
import stats.parser.AthleteStatsParser;
import stats.parser.mlb.BattingParser;
import stats.parser.mlb.FieldingParser;
import stats.parser.mlb.PitchingParser;
import stats.provider.mlb.StatsIncProviderMLB;
import stats.translator.mlb.FantasyPointTranslator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 7/11/14.
 */
public class MlbEventManager extends StatsEventManager {

    private FantasyPointTranslator fantasyPointTranslator;

    public MlbEventManager(FantasyPointTranslator fantasyPointTranslator) {
        this.fantasyPointTranslator = fantasyPointTranslator;
    }

    public void process() throws Exception {

        BattingParser battingParser = new BattingParser(fantasyPointTranslator);
        FieldingParser fieldingParser = new FieldingParser();
        PitchingParser pitchingParser = new PitchingParser(fantasyPointTranslator);

        List<Athlete> athletes = getAthletes();

        for(Athlete athlete: athletes){
            Map<String, String> map = new HashMap<>();
            map.put(GlobalConstants.STATS_INC_KEY_RESOURCE,
                    String.format("stats/players/%d/events", athlete.getStatProviderId()));
            map.put("season", String.valueOf(LocalDate.now().getYear()));

            try {
                String results = new StatsIncProviderMLB().getStats(map);

                battingParser.parse(results);
                fieldingParser.parse(results);
                pitchingParser.parse(results);

                Logger.info("Processed MLB stats for " + athlete.getFirstName() + " " + athlete.getLastName());
            }
            catch(Exception e) {
                Logger.error("Unable to process stat update for " + athlete.getFirstName() + " " + athlete.getLastName() + "(" + athlete.getStatProviderId() + "): " + e.getMessage());
            }
        }
    }

    private List<Athlete> getAthletes() throws Exception {
        Map<String, String> map1 = new HashMap<>();
        map1.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "participants");
        String results1 =new StatsIncProviderMLB().getStats(map1);
        return new AthleteStatsParser().parse(results1);
    }
}
