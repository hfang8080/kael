package com.internet.common.lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author Kael He
 */
public class ZKDistributeLockSample implements Lock, Watcher {

    private static final String PATH_SPLIT = "/";
    public static final String LOCK_SPLIT = "__lock__";

    private ZooKeeper zk = null;
    private String LOCK_ROOT = "/distribute_locks";
    private String connectString = "127.0.0.1:2181";
    private String lockName;
    private String waitLock;
    private String currentLock;
    private CountDownLatch countDownLatch;
    private int sessionTimeout = 30000;
    private List<Exception> exceptionList = new ArrayList<Exception>();


    public ZKDistributeLockSample(String lockName) {
        this.lockName = lockName;
        try {
            this.zk = new ZooKeeper(connectString, sessionTimeout, this);
            Stat stat = zk.exists(LOCK_ROOT, false);
            if (stat == null) {
                zk.create(LOCK_ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lock() {
        if (exceptionList.size() > 0) {
            throw new LockException(exceptionList.get(0));
        }
        try {
            if (this.tryLock()) {
                System.out.println(Thread.currentThread().getName() + " " + lockName + "获得了锁");
                return;
            } else {
                // 等待锁
                waitForLock(waitLock, sessionTimeout);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    @Override
    public boolean tryLock() {
        if (lockName.contains(LOCK_SPLIT)) {
            throw new RuntimeException("Illegal lock name.");
        }
        try {
            currentLock = zk.create(LOCK_ROOT + PATH_SPLIT + lockName, new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> childrenNodes = zk.getChildren(LOCK_ROOT, false);
            List<String> sortedLocks = childrenNodes.stream()
                    .filter(node -> this.lockName.equals(node.split(LOCK_SPLIT)[0]))
                    .sorted()
                    .collect(Collectors.toList());
            if (currentLock.equals(sortedLocks.get(0))) {
                System.out.println(String.format("%s: Acquire lock %s success.", Thread.currentThread().getName(), lockName));
                return true;
            }

            String prevNode = currentLock.substring(currentLock.lastIndexOf(LOCK_SPLIT) + 1);
            waitLock = childrenNodes.get(Collections.binarySearch(childrenNodes, prevNode) - 1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        try {
            if (this.tryLock()) {
                return true;
            }
            return waitForLock(waitLock, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private boolean waitForLock(String prev, long waitTime) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(LOCK_ROOT + PATH_SPLIT + prev, true);

        if (stat != null) {
            System.out.println(Thread.currentThread().getName() + "等待锁 " + LOCK_ROOT + "/" + prev);
            this.countDownLatch = new CountDownLatch(1);
            // 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
            this.countDownLatch.await(waitTime, TimeUnit.MILLISECONDS);
            this.countDownLatch = null;
            System.out.println(Thread.currentThread().getName() + " 等到了锁");
        }
        return true;
    }


    @Override
    public void unlock() {
        try {
            System.out.println("释放锁 " + currentLock);
            zk.delete(currentLock, -1);
            currentLock = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(String.format("Event: %s", event));
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }

    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public LockException(String e){
            super(e);
        }
        public LockException(Exception e){
            super(e);
        }
    }

}
