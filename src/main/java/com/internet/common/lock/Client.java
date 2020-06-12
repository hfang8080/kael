package com.internet.common.lock;

import java.util.concurrent.CountDownLatch;

public class Client {
    private static final String KEY = "lock:redis";
    private static final String MYSQL_KEY = "lock:mysql";
    static int count = 10;
    static CountDownLatch countDownLatch = new CountDownLatch(10);

    public void secKill() {
        DistributedLockFactory factory = new DistributedLockFactoryImpl();
        DistributedLock lock = factory.createLock(LockDialect.MYSQL);
        Runnable runnable = () -> {
            while (countDownLatch.getCount() > 0) {
            }
            String identifier = lock.acquireLockWithBlock(MYSQL_KEY, 1000, 1000);
            if (identifier != null) {
                System.out.println(String.format("%s: Got!!! count: %d",
                        Thread.currentThread().getName(), --count));
                lock.releaseLock(MYSQL_KEY, identifier);
            }

        };

        for (int i = 0; i < 10; i++) {
            countDownLatch.countDown();
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }


    public static void main(String[] args) {
        new Client().secKill();
    }
}
