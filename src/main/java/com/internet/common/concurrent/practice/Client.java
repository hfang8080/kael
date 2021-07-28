// Copyright 2021 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent.practice;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author Kael.he (kael.he@alo7.com)
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(20);
        CostComputing costComputing = new CostComputing();
        MemoryCacheComputingProxy<String, String> cacheComputer = new MemoryCacheComputingProxy<>(costComputing);
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(() -> {
                try {
                    ThreadUtil.print("Waiting to start.");
                    startGate.await();
                    cacheComputer.compute("");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endGate.countDown();
                }
            });
            thread.start();
            ThreadUtil.sleep(1);
        }
        startGate.countDown();
        endGate.await();
        ThreadUtil.print("Work done.");
    }
    /*
    output:
    2021-07-28 23:43:31.471 +0800 Thread-0: Waiting to start.
    2021-07-28 23:43:32.476 +0800 Thread-1: Waiting to start.
    2021-07-28 23:43:33.481 +0800 Thread-2: Waiting to start.
    2021-07-28 23:43:34.485 +0800 Thread-3: Waiting to start.
    2021-07-28 23:43:35.490 +0800 Thread-4: Waiting to start.
    2021-07-28 23:43:36.493 +0800 Thread-5: Waiting to start.
    2021-07-28 23:43:37.497 +0800 Thread-6: Waiting to start.
    2021-07-28 23:43:38.498 +0800 Thread-7: Waiting to start.
    2021-07-28 23:43:39.499 +0800 Thread-8: Waiting to start.
    2021-07-28 23:43:40.501 +0800 Thread-9: Waiting to start.
    2021-07-28 23:43:41.503 +0800 Thread-10: Waiting to start.
    2021-07-28 23:43:42.505 +0800 Thread-11: Waiting to start.
    2021-07-28 23:43:43.506 +0800 Thread-12: Waiting to start.
    2021-07-28 23:43:44.511 +0800 Thread-13: Waiting to start.
    2021-07-28 23:43:45.512 +0800 Thread-14: Waiting to start.
    2021-07-28 23:43:46.512 +0800 Thread-15: Waiting to start.
    2021-07-28 23:43:47.517 +0800 Thread-16: Waiting to start.
    2021-07-28 23:43:48.522 +0800 Thread-17: Waiting to start.
    2021-07-28 23:43:49.524 +0800 Thread-18: Waiting to start.
    2021-07-28 23:43:50.528 +0800 Thread-19: Waiting to start.
    2021-07-28 23:43:51.531 +0800 Thread-0: Do compute.
    2021-07-28 23:43:51.531 +0800 Thread-12: Hit cache.
    2021-07-28 23:43:51.531 +0800 Thread-16: Hit cache.
    2021-07-28 23:43:51.531 +0800 Thread-14: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-13: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-11: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-17: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-10: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-9: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-15: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-19: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-8: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-4: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-18: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-6: Hit cache.
    2021-07-28 23:43:51.533 +0800 Thread-5: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-1: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-7: Hit cache.
    2021-07-28 23:43:51.532 +0800 Thread-2: Hit cache.
    // === 注意一下两行 ===
    2021-07-28 23:43:51.533 +0800 Thread-3: Hit cache.
    2021-07-28 23:43:54.534 +0800 Thread-7: value
    // 所有线程都在等待future的执行结果
    // run 耗费了3s的时间，并且只有一个线程实际跑了运算，其他的线程都block等待了3s，make sense!!!
    // ==================
    2021-07-28 23:43:54.535 +0800 Thread-5: value
    2021-07-28 23:43:54.535 +0800 Thread-18: value
    2021-07-28 23:43:54.535 +0800 Thread-8: value
    2021-07-28 23:43:54.535 +0800 Thread-9: value
    2021-07-28 23:43:54.535 +0800 Thread-10: value
    2021-07-28 23:43:54.535 +0800 Thread-13: value
    2021-07-28 23:43:54.536 +0800 Thread-12: value
    2021-07-28 23:43:54.535 +0800 Thread-1: value
    2021-07-28 23:43:54.535 +0800 Thread-3: value
    2021-07-28 23:43:54.534 +0800 Thread-0: value
    2021-07-28 23:43:54.534 +0800 Thread-2: value
    2021-07-28 23:43:54.536 +0800 Thread-16: value
    2021-07-28 23:43:54.536 +0800 Thread-14: value
    2021-07-28 23:43:54.535 +0800 Thread-11: value
    2021-07-28 23:43:54.535 +0800 Thread-17: value
    2021-07-28 23:43:54.535 +0800 Thread-15: value
    2021-07-28 23:43:54.535 +0800 Thread-19: value
    2021-07-28 23:43:54.535 +0800 Thread-4: value
    2021-07-28 23:43:54.535 +0800 Thread-6: value
    2021-07-28 23:43:54.538 +0800 main: Work done.
    Process finished with exit code 0

     */
}
