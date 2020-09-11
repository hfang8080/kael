// Copyright 2020 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.CyclicBarrier;

/**
 * @author Kael He (kael.he@alo7.com)
 */
public class CyclicBarrierUsage {
    private final CyclicBarrier cyclicBarrier;

    public CyclicBarrierUsage(int n) {
        cyclicBarrier = new CyclicBarrier(n);
    }

    public void parallelRun() {
        for (int i = 0; i < 10; ++i) {
            doWork(i);
            ThreadUtil.sleep(1);
            cyclicBarrier.reset(); // 可以循环使用该线程屏障，这个也是CyclicBarrier的特色
        }

    }

    public void doWork(int epoch) {
        for (int i = 0; i < 10; ++i) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await(); // 需要等待n个线程一起运行 CountdownLatch, 也可以做到
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ThreadUtil.print("Epoch: " + epoch + ", Done!!!");
            }).start();
        }
    }

    public static void main(String[] args) {
        new CyclicBarrierUsage(10).parallelRun();

    }
}
