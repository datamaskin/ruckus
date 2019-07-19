package stats.manager;

import java.util.Map;

public interface IStatManager {

    /**
     * Attempts to retrieve a cached object with the provided key.
     *
     * @param key       They key used to store the cached object.
     */
    Object retrieveFromCache(String key) throws Exception;

    /**
     * Attempts to retrieve desired stat data from our persistent data storage.
     *
     * @param parameters    The parameters needed to query the data.
     * @return              An object representing the result.
     */
    Object retrieveFromDataSource(Map<String, String> parameters) throws Exception;
  
    void updateDataSourceFromStatProvider(Map<String, String> parameters) throws Exception;
  
}
