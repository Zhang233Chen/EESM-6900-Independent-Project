import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiscardOldestPolicyDemo {

    private static final int THREADS_SIZE = 1;
    private static final int CAPACITY = 1;

    public static void main(String[] args) throws Exception {

        // create the thread pool corePoolSize = maximumPoolSize = 1，blockingQueue capacity = 1.
        ThreadPoolExecutor pool = new ThreadPoolExecutor(THREADS_SIZE, THREADS_SIZE, 0,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(CAPACITY));
        // The reject strategy is DiscardPolicy
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        // new 10 tasks and add them into the thread pool.
        for (int i = 0; i < 10; i++) {
            Runnable myrun = new MyRunnable("task-"+i);
            pool.execute(myrun);
        }
        // close thread pool
        pool.shutdown();
    }
}
