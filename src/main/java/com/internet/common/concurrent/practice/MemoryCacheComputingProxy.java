// Copyright 2021 ALO7 Inc. All rights reserved.

package com.internet.common.concurrent.practice;

import com.internet.common.util.ThreadUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 实现一个高效缓存机制。
 * @author Kael.he (kael.he@alo7.com)
 */
public class MemoryCacheComputingProxy<T, V> implements Computable<T, V> {
    Map<T, FutureTask<V>> cache = new ConcurrentHashMap<>();
    Computable<T, V> computable;

    public MemoryCacheComputingProxy(Computable<T, V> computable) {
        this.computable = computable;
    }

    @Override
    public V compute(T t) throws ExecutionException, InterruptedException {
        FutureTask<V> ft = new FutureTask<>(() -> {
            ThreadUtil.print("Do compute.");
            return computable.compute(t);
        });
        FutureTask<V> futureTask = cache.putIfAbsent(t, ft);
        if (futureTask == null) {
            futureTask = ft;
            futureTask.run();
        } else {
            ThreadUtil.print("Hit cache.");
        }
        V value = futureTask.get();
        ThreadUtil.print(value.toString());
        return value;
    }
}
