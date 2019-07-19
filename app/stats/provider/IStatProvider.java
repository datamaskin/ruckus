package stats.provider;

import java.util.Map;

/**
 * Created by mgiles on 5/24/14.
 */
public interface IStatProvider {

    public String getStats(Map<String, String> params) throws Exception;

}
