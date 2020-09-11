// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.CountDownLatch;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class CountDownLatchUsage {
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;

    public CountDownLatchUsage(int countDown) {
        startSignal = new CountDownLatch(1);
        doneSignal = new CountDownLatch(countDown);
    }

    //
    public void parallelRun(int threadCount) throws InterruptedException {
        // 并行运行的任务
        for (int i = 0; i < threadCount; ++i) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    ThreadUtil.print("Work Done!!!");
                    doneSignal.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        ThreadUtil.sleep(1);
        ThreadUtil.print("Start to running.");
        startSignal.countDown(); // 开始运行
        doneSignal.await(); // 等待countDown个线程运行完成
    }

    public static void main(String[] args) throws InterruptedException {
        int countDown = 10;
        int threadCount = 11;
        // 此处如果threadCount < countDown, 那么程序将无法正常自主关闭
        // 因为doneSignal在等待他期望的数量结束
        // 但是theadCount >= countDown, 都能正常结束
        CountDownLatchUsage countDownLatchUsage = new CountDownLatchUsage(countDown);
        countDownLatchUsage.parallelRun(threadCount);
    }
}
