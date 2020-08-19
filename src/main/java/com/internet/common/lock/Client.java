package com.internet.common.lock;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final String KEY = "key";
    static int count = 100;
    static CountDownLatch countDownLatch = new CountDownLatch(100);
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            100, 100, 1000, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(100));

    public void secKill() {
        DistributedLockFactory factory = new DistributedLockFactoryImpl();
        DistributedLock lock = factory.createLock(LockDialect.ZOOKEEPER);
        Runnable runnable = () -> {
            while (countDownLatch.getCount() > 0) {
                System.out.println(String.format("%s: Waiting....",
                        Thread.currentThread().getName()));
            }
            String identifier = lock.acquireLockWithBlock(KEY, 1000, 10);
            if (identifier != null) {
                System.out.println(String.format("%s: Got!!! count: %d",
                        Thread.currentThread().getName(), --count));
                lock.releaseLock(KEY, identifier);
            }

        };


        for (int i = 0; i < 100; i++) {
            countDownLatch.countDown();
            executor.execute(runnable);
        }

        while (executor.getActiveCount() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        lock.close();
    }


    public static void main(String[] args) {
        new Client().secKill();
    }
}
