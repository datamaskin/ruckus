package simulator;

import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedTopic;
import distributed.topics.BaseStatsUpdateDistributedTopic;
import distributed.topics.mlb.MLBDistributedTopic;
import models.sports.League;
import models.sports.SportEvent;
import models.stats.StatsLiveFeedData;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import play.Logger;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.IUpdateProcessor;
import stats.updateprocessor.nfl.UpdateProcessor;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dmaclean on 7/7/14.
 */
public class SimulationRunner implements Runnable {
    private boolean running;
    private List<SportEvent> sportEvents;
    private DistributedTopic topic;
    private ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

    public SimulationRunner(List<SportEvent> sportEvents) {
        this.sportEvents = sportEvents;

        if (sportEvents.get(0).getLeague().equals(League.MLB)) {
            IFantasyPointTranslator mlbTranslator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);
            IUpdateProcessor mlbStatsUpdateProcessor = context.getBean("MLBStatsUpdateProcessor", stats.updateprocessor.mlb.UpdateProcessor.class);

            topic = new MLBDistributedTopic(GlobalConstants.TOPIC_REALTIME_PREFIX + GlobalConstants.SPORT_MLB, mlbTranslator,
                    mlbStatsUpdateProcessor, DaoFactory.getSportsDao(), DaoFactory.getContestDao(), GlobalConstants.STATS_INC_MLB_SOCKET_ROOT_NODE_NAME);
        } else if (sportEvents.get(0).getLeague().equals(League.NFL)) {
            IFantasyPointTranslator nflTranslator = context.getBean("NFLFantasyPointTranslator", IFantasyPointTranslator.class);
            IUpdateProcessor nflStatsUpdateProcessor = context.getBean("NFLStatsUpdateProcessor", UpdateProcessor.class);

            topic = new BaseStatsUpdateDistributedTopic(GlobalConstants.TOPIC_REALTIME_PREFIX + GlobalConstants.SPORT_NFL, nflTranslator,
                    nflStatsUpdateProcessor, DaoFactory.getSportsDao(), DaoFactory.getContestDao(), GlobalConstants.STATS_INC_NFL_SOCKET_ROOT_NODE_NAME);
        }

        topic.start();
    }

    @Override
    public void run() {
        running = true;

        List<StatsLiveFeedData> liveFeedDataList = DaoFactory.getStatsDao().findLiveFeed(sportEvents);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy - hh:mm");

        for(SportEvent sportEvent: sportEvents) {
            Logger.info(String.format("Streaming %s events for %s vs %s - %s", liveFeedDataList.size(),
                    sportEvent.getTeams().get(0).getAbbreviation(),
                    sportEvent.getTeams().get(1).getAbbreviation(),
                    simpleDateFormat.format(sportEvent.getStartTime())));
        }

        int minutesUntilStreaming = ContestSimulationManager.getWaitTime() + 1;
        while (minutesUntilStreaming > 0) {
            for(SportEvent sportEvent: sportEvents) {
                Logger.info(minutesUntilStreaming + " minutes until streaming of " + sportEvent.getTeams().get(0).getName() + " vs " + sportEvent.getTeams().get(1).getName());
            }
            try {
                Thread.sleep(1000 * 60);
                minutesUntilStreaming--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (StatsLiveFeedData statsLiveFeedData : liveFeedDataList) {
            if (!running) {
                break;
            }

            String[] liveFeedDataChunks = statsLiveFeedData.getData().split("\n");
            if(liveFeedDataChunks.length == 1) {
                String tempData = statsLiveFeedData.getData();
                liveFeedDataChunks = tempData.replaceAll(">", ">\n").split("\n");
            }

            for (String lfd : liveFeedDataChunks) {
                topic.publish(lfd.trim());
            }

            try {
                Logger.info("Streamed message " + statsLiveFeedData.getId() + " to topic");
                Thread.sleep(1000 * ContestSimulationManager.getSocketUpdateInterval());
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
            }
        }

        for(SportEvent sportEvent: sportEvents) {
            Logger.info("Done streaming " + sportEvent.getTeams().get(0).getName() + " vs " + sportEvent.getTeams().get(1).getName());
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<SportEvent> getSportEvents() {
        return sportEvents;
    }

    public void setSportEvents(List<SportEvent> sportEvents) {
        this.sportEvents = sportEvents;
    }

    public DistributedTopic getTopic() {
        return topic;
    }

    public void setTopic(DistributedTopic topic) {
        this.topic = topic;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
