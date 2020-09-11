// Copyright 2019 ALO7 Inc. All rights reserved.

package com.internet.common.producer_consumer;

import com.internet.common.util.ThreadUtil;

import java.util.LinkedList;

/**
 * @author Kael He(kael.he@alo7.com)
 */
public class Producer extends Thread {

    private final LinkedList<String> queue;
    private final int capital;
    public Producer(LinkedList<String> queue, int capital) {
        this.queue = queue;
        this.capital = capital;
        this.setName("Producer");
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            synchronized (queue) {
                String value = "" + i++;
                queue.addFirst(value);
                ThreadUtil.print("Producing message to queue. value:" + value);
                queue.notify();

                if (capital <= queue.size()) {
                    try {
                        ThreadUtil.print("Queue if full, waiting for consuming.");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
