// Copyright 2019 ALO7 Inc. All rights reserved.

package com.internet.common.producer_consumer;

import com.internet.common.util.ThreadPrint;

import java.util.LinkedList;

/**
 * @author Kael He(kael.he@alo7.com)
 */
public class Consumer extends Thread {
    private final LinkedList<String> queue;
    public Consumer(LinkedList<String> queue) {
        this.queue = queue;
        this.setName("Consumer");
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                if (queue.size() == 0) {
                    try {
                        ThreadPrint.print("Queue is empty, waiting for producing.");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ThreadPrint.print("Consuming message, value:" + queue.removeLast());
                queue.notify();
            }
        }
    }
}
