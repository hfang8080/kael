package com.internet.common.lock;

/**
 * @author Kael He
 */
public interface DistributedLockFactory {
    DistributedLock createLock(LockDialect dialect);
}
