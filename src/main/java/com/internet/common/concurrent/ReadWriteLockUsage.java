// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

import static com.internet.common.util.ExecutorUtils.stop;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class ReadWriteLockUsage {

    ReadWriteLock lock = new ReentrantReadWriteLock();
    ExecutorService executor = Executors.newFixedThreadPool(10);

    void read() {
        Lock readLock = this.lock.readLock();
        // 读锁不会互相阻塞，当写锁释放之后，需要获取读锁的线程都可以并发执行。
        readLock.lock();
        ThreadUtil.print("Reading Something...");
        readLock.unlock();
    }

    void write() {
        Lock writeLock = this.lock.writeLock();
        // 写锁会阻塞，即阻塞其他线程想要拥有的写锁和读锁
        writeLock.lock();
        ThreadUtil.print("writing Something...");
        ThreadUtil.sleep(3);
        writeLock.unlock();
        ThreadUtil.print("writing Done");
    }

    void test() {
        IntStream.range(0, 3)
                .forEach(i -> executor.submit(this::write));
        IntStream.range(0, 5)
                .forEach(i -> executor.submit(this::read));
        stop(executor);
    }

    public static void main(String[] args) {
        ReadWriteLockUsage usage = new ReadWriteLockUsage();
        usage.test();
    }
}
