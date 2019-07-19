package service;

import com.hazelcast.spring.cache.HazelcastCacheManager;
import distributed.DistributedServices;

/**
 * Created by mgiles on 7/16/14.
 */
public class HazelcastCache extends HazelcastCacheManager {
    public HazelcastCache() {
        // Use our own configured Hazelcast instance
        super(DistributedServices.getInstance());
    }
}
