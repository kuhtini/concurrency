package course.concurrency.queue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue<T> {


   private Object[] queue;

    private int setIndex;
    private int getIndex;

    private int totalCount;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition emptyCondition = lock.newCondition();
    private final Condition fullCondition = lock.newCondition();


    public MyBlockingQueue(int size) {
        ReentrantLock l = this.lock;
        l.lock();
        try {
            queue = new Object[size];
        } finally {
            l.unlock();
        }
    }

    public void enqueue(T value) throws InterruptedException {
        ReentrantLock reentrantLock = lock;
        reentrantLock.lock();
        System.out.println(" enqueue locked " + Thread.currentThread().getName() + reentrantLock.toString());
        try {
            while (queue.length == totalCount) {
                fullCondition.await();
            }
            _enqueue(value);
        } finally {
            reentrantLock.unlock();
            System.out.println(" enqueue unlocked " + Thread.currentThread().getName() + reentrantLock.toString());
            System.out.println();
        }
    }

    public T dequeue() throws InterruptedException {
        ReentrantLock l = lock;
        l.lock();
        System.out.println(" dequeue locked " + Thread.currentThread().getName() + l.toString());
        try {
            while (totalCount == 0) {
                emptyCondition.await();
            }

            return _dequeue();
        } finally {
            l.unlock();
            System.out.println(" dequeue unlocked " + Thread.currentThread().getName() + l.toString());
            System.out.println();
        }
    }

    public int size() {
        return totalCount;
    }

    private void _enqueue(T value) {
        queue[setIndex++] = value;

        System.out.println("add to position " + (setIndex - 1));

        if (setIndex == queue.length) {
            setIndex = 0;
        }
        totalCount++;
        emptyCondition.signal();
    }

    private T _dequeue() {

        T value = (T) queue[getIndex];
        queue[getIndex++]=null;

        System.out.println("removed from position " + (setIndex - 1) + " current thread " + Thread.currentThread().getName());

        if (getIndex == queue.length) {
            getIndex = 0;
        }
        totalCount--;
        fullCondition.signal();

        return value;
    }

}
