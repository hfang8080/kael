// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static com.internet.common.util.ExecutorUtils.stop;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class ReentrantLockUsage {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    ReentrantLock lock = new ReentrantLock();
    int count = 0;

    enum Method {
        UNSAFE,
        SYNC,
        LOCK
    }

    void unsafe_increment() {
        count++;
    }

    synchronized void safe_increment_with_sync() {
        count++;
    }

    synchronized void safe_increment_with_lock() {
        lock.lock();
        count++;
        lock.unlock();
    }

    void test(Method method) {
        Runnable runnable;
        switch (method) {
            case LOCK:
                runnable = this::safe_increment_with_lock;
                break;
            case SYNC:
                runnable = this::safe_increment_with_sync;
                break;
            case UNSAFE:
            default:
                runnable = this::unsafe_increment;
        }
        IntStream.range(0, 1000)
                .forEach(i -> executor.execute(runnable));
        stop(executor);
        System.out.println(this.count);

    }

    public static void main(String[] args) {
        ReentrantLockUsage usage = new ReentrantLockUsage();
        usage.test(Method.UNSAFE);
//        usage.test(Method.SYNC);
//        usage.test(Method.LOCK);
    }

}
