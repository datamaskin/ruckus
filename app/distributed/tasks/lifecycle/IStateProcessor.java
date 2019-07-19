package distributed.tasks.lifecycle;

import models.contest.Contest;

/**
 * Created by mwalsh on 7/25/14.
 */
public interface IStateProcessor {
    void process(Contest contest) throws Exception;
}
