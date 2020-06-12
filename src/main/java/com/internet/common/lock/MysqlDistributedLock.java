package com.internet.common.lock;

import java.time.Instant;

/**
 * @author Kael He
 */
public class MysqlDistributedLock implements DistributedLock {
    private final MysqlService mysqlService;

    public MysqlDistributedLock() {
        mysqlService = new MysqlService();
    }

    @Override
    public String acquireLock(String key, int expireSeconds) {
        String identifier = Util.generateUuid();
        int count = mysqlService.update(buildAcquireLockSql(key, identifier, expireSeconds));
        if (count == 1) {
            System.out.println(String.format("%s: Acquire lock %s success with %s.",
                    Thread.currentThread().getName(), key, identifier));
            return identifier;
        }
        System.out.println(String.format("%s: Acquire lock %s failed.",
                Thread.currentThread().getName(), key));
        return null;
    }

    @Override
    public boolean releaseLock(String key, String identifier) {
        int count = mysqlService.update(buildReleaseLockSql(key, identifier));
        if (count == 1) {
            System.out.println(String.format("%s: Release lock %s success with %s.",
                    Thread.currentThread().getName(), key, identifier));
            return true;
        }
        System.out.println(String.format("%s: Release lock %s failed with %s.",
                Thread.currentThread().getName(), key, identifier));
        return false;
    }

    @Override
    public String acquireLockWithBlock(String key, int expireSeconds, int blockSeconds) {
        String identifier = Util.generateUuid();
        Instant blockEndTime = Instant.now().plusSeconds(blockSeconds);
        while (blockEndTime.isAfter(Instant.now())) {
            int count = mysqlService.update(buildAcquireLockSql(key, identifier, expireSeconds));
            if (count == 1) {
                System.out.println(String.format("%s: Acquire lock %s success with %s.",
                        Thread.currentThread().getName(), key, identifier));
                return identifier;
            }
            try {
                Thread.sleep(10);
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

    }

    private String buildAcquireLockSql(String key, String owner, int expire_time) {
        return String.format("UPDATE `distributed_lock` SET `owner` = '%s', `expire_time` = DATE_ADD(now(), INTERVAL %d second) " +
                        "WHERE `key` = '%s' AND (`owner` = '%s' OR `expire_time` <= now())",
                owner, expire_time, key, owner);
    }

    private String buildReleaseLockSql(String key, String owner) {
        return String.format("UPDATE `distributed_lock` SET `owner` = '', `expire_time` = DATE_SUB(now(), INTERVAL 1 second) " +
                        "WHERE `key` = '%s' and `owner` = '%s'",
                key, owner);
    }

}
