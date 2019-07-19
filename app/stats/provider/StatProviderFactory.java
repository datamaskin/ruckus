package stats.provider;

import common.GlobalConstants;
import play.Play;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mgiles on 5/24/14.
 */
public class StatProviderFactory {

    private static final Map<String, IStatProvider> providers = new HashMap<>();

    public static IStatProvider getStatsProvider(String leagueAbbreviation)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String activeProvider = "stats.provider." + leagueAbbreviation.toLowerCase() + "."
                + Play.application()
                .configuration()
                .getString(GlobalConstants.STATS_KEY_ACTIVE_PROVIDER) + leagueAbbreviation.toUpperCase();
        IStatProvider provider = providers.get(activeProvider);
        if (provider == null) {
            provider = (IStatProvider) Class.forName(activeProvider).newInstance();
            providers.put(activeProvider, provider);
        }
        return provider;
    }

    public static IStatProvider getMLBProvider()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String leagueAbbreviation = "MLB";
        return getStatsProvider(leagueAbbreviation);
    }

    public static IStatProvider getNFLProvider()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String leagueAbbreviation = "NFL";
        return getStatsProvider(leagueAbbreviation);
    }
}
