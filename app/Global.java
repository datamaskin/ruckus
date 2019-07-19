import auth.AppEnvironment;
import auth.Environment;
import auth.LocalEnvironment;
import distributed.tasks.nfl.StatsNflBoxscoreReconciliationTask;
import distributed.tasks.nfl.StatsNflUpdaterTask;
import service.IContestListService;
import common.GlobalConstants;
import controllers.UtilityController;
import dao.DaoFactory;
import distributed.DistributedRunnable;
import distributed.DistributedServices;
import distributed.DistributedTopic;
import distributed.tasks.*;
import distributed.tasks.lifecycle.ContestLifecycleTask;
import distributed.tasks.lifecycle.IRandomizer;
import distributed.topics.BaseStatsUpdateDistributedTopic;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.play.AtmosphereCoordinator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.api.mvc.Handler;
import play.libs.F;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import securesocial.core.RuntimeEnvironment;
import simulator.ContestSimulationManager;
import sockets.ChatSocket;
import sockets.ContestListSocket;
import sockets.ContestLiveOverviewSocket;
import sockets.SocketRouter;
import stats.parser.nfl.BoxscoreParser;
import stats.provider.nfl.StatsIncProviderNFL;
import stats.retriever.*;
import stats.retriever.nfl.NflBoxscoreRetriever;
import stats.translator.IFantasyPointTranslator;
import stats.updateprocessor.IUpdateProcessor;
import stats.updateprocessor.nfl.UpdateProcessor;
import utils.ITimeService;
import utils.TimeService;
import views.html.errorpage;
import views.html.notfound;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static play.libs.F.Promise.promise;
import static play.mvc.Results.notFound;


/**
 * Created by mgiles on 4/15/14.
 */
public class Global extends GlobalSettings {

    private DistributedServices distributedServices;

    @Override
    public void onStart(Application application) {
        if (Play.isTest()) {
            return;
        }

        if (Play.isDev()) {
            AppEnvironment.setEnvironment(new LocalEnvironment());
        } else {
            AppEnvironment.setEnvironment(new Environment());
        }

        AtmosphereCoordinator.instance().framework().addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT, "true");

        // Set cluster port to localhost if it's not passed in
        String host_ip = System.getProperty(GlobalConstants.CONFIG_PUBLIC_HOST_IP);
        if (host_ip == null) {
            host_ip = System.getenv(GlobalConstants.ENV_HOST_IP) == null ? "127.0.0.1" : System.getenv(GlobalConstants.ENV_HOST_IP);
            System.setProperty(GlobalConstants.CONFIG_PUBLIC_HOST_IP, host_ip);
        }
        String awsKey = System.getenv(GlobalConstants.AWS_ACCESS_KEY_ID);
        String awsSecret = System.getenv(GlobalConstants.AWS_SECRET_KEY);
        if (awsKey != null && awsSecret != null) {
            System.setProperty(GlobalConstants.AWS_ACCESS_KEY_ID, awsKey);
            System.setProperty(GlobalConstants.AWS_SECRET_KEY, awsSecret);
        }
        String environmentName = System.getenv(GlobalConstants.AWS_ENVIRONMENT_NAME);
        if (environmentName != null) {
            System.setProperty(GlobalConstants.AWS_ENVIRONMENT_NAME, environmentName);
        }


        distributedServices = DistributedServices.get();
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        DistributedServices.setContext(context);
        new DaoFactory(context);

        //IStatsFantasyPointTranslator mlbTranslator = context.getBean("MLBFantasyPointTranslator", IStatsFantasyPointTranslator.class);
        IFantasyPointTranslator nflTranslator = context.getBean("NFLFantasyPointTranslator", IFantasyPointTranslator.class);

        //IStatsUpdateProcessor mlbStatsUpdateProcessor = context.getBean("MLBStatsUpdateProcessor", StatsMLBUpdateProcessor.class);
        IUpdateProcessor nflStatsUpdateProcessor = context.getBean("NFLStatsUpdateProcessor", UpdateProcessor.class);

        /*
         * Create topics for specific leagues and for fantasy points.
         */
        // Create the default NFL real-time topic
        DistributedTopic nflTopic = new BaseStatsUpdateDistributedTopic(GlobalConstants.TOPIC_REALTIME_PREFIX + GlobalConstants.SPORT_NFL,
                nflTranslator, nflStatsUpdateProcessor, DaoFactory.getSportsDao(), DaoFactory.getContestDao(), GlobalConstants.STATS_INC_NFL_SOCKET_ROOT_NODE_NAME);

        // Create the default MLB real-time topic
        //DistributedTopic mlbTopic = new BaseStatsUpdateDistributedTopic(GlobalConstants.TOPIC_REALTIME_PREFIX + GlobalConstants.SPORT_MLB,
        //        mlbTranslator, mlbStatsUpdateProcessor, DaoFactory.getSportsDao(), DaoFactory.getContestDao(), GlobalConstants.STATS_INC_MLB_SOCKET_ROOT_NODE_NAME);

        if (!ContestSimulationManager.isSimulation()) {
            // Add topic to the NFL socket for listening for events
            distributedServices.getSockets().get(GlobalConstants.SPORT_NFL).setTopic(nflTopic);

            // Add topic to the MLB socket for listening for events
            //distributedServices.getSockets().get(GlobalConstants.SPORT_MLB).setTopic(mlbTopic);
        }

        AtmosphereCoordinator.instance().discover(ContestListSocket.class).ready();
        AtmosphereCoordinator.instance().discover(ChatSocket.class).ready();
        AtmosphereCoordinator.instance().discover(ContestLiveOverviewSocket.class).ready();

        ITimeService timeService = new TimeService();

        ContestLifecycleTask contestLifecycleTask = new ContestLifecycleTask(
                DaoFactory.getContestDao(),
                context.getBean("timeService", ITimeService.class),
                context.getBean("ContestListManager", IContestListService.class),
                context.getBean("randomizer", IRandomizer.class)
        );

        AthleteUpdaterTask athleteUpdaterTask = new AthleteUpdaterTask(
                new AthleteInjuryRetriever(),
                new AthleteRetriever()
        );
        TeamUpdaterTask teamUpdaterTask = new TeamUpdaterTask(new TeamRetriever());
//        StatsMlbUpdaterTask statsMlbUpdaterTask = new StatsMlbUpdaterTask();
        StatsNflUpdaterTask statsNflUpdaterTask = new StatsNflUpdaterTask();

        SportEventCompletedUpdaterTask sportEventCompletedUpdaterTask = new SportEventCompletedUpdaterTask(timeService);
        //MLBProbablePitcherUpdaterTask probablePitcherUpdaterTask = new MLBProbablePitcherUpdaterTask(
        //        new StatsProbablePitcherRetriever(new StatsIncProviderMLB()), timeService);

        StatsSocketConnectionTask statsSocketConnectionTask = new StatsSocketConnectionTask(Arrays.asList(
                //distributedServices.getSockets().get(GlobalConstants.SPORT_MLB),
                distributedServices.getSockets().get(GlobalConstants.SPORT_NFL)
        ));

        /*
         * Configure the task responsible for box score reconciliation and migrating contests from a completed state into
         * a historical state so winners can get paid out.
         */
        BoxscoreParser boxscoreParser = new BoxscoreParser(nflTranslator);
        NflBoxscoreRetriever boxscoreRetriever = new NflBoxscoreRetriever(boxscoreParser, new StatsIncProviderNFL());
        StatsNflBoxscoreReconciliationTask statsNflBoxscoreReconciliationTask = new StatsNflBoxscoreReconciliationTask(timeService, boxscoreRetriever);

        promise(() -> {
            try {
                UtilityController.initData();
                teamUpdaterTask.call();
                athleteUpdaterTask.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
//        if (Play.isProd()) {
//            DistributedServices.getTaskScheduler().schedule(statsNflUpdaterTask, 1, TimeUnit.MINUTES);
//        }

        SportEventUpdaterTask sportEventUpdaterTask = new SportEventUpdaterTask(new SportEventRetriever());
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(
                new DistributedRunnable(sportEventUpdaterTask), 1, 24 * 60, TimeUnit.MINUTES);

        DistributedServices.getTaskScheduler().scheduleAtFixedRate(
                new DistributedRunnable(athleteUpdaterTask), 1, 15, TimeUnit.MINUTES);
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(
                new DistributedRunnable(contestLifecycleTask), 1, 1, TimeUnit.MINUTES);
//        DistributedServices.getTaskScheduler().scheduleAtFixedRate(
//                new DistributedRunnable(contestCreatorTask), 1, 60, TimeUnit.MINUTES);
//        DistributedServices.getTaskScheduler().scheduleAtFixedRate(
//                new DistributedRunnable(statsMlbUpdaterTask), timeService.getMinutesFromTargetTimeEST(2), 60 * 24, TimeUnit.MINUTES);
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(
                new DistributedRunnable(statsNflUpdaterTask), timeService.getMinutesFromTargetTimeEST(2), 60 * 24, TimeUnit.MINUTES);
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(new DistributedRunnable(sportEventCompletedUpdaterTask), 1, 10, TimeUnit.MINUTES);
        //DistributedServices.getTaskScheduler().scheduleAtFixedRate(new DistributedRunnable(probablePitcherUpdaterTask), 1, 60, TimeUnit.MINUTES);
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(new DistributedRunnable(statsSocketConnectionTask), 1, 1, TimeUnit.MINUTES);
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(new DistributedRunnable(statsNflBoxscoreReconciliationTask), 1, 60, TimeUnit.MINUTES);

        // fill and clear the caches
        DistributedServices.getTaskScheduler().scheduleAtFixedRate(new DistributedRunnable(new CacheRefreshTask()), 240, 240, TimeUnit.MINUTES);
        DistributedServices.getTaskScheduler().schedule(new CachePopulatorTask(), 1, TimeUnit.MINUTES);

        Logger.info("Ruckus server has started");
    }

    @Override
    public void onStop(Application application) {
        Logger.info("Ruckus server has stopped");
    }

    @Override
    public Handler onRouteRequest(RequestHeader request) {
        return SocketRouter.dispatch(request);
    }

    /**
     * Manages controllers instantiation.
     *
     * @param controllerClass the controller class to instantiate.
     * @return the appropriate instance for the given controller class.
     */
    @Override
    public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
        Logger.debug("creating controller: " + controllerClass.getName());
        try {
            return controllerClass.getDeclaredConstructor(RuntimeEnvironment.class).newInstance(AppEnvironment.getEnvironment());
        } catch (NoSuchMethodException e) {
            // the controller does not receive a RuntimeEnvironment, delegate creation to base class.
            return super.getControllerInstance(controllerClass);
        }
    }

    /**
     * Called when no action was found to serve a request.
     * <p>
     * The default behavior is to render the framework's default 404 page. This is achieved by returning <code>null</code>,
     * so that the Scala engine handles <code>onHandlerNotFound</code>.
     * <p>
     * By overriding this method one can provide an alternative 404 page.
     *
     * @param request the HTTP request
     * @return null in the default implementation, you can return your own custom Result in your Global class.
     */
    @Override
    public F.Promise<Result> onHandlerNotFound(RequestHeader request) {
        return F.Promise.pure(notFound(
                notfound.render()
        ));
    }

    /**
     * Called when an exception occurred.
     * <p>
     * The default is to send the framework's default error page. This is achieved by returning <code>null</code>,
     * so that the Scala engine handles the excepetion and shows an error page.
     * <p>
     * By overriding this method one can provide an alternative error page.
     *
     * @param request
     * @param t       is any throwable
     * @return null as the default implementation
     */
    @Override
    public F.Promise<Result> onError(RequestHeader request, Throwable t) {
        return F.Promise.pure(notFound(
                errorpage.render()
        ));
    }
}
