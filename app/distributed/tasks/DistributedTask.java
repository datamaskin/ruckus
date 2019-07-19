package distributed.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by mgiles on 5/7/14.
 */

public abstract class DistributedTask implements Callable<String>, Serializable {

    private List<IDistributedTaskListener> listeners = new ArrayList<>();

    protected abstract String execute() throws Exception;

    public void addListener(IDistributedTaskListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public String call() throws Exception {
        String result = this.execute();
        for (IDistributedTaskListener listener : listeners) {
            listener.onMessage(result);
        }
        return result;
    }
}
