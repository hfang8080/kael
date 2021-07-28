// Copyright 2021 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent.practice;

import java.util.concurrent.ExecutionException;

/**
 * @author Kael.he (kael.he@alo7.com)
 */
public interface Computable<T, V> {
    V compute(T t) throws ExecutionException, InterruptedException;
}
