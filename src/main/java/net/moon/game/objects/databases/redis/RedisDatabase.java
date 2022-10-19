package net.moon.game.objects.databases.redis;

import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Data
public class RedisDatabase {

    private final JedisPool jedisPool;
    private Jedis jedis;
    private final String name;
    private final int database;

    public RedisDatabase(final JedisPool jedisPool, final String name, final int database) {
        this.jedisPool = jedisPool;
        this.jedis = jedisPool.getResource();
        this.name = name;
        this.database = database;
    }

    /**
     * Set a value into redis
     * @param key key of object.
     * @param value new value of object.
     */
    public void set(final String key, final String value) {
        this.jedis = null;
        try {
            this.jedis = this.jedisPool.getResource();
            this.jedis.select(database);
            this.jedis.set(key, value);
        } finally {
            if (this.jedis != null) this.jedis.close();
        }
    }

    /**
     * Get a value into redis.
     * @param key key of object.
     * @return serialized object.
     */
    public String get(final String key) {
        String value;

        this.jedis = null;
        try {
            this.jedis = this.jedisPool.getResource();
            this.jedis.select(database);
            value = this.jedis.get(key);
        } finally {
            if (this.jedis != null) this.jedis.close();
        }
        return value;
    }

    /**
     * Delete a object into redis.
     * @param key key of object.
     */
    public void del(final String key) {
        this.jedis = null;
        try {
            this.jedis = this.jedisPool.getResource();
            this.jedis.select(database);
            this.jedis.del(key);
        } finally {
            if (this.jedis != null) this.jedis.close();
        }
    }

    /**
     * Clear redis database.
     */
    public void flushAll() {
        this.jedis = null;
        try {
            this.jedis = this.jedisPool.getResource();
            this.jedis.select(database);
            this.jedis.flushAll();
        } finally {
            if (this.jedis != null) this.jedis.close();
        }
    }
}
