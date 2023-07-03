package course.concurrency.m2_async.cf;

import course.concurrency.m2_async.cf.report.ReportServiceCF;
import course.concurrency.m2_async.cf.report.ReportServiceExecutors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class ReportServiceTests {

//    private ReportServiceExecutors reportService = new ReportServiceExecutors();
    private ReportServiceCF reportService = new ReportServiceCF();

    @Test
    public void testMultipleTasks() throws InterruptedException {
        int poolSize = Runtime.getRuntime().availableProcessors()*3;
        int iterations = 5;

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        System.out.println(poolSize);

        for (int i = 0; i < poolSize; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException ignored) {
                }

                for (int it = 0; it < iterations; it++) {
                    try {
                        reportService.getReport();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        long start = System.currentTimeMillis();
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        System.out.println("Test executor " + executor);
        System.out.println("Report executor " + reportService.executor);
        long end = System.currentTimeMillis();

        System.out.println("Execution time: " + (end - start));
    }
}
