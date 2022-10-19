package net.moon.game.objects.leaderboards;

import com.mongodb.client.model.Filters;
import net.moon.game.Practice;
import net.moon.game.objects.databases.mongodb.MongoManager;
import net.moon.game.objects.databases.redis.RedisDatabase;
import net.moon.game.objects.databases.redis.RedisManager;
import net.moon.game.objects.kits.Kit;
import org.bson.Document;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardsManager {

    private final MongoManager mongoManager;
    private final RedisDatabase redisDatabase;
    private final Map<String, Leaderboard> leaderboards;

    public LeaderboardsManager(final Practice instance) {
        this.mongoManager = instance.getMongoManager();

        final RedisManager redisManager = instance.getRedisManager();
        redisManager.createDatabase("leaderboard", 1);
        this.redisDatabase = redisManager.get("leaderboard");

        final Collection<Kit> kits = instance.getKitsManager().getKits().values();
        this.leaderboards = new ConcurrentHashMap<>(kits.size());

        for (Kit kit : kits) {
            final String name = kit.getName();
            this.leaderboards.put(name, this.get(name));
        }
    }

    public Leaderboard get(final String name) {
        Leaderboard leaderboard = this.leaderboards.get(name);
        if (leaderboard == null) {
            leaderboard = this.getFromRedis(name);
            if (leaderboard == null) {
                leaderboard = this.getFromMongo(name);
                if (leaderboard == null) {
                    leaderboard = new Leaderboard(name);
                }
            }
        }
        return leaderboard;
    }
    
    public void update() {
        this.leaderboards.values().forEach(Leaderboard::update);
    }

    public void stop() {
        this.leaderboards.values().forEach(Leaderboard::update);
        this.leaderboards.values().forEach(this::saveToRedis);
        this.leaderboards.values().forEach(this::saveToMongo);
    }

    public Leaderboard getFromRedis(final String kit) {
        final Document document = Document.parse(this.redisDatabase.get(kit));
        if (document == null) return null;
        return new Leaderboard(document);
    }
    public Leaderboard getFromMongo(final String kit) {
        final Document document = this.mongoManager.getLeaderboards().find(Filters.eq("kit", kit)).limit(1).first();
        if (document == null) return null;
        return new Leaderboard(document);
    }
    public void saveToMongo(final Leaderboard leaderboard) {
        this.mongoManager.update(leaderboard);
    }
    public void saveToRedis(final Leaderboard leaderboard) {
        this.redisDatabase.set(leaderboard.getKit(), leaderboard.toDocument().toJson());
    }
}
