// Copyright 2021 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent.practice;

import com.internet.common.util.ThreadUtil;

import java.util.concurrent.ExecutionException;

/**
 * @author Kael.he (kael.he@alo7.com)
 */
public class CostComputing implements Computable<String, String>{
    @Override
    public String compute(String s) throws ExecutionException, InterruptedException {
        ThreadUtil.sleep(3);
        return "value";
    }
}
