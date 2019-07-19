package distributed.tasks.mlb;

import distributed.DistributedServices;
import distributed.tasks.DistributedTask;
import stats.manager.mlb.MlbEventManager;

/**
 * Created by mwalsh on 7/7/14.
 */
public class StatsMlbUpdaterTask extends DistributedTask {

    @Override
    protected String execute() throws Exception {
        MlbEventManager manager = (MlbEventManager) DistributedServices.getContext().getBean("StatsMlbEventManager");
        manager.process();

        return "ok";
    }
}
