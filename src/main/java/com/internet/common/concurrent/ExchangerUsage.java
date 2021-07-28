// Copyright 2021 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.Exchanger;

/**
 * @author Kael.he (kael.he@alo7.com)
 */
public class ExchangerUsage {
    Exchanger<String> exchanger = new Exchanger<>();
    Runnable playerA = () -> {
        for (int i = 0; i < 3; i++) {
            ThreadUtil.print("Player A serve a ball.");
            try {
                String response = exchanger.exchange("Ball back from A.");
                ThreadUtil.print("Player A get:" + response);
                ThreadUtil.sleep(1);
            } catch (InterruptedException ignorance) {
            }
        }
    };

    Runnable playerB = () -> {
        for (int i =0; i < 3; i++) {
            ThreadUtil.print("Player B serve a ball.");
            try {
                String response = exchanger.exchange("Ball back from B.");
                ThreadUtil.print("Player B get:" + response);
                ThreadUtil.sleep(1);
            } catch (InterruptedException ignorance) {
            }
        }
    };

    public static void main(String[] args) {
        ExchangerUsage usage = new ExchangerUsage();
        new Thread(usage.playerA).start();
        new Thread(usage.playerB).start();

        /*
        当两个线程都exchange了之后，才能互相交换他们当前所持有的对象。
        output:
        2021-07-28 22:20:50.718 +0800 Thread-1: Player B serve a ball.
        2021-07-28 22:20:50.718 +0800 Thread-0: Player A serve a ball.
        2021-07-28 22:20:50.719 +0800 Thread-0: Player A get:Ball back from B.
        2021-07-28 22:20:50.719 +0800 Thread-1: Player B get:Ball back from A.
         */
    }

}
