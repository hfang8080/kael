package com.internet.common.lock;

/**
 * @author Kael He
 */
public class DistributedLockFactoryImpl implements DistributedLockFactory {

    @Override
    public DistributedLock createLock(LockDialect dialect) {
        switch (dialect) {
            case MYSQL:
                return new MysqlDistributedLock();
            case REDIS:
                return new RedisDistributedLock();
            case ZOOKEEPER:
                return null;
            default:
                return null;
        }

    }

}
