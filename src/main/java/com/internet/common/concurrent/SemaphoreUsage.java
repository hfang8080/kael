// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.Semaphore;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class SemaphoreUsage {
    private final Semaphore semaphore;
    public SemaphoreUsage(int max) {
        semaphore = new Semaphore(max);
    }

    // 通过Semaphore（信号量） 控制并行运行某个任务并行运行的最大线程数
    public void parallelRun(int threadCount) {
        long sleepMilliseconds = 1000 * 5;
        for (int i = 0; i < threadCount; ++i) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        ThreadUtil.print("Done !!!");
                        Thread.sleep(sleepMilliseconds);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }
                }
            }.start();
        }
    }

    public static void main(String[] args) {
        SemaphoreUsage semaphoreUsage = new SemaphoreUsage(10);
        semaphoreUsage.parallelRun(100);
    }
}
