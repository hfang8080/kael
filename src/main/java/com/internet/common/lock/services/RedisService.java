package com.internet.common.lock.services;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

/**
 * @author Kael He
 */
public class RedisService {
    private static JedisPool pool = null;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMaxIdle(200);
        config.setMaxWaitMillis(1000 * 100);
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, "127.0.0.1", 6379, 3000);
    }

    public long setNx(String key, String value) {
        Jedis instance = null;
        long count = 0;
        try {
            instance = pool.getResource();

            count = instance.setnx(key, value);
        } catch (JedisException ex) {
            ex.printStackTrace();
        } finally {
            if (instance != null) {
                instance.close();
            }
        }
        return count;
    }

    public long expire(String key, int expire) {
        Jedis instance = null;
        long count = 0;
        try {
            instance = pool.getResource();
            if (instance.ttl(key) == -1) {
                count = instance.expire(key, expire);
            }
        } catch (JedisException ex) {
            ex.printStackTrace();
        } finally {
            if (instance != null) {
                instance.close();
            }
        }
        return count;
    }

    public long delete(String key) {
        Jedis instance = null;
        long count = 0;
        try {
            instance = pool.getResource();
            count = instance.del(key);
        } catch (JedisException ex) {
            ex.printStackTrace();
        } finally {
            if (instance != null) {
                instance.close();
            }
        }
        return count;
    }

    public boolean setNxWithExpire(String key, String value, int expire) {
        Jedis instance = null;
        try {
            instance = pool.getResource();
            if (instance.ttl(key) == -1) {
                Transaction multi = instance.multi();
                instance.setnx(key, value);
                instance.expire(key, expire);
                List<Object> exec = multi.exec();
                System.out.println(exec);
                return true;
            }
        } catch (JedisException ex) {
            ex.printStackTrace();
        } finally {
            if (instance != null) {
                instance.close();
            }
        }
        return false;
    }

    public String get(String key) {
        Jedis instance = null;
        String value = null;
        try {
            instance = pool.getResource();
            value = instance.get(key);
        } catch (JedisException ex) {
            ex.printStackTrace();
        } finally {
            if (instance != null) {
                instance.close();
            }
        }
        return value;
    }

    public void close() {
        if (pool != null) pool.destroy();
    }

    public static void main(String[] args) {
        RedisService redisService = new RedisService();
        redisService.setNx("k", "v");
    }

}
