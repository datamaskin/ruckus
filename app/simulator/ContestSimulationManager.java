package simulator;

import com.avaje.ebean.Ebean;
import common.GlobalConstants;
import dao.DaoFactory;
import distributed.DistributedServices;
import distributed.DistributedSimulatorSocket;
import distributed.DistributedTopic;
import distributed.tasks.ContestCreatorTask;
import distributed.topics.mlb.MLBDistributedTopic;
import models.contest.Contest;
import models.contest.ContestPayout;
import models.contest.ContestState;
import models.contest.ContestType;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import play.Logger;
import play.Play;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.IUpdateProcessor;
import stats.updateprocessor.mlb.UpdateProcessor;
import utils.ContestIdGeneratorImpl;
import utils.IContestIdGenerator;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dmaclean on 7/2/14.
 */
public class ContestSimulationManager {

    private static List<SimulationRunner> activeRunners = new ArrayList<>();
    private ApplicationContext context;

    public ContestSimulationManager() {
        context = new ClassPathXmlApplicationContext("spring.xml");
    }

    public static boolean isSimulation() {
        String simulatorFlag = System.getProperty(GlobalConstants.SIMULATOR_JVM_ENV_FLAG);
        return simulatorFlag != null && simulatorFlag.equals("true");
    }

    /**
     * Get the wait time attribute specified in the conf file.  This attribute is used to determine
     * how long from now the contest should start, and how long from now to begin streaming events.
     *
     * @return
     */
    public static int getWaitTime() {
        return Play.application().configuration().getInt("contestsimulator.waittime");
    }

    /**
     * Get the socket update interval attribute from the conf file.  This attribute is used to
     * determine how long to wait between socket messages.
     *
     * @return
     */
    public static int getSocketUpdateInterval() {
        return Play.application().configuration().getInt("contestsimulator.socketupdateinterval");
    }

//    public static void stopSimulatorThread(int sportEventId) {
//        for (SimulationRunner runner : activeRunners) {
//            for(SportEvent sportEvent: sportEvents) {
//                if (runner.getSportEvent().getStatProviderId() == sportEventId) {
//                    runner.setRunning(false);
//                }
//            }
//        }
//    }

    public void startSimulatorThread(List<SportEvent> sportEvents) {
        /*
         * Reset AthleteSportEventInfo data.
         */
        for(SportEvent sportEvent: sportEvents) {
            List<AthleteSportEventInfo> athleteSportEventInfoList = DaoFactory.getSportsDao().findAthleteSportEventInfos(sportEvent);
            for (AthleteSportEventInfo athleteSportEventInfo : athleteSportEventInfoList) {
                athleteSportEventInfo.setFantasyPoints(new BigDecimal(0));
                athleteSportEventInfo.setTimeline("[]");
                athleteSportEventInfo.setStats(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(athleteSportEventInfo.getAthlete().getPositions().get(0)));
                DaoFactory.getSportsDao().saveAthleteSportEventInfo(athleteSportEventInfo);
            }
        }

        SimulationRunner runner = new SimulationRunner(sportEvents);
        activeRunners.add(runner);
        Thread t = new Thread(runner);
        t.start();
    }

    public String setUpSocket(int sportEventId, int port) {
        try {
            String url = Play.application().configuration().getString(GlobalConstants.CONFIG_SIMULATOR_BASE_URL);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(String.format("%s%s/%s", url, sportEventId, port));
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
                System.out.println(response1.getStatusLine());
                HttpEntity entity1 = response1.getEntity();
                String result = new java.util.Scanner(entity1.getContent()).useDelimiter("\\A").next();

                EntityUtils.consume(entity1);
                return result;
            } finally {
                response1.close();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public void configureDistributedSocket(int sportEventId, int port) {
        DistributedServices distributedServices = DistributedServices.get();
        SportEvent sportEvent = DaoFactory.getSportsDao().findSportEvent(sportEventId);

        if (sportEvent.getLeague().getAbbreviation().equals(League.MLB.getAbbreviation())) {
            IFantasyPointTranslator mlbTranslator = context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);

            IUpdateProcessor mlbStatsUpdateProcessor = context.getBean("MLBStatsUpdateProcessor", UpdateProcessor.class);

            // Add the mlb socket
            distributedServices.getSockets().put(GlobalConstants.SPORT_MLB, new DistributedSimulatorSocket(sportEventId));

            // Create the default MLB real-time topic
            DistributedTopic mlbTopic = new MLBDistributedTopic(GlobalConstants.TOPIC_REALTIME_PREFIX + GlobalConstants.SPORT_MLB, mlbTranslator,
                    mlbStatsUpdateProcessor, DaoFactory.getSportsDao(), DaoFactory.getContestDao(), GlobalConstants.STATS_INC_MLB_SOCKET_ROOT_NODE_NAME);

            // Add topic to the MLB socket for listening for events
            distributedServices.getSockets().get(GlobalConstants.SPORT_MLB).setTopic(mlbTopic);

        } else if (sportEvent.getLeague().getAbbreviation().equals(League.NFL.getAbbreviation())) {
            // Add the default nfl socket
            distributedServices.getSockets().put(GlobalConstants.SPORT_NFL, new DistributedSimulatorSocket(sportEventId));
        }
    }

    /**
     * Creates a test contest using sport events running on the same day as the provided sportEventId.
     *
     * @param sportEvents     The id of the SportEventGrouping that we want to use in the contest.
     * @return A status message describing whether the operation was successful.
     */
    public String createContest(League league, List<SportEvent> sportEvents, SportEventGrouping sportEventGrouping) {
        // Create start time for contest (now + 10 minutes)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, getWaitTime());
        Date contestStartTime = calendar.getTime();

        List<ContestPayout> payouts = new ArrayList<>();

//        List<SportEventDateRangeSelector> criteria = new ArrayList<>();
//        SportEventGroupingType type = null;
//        if (league.equals(League.NFL)) {
//            type = new SportEventGroupingType(League.NFL, "NFL ALL", criteria);
//        } else if (league.equals(League.MLB)) {
//            type = new SportEventGroupingType(League.MLB, "MLB ALL", criteria);
//        }
//        SportEventGrouping sportEventGrouping = new SportEventGrouping(sportEventList, type);
//        Ebean.save(Arrays.asList(type, sportEventGrouping));

        String urlId = "SIMULATOR_TEST_" + new ContestIdGeneratorImpl().generateString(8, IContestIdGenerator.alphaLower + IContestIdGenerator.alphaUpper + IContestIdGenerator.numeric, new SecureRandom());
        payouts.add(new ContestPayout(1, 1, 194));

        Contest contest = new Contest(ContestType.H2H, urlId, league, 2, true, 100, 1, GlobalConstants.DEFAULT_SALARY_CAP, sportEventGrouping, payouts, null);
        contest.setStartTime(contestStartTime);
        contest.setContestState(ContestState.open);
        Ebean.save(contest);

        ContestCreatorTask contestCreatorTask = new ContestCreatorTask();
        if (sportEventGrouping.getSportEventGroupingType().getLeague().equals(League.NFL)) {
            if (!contestCreatorTask.createNflSalaries(sportEventGrouping)) {
                Logger.error("Cannot create salaries for these contests. No prediction data");
                contestCreatorTask.createRandomSalaries(sportEventGrouping);
            }
        } else {
            contestCreatorTask.createRandomSalaries(sportEventGrouping);
        }

        List<AthleteSportEventInfo> athleteSportEventInfoList = new ArrayList<>();
        for(SportEvent sportEvent: sportEvents) {
            athleteSportEventInfoList.addAll(DaoFactory.getSportsDao().findAthleteSportEventInfos(sportEvent));
        }
        for(AthleteSportEventInfo athleteSportEventInfo: athleteSportEventInfoList) {
            athleteSportEventInfo.setFantasyPoints(new BigDecimal("0"));
            athleteSportEventInfo.setIndicator(0);
            athleteSportEventInfo.setStats(DaoFactory.getSportsDao().createInitialJsonForAthleteBoxscore(athleteSportEventInfo.getAthlete().getPositions().get(0)));
            athleteSportEventInfo.setTimeline("[]");
            DaoFactory.getSportsDao().saveAthleteSportEventInfo(athleteSportEventInfo);
        }

        // Reset the SportEvent unitsRemaining.
        for (SportEvent se : sportEvents) {
            if (se.getLeague().equals(League.MLB)) {
                se.setUnitsRemaining(9);
            } else if (se.getLeague().equals(League.NFL)) {
                se.setUnitsRemaining(60);
            }
            se.setComplete(false);
                DaoFactory.getSportsDao().saveSportEvent(se);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        return "Successfully created contest " + contest.getUrlId() + ", starting at " + simpleDateFormat.format(contest.getStartTime());
    }
}
