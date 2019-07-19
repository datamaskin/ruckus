package distributed.tasks.mlb;

import distributed.DistributedServices;
import common.GlobalConstants;
import distributed.tasks.DistributedTask;
import stats.retriever.mlb.IProbablePitcherRetriever;
import stats.retriever.mlb.ProbablePitcherRetriever;
import utils.ITimeService;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaclean on 7/15/14.
 */
public class MLBProbablePitcherUpdaterTask extends DistributedTask {
    private IProbablePitcherRetriever probablePitcherRetriever;
    private ITimeService timeService;

    public MLBProbablePitcherUpdaterTask(IProbablePitcherRetriever probablePitcherRetriever, ITimeService timeService) {
        this.probablePitcherRetriever = probablePitcherRetriever;
        this.timeService = timeService;
    }

    @Override
    protected String execute() throws Exception {
        Instant today = timeService.getNowEST();
        Instant tomorrow = today.plus(1, ChronoUnit.DAYS);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Integer> todayProbables = probablePitcherRetriever.getProbablePitchersForDate(today);
        List<Integer> tomorrowProbables = probablePitcherRetriever.getProbablePitchersForDate(tomorrow);

        Map<String, List<Integer>> cache = DistributedServices.getInstance().getMap(GlobalConstants.PROBABLE_PITCHERS_MAP);
        cache.put(simpleDateFormat.format(Date.from(today)), todayProbables);
        cache.put(simpleDateFormat.format(Date.from(tomorrow)), tomorrowProbables);

        return null;
    }

    public void setProbablePitcherRetriever(ProbablePitcherRetriever probablePitcherRetriever) {
        this.probablePitcherRetriever = probablePitcherRetriever;
    }
}
