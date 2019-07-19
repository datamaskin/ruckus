package stats.retriever.mlb;

import common.GlobalConstants;
import stats.parser.mlb.ProbablePitcherParser;
import stats.provider.IStatProvider;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/**
 * Created by dmaclean on 7/15/14.
 */
public class ProbablePitcherRetriever implements IProbablePitcherRetriever {
    private IStatProvider statProvider;

    public ProbablePitcherRetriever(IStatProvider statProvider) {
        this.statProvider = statProvider;
    }

    @Override
    public List<Integer> getProbablePitchersForDate(Instant now) {
        try {
            Date nowAsDate = Date.from(now);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Map<String, String> params = new HashMap<>();
            params.put(GlobalConstants.STATS_INC_KEY_RESOURCE, "probables");
            params.put("date", simpleDateFormat.format(nowAsDate));

            String result = statProvider.getStats(params);
            List<Integer> pitcherIds = new ProbablePitcherParser().parse(result);
            return pitcherIds;
        } catch (Exception e) {
            play.Logger.info("Problem retrieving probable pitchers", e);
            return new ArrayList<>();
        }
    }

    public void setStatProvider(IStatProvider statProvider) {
        this.statProvider = statProvider;
    }
}
