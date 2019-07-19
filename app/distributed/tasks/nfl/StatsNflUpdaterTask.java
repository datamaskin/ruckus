package distributed.tasks.nfl;

import dao.DaoFactory;
import distributed.DistributedServices;
import distributed.tasks.DistributedTask;
import stats.manager.nfl.AthleteManager;
import stats.manager.nfl.DepthChartManager;
import stats.manager.nfl.GameOddsManager;
import stats.predictive.nfl.DefenseProjector;
import stats.predictive.nfl.OffenseProjector;

/**
 * Created by mgiles on 7/22/14.
 */
public class StatsNflUpdaterTask extends DistributedTask {

    @Override
    protected String execute() throws Exception {
        DaoFactory.getStatsDao().updateNflInjuries();
        AthleteManager athleteManager = (AthleteManager) DistributedServices.getContext().getBean("StatsNflAthleteManager");
        athleteManager.process();

        GameOddsManager oddsManager = (GameOddsManager) DistributedServices.getContext().getBean("StatsNflGameOddsManager");
        oddsManager.process();

        DepthChartManager depthManager = (DepthChartManager) DistributedServices.getContext().getBean("StatsNflDepthChartManager");
        depthManager.process();

        new DefenseProjector().predict();
        new OffenseProjector().predict();

        return "ok";
    }
}