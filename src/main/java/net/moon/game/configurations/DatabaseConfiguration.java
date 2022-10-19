package net.moon.game.configurations;

import lombok.Getter;
import net.moon.game.utils.commons.YamlFile;

import java.nio.file.Path;

@Getter
public class DatabaseConfiguration {
    private final YamlFile yml;

    //Mongo
    private final String mongoName, mongoHost, mongoDatabase, mongoUsername, mongoPassword;
    private final int mongoPort;
    private final boolean mongoAuthentication;

    //Redis
    private final String redisHost, redisPassword;
    private final int redisPort, redisTimeout;

    public DatabaseConfiguration(final Path path) {
        this.yml = new YamlFile("database", path, null);

        //Mongo
        this.mongoName = this.yml.getString("mongo.name", "MoonPractice");
        this.mongoHost = this.yml.getString("mongo.host", "127.0.0.1");
        this.mongoPort = this.yml.getInt("mongo.port", 27017);
        this.mongoAuthentication = this.yml.getBoolean("mongo.authentication.enabled", false);
        this.mongoUsername = this.yml.getString("mongo.authentication.username", "");
        this.mongoPassword = this.yml.getString("mongo.authentication.password", "");
        this.mongoDatabase = this.yml.getString("mongo.authentication.database", "admin");

        //Redis
        this.redisHost = this.yml.getString("redis.host", "127.0.0.1");
        this.redisPort = this.yml.getInt("redis.port", 6379);
        this.redisTimeout = this.yml.getInt("redis.timeout", 3000);
        this.redisPassword = this.yml.getString("redis.password", "admin");
        this.yml.save();
    }
}