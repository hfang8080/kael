// Copyright 2019 ALO7 Inc. All rights reserved.

package com.internet.common.lock;

import com.internet.common.util.Util;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Kael He(kael.he@alo7.com)
 */
public class ZookeeperDistributeLock implements DistributedLock, Watcher {

    private ZooKeeper zk = null;
    public static final String LOCK_SPLIT = "__lock__";
    private static final String PATH_SPLIT = "/";
    private String LOCK_ROOT = "/distribute_locks";
    public static final int sessionTimeout = 30000;

    public ZookeeperDistributeLock() {
        try {
            zk = new ZooKeeper("127.0.0.1:2181", sessionTimeout, this);
            Stat exists = zk.exists(LOCK_ROOT, false);
            if (null == exists) {
                zk.create(LOCK_ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
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
    public String acquireLock(String key, int expireSeconds) {
        String identifier = Util.generateUuid();
        try {
            String node = zk.create(LOCK_ROOT + PATH_SPLIT + key + LOCK_SPLIT, identifier.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            List<String> childrenNodes = zk.getChildren(LOCK_ROOT, false);
            List<String> sortedNodes = childrenNodes.stream()
                    .filter(n -> n.contains(key))
                    .sorted()
                    .collect(toList());
            if (sortedNodes.get(0).equals(node) && sortedNodes.get(0).getBytes() == identifier.getBytes()) {
                System.out.println(String.format("%s: Acquire lock %s success with %s",
                        Thread.currentThread().getName(), key, identifier));
                return identifier;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("%s: Acquire lock %s failed.",
                Thread.currentThread().getName(), key));
        return null;
    }

    @Override
    public boolean releaseLock(String key, String identifier) {
        try {
            List<String> childrenNodes = zk.getChildren(LOCK_ROOT, false);
            List<String> sortedNodes = childrenNodes.stream()
                    .filter(n -> n.contains(key))
                    .sorted()
                    .collect(toList());


            if (sortedNodes.size() > 0) {
                byte[] data = zk.getData(LOCK_ROOT + PATH_SPLIT + sortedNodes.get(0), false, null);
                if (new String(data).equals(identifier)) {
                    System.out.println(String.format("%s: Release lock %s success with %s.",
                            Thread.currentThread().getName(), key, identifier));
                    zk.delete(LOCK_ROOT + PATH_SPLIT + sortedNodes.get(0), -1);
                    return true;
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("%s: Release lock %s failed with %s.",
                Thread.currentThread().getName(), key, identifier));
        return false;
    }

    @Override
    public String acquireLockWithBlock(String key, int expireSeconds, int blockSeconds) {
        String identifier = Util.generateUuid();
        try {
            String node = zk.create(LOCK_ROOT + PATH_SPLIT + key + LOCK_SPLIT, identifier.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            Instant endTime = Instant.now().plusSeconds(blockSeconds);
            while (endTime.isAfter(Instant.now())) {
                List<String> childrenNodes = zk.getChildren(LOCK_ROOT, false);
                List<String> sortedNodes = childrenNodes.stream()
                        .filter(n -> n.contains(key))
                        .sorted()
                        .collect(toList());
                String nodeName = node.substring(node.lastIndexOf(PATH_SPLIT) + 1);
                if (sortedNodes.get(0).equals(nodeName)) {
                    System.out.println(String.format("%s: Acquire lock %s success with %s",
                            Thread.currentThread().getName(), key, identifier));
                    return identifier;
                }
                Thread.sleep(10);
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("%s: Acquire lock %s failed.",
                Thread.currentThread().getName(), key));
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void process(WatchedEvent event) {

    }
}
