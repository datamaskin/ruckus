package distributed;

import distributed.tasks.DistributedTask;
import play.Logger;

/**
 * Created by mgiles on 5/8/14.
 */
public class DistributedRunnable implements Runnable {

    private DistributedTask task;

    public DistributedRunnable(DistributedTask task) {
        this.task = task;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            if (DistributedServices.isMaster()) {
                Logger.info("Running task: " + task.toString());
                DistributedServices.getTaskScheduler().submit(task);
            } else {
                Logger.info("Pretending to run task: " + task.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
