// Copyright 2019 ALO7 Inc. All rights reserved.

package com.internet.common.util;

/**
 * @author Kael He(kael.he@alo7.com)
 */
public class ThreadPrint {

    public static void print(String message) {
        System.out.println(Thread.currentThread().getName() + ": " + message);
    }
}
