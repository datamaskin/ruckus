package distributed.tasks;

import java.util.EventListener;

/**
 * Created by mgiles on 5/8/14.
 */
public interface IDistributedTaskListener extends EventListener {

    void onMessage(String message);

}
