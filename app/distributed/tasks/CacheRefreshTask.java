package distributed.tasks;

import service.EdgeCacheService;

/**
 * Created by mgiles on 8/20/14.
 */
public class CacheRefreshTask extends DistributedTask {
    @Override
    protected String execute() throws Exception {
        EdgeCacheService.flushAllCaches();
        new CachePopulatorTask().execute();
        return "ok";
    }
}
