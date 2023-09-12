package course.concurrency.queue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyBlockingQueueTest {

    ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Test
    void checkAdd() throws InterruptedException {
        final MyBlockingQueue<Object> queue = new MyBlockingQueue<>(5);

        Object value1 = new Object();
        queue.enqueue(value1);
        assertEquals(1, queue.size());
    }

    @Test
    void checkPop() throws InterruptedException {
        final MyBlockingQueue<Object> queue = new MyBlockingQueue<>(5);

        Object value1 = new Object();
        queue.enqueue(value1);

        Object actualValue = queue.dequeue();

        assertEquals(value1, actualValue);
        assertEquals(0, queue.size());
    }

    @Test
    void checkFIFO() throws InterruptedException {

        final MyBlockingQueue<Object> queue = new MyBlockingQueue<>(5);

        Object value1 = new Object();
        Object value2 = new Object();
        Object value3 = new Object();
        Object value4 = new Object();
        Object value5 = new Object();

        queue.enqueue(value1);
        queue.enqueue(value2);
        queue.enqueue(value3);
        queue.enqueue(value4);
        queue.enqueue(value5);


        assertEquals(value1, queue.dequeue());
        assertEquals(value2, queue.dequeue());
        assertEquals(value3, queue.dequeue());
        assertEquals(value4, queue.dequeue());
        assertEquals(value5, queue.dequeue());

        assertEquals(0, queue.size());

    }

    @RepeatedTest(10)
    void testMultithreading() throws InterruptedException {
        final MyBlockingQueue<Object> queue = new MyBlockingQueue<>(100);
        Set<Object> objects = IntStream.range(0, 99).mapToObj((a) -> new Object()).collect(Collectors.toSet());

        final ConcurrentLinkedQueue<Object> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();


        CountDownLatch latch = new CountDownLatch(1);
        for (Object object : objects) {
            executorService.execute(
                    () -> {
                        try {
                            latch.await();
                            concurrentLinkedQueue.add(queue.dequeue());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
            executorService.execute(
                    () -> {
                        try {
                            latch.await();
                            queue.enqueue(object);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });



        }
        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);


        assertEquals(objects.size(), concurrentLinkedQueue.size());


        for (int i = 0; i < objects.size(); i++) {
            Object actualValue = concurrentLinkedQueue.poll();
            assertTrue(objects.contains(actualValue));
        }
    }

}
