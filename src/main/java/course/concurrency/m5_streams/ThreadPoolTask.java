package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {

        // Invert FIFO behavior to LIFO
        LinkedBlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<>() {
            @Override
            public Runnable take() throws InterruptedException {
                return super.takeLast();
            }
        };

        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                workQueue);
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(8, 8,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    }
}
