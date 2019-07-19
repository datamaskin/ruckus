package distributed;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import common.GlobalConstants;
import org.springframework.context.ApplicationContext;
import play.Logger;
import play.Play;
import simulator.ContestSimulationManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mgiles on 5/8/14.
 */
public class DistributedServices {

    private static DistributedServices singleton;
    private static HazelcastInstance instance;
    private static final ScheduledExecutorService taskScheduler = Executors.newScheduledThreadPool(50);
    private static final ScheduledExecutorService masterChecker = Executors.newSingleThreadScheduledExecutor();
    private static String DEFAULT_EXECUTOR = "DEFAULT_EXECUTOR";
    private static boolean MASTER = false;
    private static Map<String, DistributedTopic> topics = new HashMap<>();
    private static Map<String, DistributedSocket> sockets = new HashMap<>();

    private static final Runnable masterTask = new Runnable() {
        @Override
        public void run() {
            try {
                if (!getInstance().getLifecycleService().isRunning()) {
                    return;
                }
                Member localMember = getInstance().getCluster().getLocalMember();
                // First member in the list is the oldest, thus is the master
                Iterator<Member> it = getInstance().getCluster().getMembers().iterator();
                Member master = it.next();
                while (master == null && it.hasNext()) {
                    master = it.next();
                }

                if (localMember.equals(master) && !MASTER) {
                    Logger.info("This instance is the master out of " + getInstance().getCluster().getMembers().size() + " instances");
                }

                if (localMember.equals(master)) {
                    MASTER = true;
                    try {
                        for (DistributedSocket socket : sockets.values()) {
                            if (!socket.isConnected()) {
                                socket.start();
                            }
                        }
                    } catch (Exception e) {
                        //masterTimer.cancel();
                        Logger.error(e.getMessage());
                    }
                } else {
                    MASTER = false;
                    try {
                        for (DistributedSocket socket : sockets.values()) {
                            if (socket.isConnected()) {
                                socket.stop();
                            }
                        }
                    } catch (Exception e) {
                        //masterTimer.cancel();
                        Logger.error(e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    static {
        try {
            String configString = Play.application().configuration().getString(GlobalConstants.CONFIG_DISTRIBUTED_CONFIG);
            File configFile = Play.application().getFile(configString);
            Logger.info("Using config file : " + configFile.getPath());
            FileInputStream fis = null;
            fis = new FileInputStream(configFile);
            Config config = new XmlConfigBuilder(fis).build();
            //int minClusterSize = Play.application().configuration().getInt("hazelcast.initial.min.cluster.size");
            //config.setProperty("hazelcast.initial.min.cluster.size", String.valueOf(minClusterSize));

            String healthLevel = Play.application().configuration().getString("hazelcast.health.monitoring.level");
            config.setProperty("hazelcast.health.monitoring.level", healthLevel);
            instance = Hazelcast.newHazelcastInstance(config);

            if (!ContestSimulationManager.isSimulation()) {
                // Add the default nfl socket
                sockets.put(GlobalConstants.SPORT_NFL, new DistributedSocket(GlobalConstants.SPORT_NFL));

                // Add the mlb socket
                sockets.put(GlobalConstants.SPORT_MLB, new DistributedSocket(GlobalConstants.SPORT_MLB));
            }

            // Check every N seconds to determine if we are the master or have recently become the master
            // If so, we assume the responsibility for executing the tasks

            masterChecker.scheduleAtFixedRate(masterTask, 10, 5, TimeUnit.SECONDS);
            Logger.info("Created Distributed Service: " + instance.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DistributedServices get() {
        if (singleton == null) {
            singleton = new DistributedServices();
        }

        return singleton;
    }

    public static void setContext(ApplicationContext context) {
        DistributedServices.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    private static ApplicationContext context;

    public static ConcurrentMap<String, Object> getHttpSession(String sessionId) {
        return instance.getMap(sessionId);
    }

    public static void removeHttpSession(String sessionId) {
        getHttpSession(sessionId).clear();
    }

    public static HazelcastInstance getInstance() {
        return instance;
    }

    public static IExecutorService getExecutorService() {
        return instance.getExecutorService(DEFAULT_EXECUTOR);
    }

    public static boolean isMaster() {
        return MASTER;
    }

    public Map<String, DistributedTopic> getTopics() {
        return topics;
    }

    public Map<String, DistributedSocket> getSockets() {
        return sockets;
    }

    public static ScheduledExecutorService getTaskScheduler() {
        return taskScheduler;
    }

}
