// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

import static com.internet.common.util.ExecutorUtils.stop;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class StampedLockUsage {
    // StampedLock也拥有ReadWriteLock的功能, 此外还提供了对于读锁的优化
    StampedLock lock = new StampedLock();
    ExecutorService executor = Executors.newFixedThreadPool(2);

    Runnable r1 = () -> {
        long stamp = lock.tryOptimisticRead();
        System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
        ThreadUtil.sleep(5);
        System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
        ThreadUtil.sleep(2);
        System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
        lock.unlock(stamp);
    };

    Runnable r2 = () -> {
        long stamp = lock.writeLock();
        System.out.println("Write Lock acquired");
        ThreadUtil.sleep(2);
        lock.unlock(stamp);
        System.out.println("Write done");
    };

    void optimistic() {
        executor.submit(r1);
        ThreadUtil.sleep(1);
        executor.submit(r2);
        stop(executor);
    }

    public static void main(String[] args) {
        StampedLockUsage usage = new StampedLockUsage();
        usage.optimistic();
    }

}
