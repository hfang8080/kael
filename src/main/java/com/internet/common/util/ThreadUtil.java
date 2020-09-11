// Copyright 2019 ALO7 Inc. All rights reserved.

package com.internet.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kael He(kael.he@alo7.com)
 */
public class ThreadUtil {
    static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");

    public static void print(String message) {
        System.out.println(FORMATTER.format(new Date()) + " " + Thread.currentThread().getName() + ": " + message);
    }

    public static void sleep(int sleepSeconds) {
        try {
            Thread.sleep(sleepSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
