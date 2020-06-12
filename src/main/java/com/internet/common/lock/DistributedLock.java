package com.internet.common.lock;

/**
 * @author Kael He
 */
public interface DistributedLock {
    /**
     * Acquire lock with specify key and a expire seconds.
     *
     * @param key The lock key.
     * @param expireSeconds Expire seconds.
     * @return Return owner's identifier when acquire lock, else null.
     */
    String acquireLock(String key, int expireSeconds);

    /**
     * Release lock with specify key and owner's identifier.
     *
     * @param key The lock key.
     * @param identifier The lock owner's identifier.
     * @return Return true when release success, else false.
     */
    boolean releaseLock(String key, String identifier);

    /**
     * That is same with acquire lock, the only difference is waiting for some seconds in block.
     *
     * @param key The lock key.
     * @param expireSeconds Expire seconds.
     * @return Return owner's identifier when acquire lock, else null.
     */
    String acquireLockWithBlock(String key, int expireSeconds, int blockSeconds);

    void close();
}
