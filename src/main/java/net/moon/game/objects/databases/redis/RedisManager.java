package net.moon.game.objects.databases.redis;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.configurations.DatabaseConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.moon.game.listeners.constants.PracticeLogger.debug;

public class RedisManager {

    @Getter private final JedisPool jedisPool;
    private final Map<String, RedisDatabase> redisDatabases;

    /**
     * Create and connect Redis instance.
     * @param instance Main instance of plugin.
     */
    public RedisManager(final Practice instance) {
        debug("Init Redis...");
        final DatabaseConfiguration config = instance.getDatabaseConfiguration();
        this.jedisPool = new JedisPool(
                new JedisPoolConfig(),
                config.getRedisHost(),
                config.getRedisPort(),
                config.getRedisTimeout(),
                config.getRedisPassword()
        );
        this.redisDatabases = new LinkedHashMap<>();
    }

    public void createDatabase(final String name, final int database) {
        this.redisDatabases.put(name, new RedisDatabase(this.jedisPool, name, database));
    }

    public RedisDatabase get(final String name) {
        return this.redisDatabases.get(name);
    }

    /**
     * Stop Redis instance.
     */
    public void stop() {
        final Jedis jedis = this.jedisPool.getResource();
        if (jedis != null) {
            jedis.close();
        }
        this.jedisPool.close();
    }

}
