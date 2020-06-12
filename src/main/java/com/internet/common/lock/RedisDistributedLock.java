package com.internet.common.lock;

import com.internet.common.lock.services.RedisService;
import com.internet.common.util.Util;

import java.time.Instant;

/**
 * @author Kael He
 */
public class RedisDistributedLock implements DistributedLock {
    private final RedisService redisService;

    public RedisDistributedLock() {
        redisService = new RedisService();
    }

    @Override
    public String acquireLock(String key, int expireSeconds) {
        String identifier = Util.generateUuid();
        System.out.println(String.format("%s: Generate id: %s.",
                Thread.currentThread().getName(), identifier));
//        long setNxResult = redisService.setNx(key, identifier);
//        long expireResult = redisService.expire(key, expireSeconds);
        boolean success = redisService.setNxWithExpire(key, identifier, expireSeconds);
        if (success) {
            System.out.println(String.format("%s: Acquire lock %s success with %s.",
                    Thread.currentThread().getName(), key, identifier));
            return identifier;
        } else {
            System.out.println(String.format("%s: Acquire lock %s failed.",
                    Thread.currentThread().getName(), key));
            return null;
        }
    }

    @Override
    public boolean releaseLock(String key, String identifier) {
        String value = redisService.get(key);
        if (null != identifier && identifier.equals(value)) {
            long count = redisService.delete(key);
            if (count == 1) {
                System.out.println(String.format("%s: Release lock %s success with %s.",
                        Thread.currentThread().getName(), key, identifier));
                return true;
            }
        }
        System.out.println(String.format("%s: Release lock %s failed with %s.",
                Thread.currentThread().getName(), key, identifier));
        return false;
    }

    @Override
    public String acquireLockWithBlock(String key, int expireSeconds, int blockSeconds) {
        String identifier = Util.generateUuid();
        System.out.println(String.format("%s: Generate id: %s.",
                Thread.currentThread().getName(), identifier));

        Instant end = Instant.now().plusSeconds(blockSeconds);
        while (end.isAfter(Instant.now())) {
            long setNxResult = redisService.setNx(key, identifier);
            long expireResult = redisService.expire(key, expireSeconds);
            if (setNxResult == 1) {
                System.out.println(String.format("%s: Acquire lock %s success with %s.",
                        Thread.currentThread().getName(), key, identifier));
                return identifier;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(String.format("%s: Acquire lock %s failed.",
                Thread.currentThread().getName(), key));
        return null;
    }

    @Override
    public void close() {
        redisService.close();
    }

}
