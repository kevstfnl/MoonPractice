package net.moon.game.configurations;

import lombok.Getter;
import net.moon.core.commons.yaml.YAMLConfiguration;

import java.io.File;
import java.util.Collections;

@Getter
public class DatabaseConfiguration {

    //Mongo
    private final String mongoName, mongoHost, mongoDatabase, mongoUsername, mongoPassword;
    private final int mongoPort;
    private final boolean mongoAuthentication;

    //Redis
    private final String redisHost, redisPassword;
    private final int redisPort, redisTimeout;

    public DatabaseConfiguration(final File path) {
        final File file = new File(path, "database.yml");
        final YAMLConfiguration config = new YAMLConfiguration(file, "");

        //Mongo
        config.setComment("databases.mongo", Collections.singletonList("Mongo Database configuration section:"));
        this.mongoName = config.getString("databases.mongo.name", "MoonPractice");
        this.mongoHost = config.getString("databases.mongo.host", "127.0.0.1");
        this.mongoPort = config.getInt("databases.mongo.port", 27017);
        this.mongoAuthentication = config.getBoolean("databases.mongo.authentication.enabled", false);
        this.mongoUsername = config.getString("databases.mongo.authentication.username", "");
        this.mongoPassword = config.getString("databases.mongo.authentication.password", "");
        this.mongoDatabase = config.getString("databases.mongo.authentication.database", "admin");

        //Redis
        config.setComment("databases.redis", Collections.singletonList("Redis Database configuration section:"));
        this.redisHost = config.getString("databases.redis.host", "127.0.0.1");
        this.redisPort = config.getInt("databases.redis.port", 6379);
        this.redisTimeout = config.getInt("databases.redis.timeout", 3000);
        this.redisPassword = config.getString("databases.redis.password", "admin");
        config.save();
    }
}