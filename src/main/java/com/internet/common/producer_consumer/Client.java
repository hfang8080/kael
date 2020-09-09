// Copyright 2019 ALO7 Inc. All rights reserved.

package com.internet.common.producer_consumer;

import java.util.LinkedList;

/**
 * @author Kael He(kael.he@alo7.com)
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        LinkedList<String> queue = new LinkedList<>();
        Producer producer = new Producer(queue, 10);
        Consumer consumer = new Consumer(queue);
        consumer.start();
        producer.start();
    }
}
