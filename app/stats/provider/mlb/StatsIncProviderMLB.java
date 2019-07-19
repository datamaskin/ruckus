package stats.provider.mlb;

import play.Play;
import stats.provider.StatsIncProvider;

/**
 * Created by mgiles on 5/24/14.
 */
public class StatsIncProviderMLB extends StatsIncProvider {
    private static final String KEY = Play.application().configuration().getString("stats.inc.key");
    private static final String SECRET = Play.application().configuration().getString("stats.inc.secret");
    private static final String SPORTNAME = "baseball";
    private static final String LEAGUEABBREVIATION = "MLB";

    @Override
    protected String getKey() {
        return KEY;
    }

    @Override
    protected String getSecret() {
        return SECRET;
    }

    @Override
    protected String getSportName() {
        return SPORTNAME;
    }

    @Override
    protected String getLeagueAbbreviation() {
        return LEAGUEABBREVIATION;
    }
}
