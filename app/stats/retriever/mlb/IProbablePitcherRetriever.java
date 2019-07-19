package stats.retriever.mlb;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by dmaclean on 7/15/14.
 */
public interface
        IProbablePitcherRetriever {
    List<Integer> getProbablePitchersForDate(Instant now);
}
