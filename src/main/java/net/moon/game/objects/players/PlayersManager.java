package net.moon.game.objects.players;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.databases.mongodb.MongoManager;
import net.moon.game.objects.databases.redis.RedisDatabase;
import net.moon.game.objects.databases.redis.RedisManager;
import net.moon.game.objects.menus.MenusManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.moon.game.constants.PracticeLogger.debug;

public class PlayersManager {

    private final Practice instance;
    private final MongoManager mongoManager;
    private final RedisDatabase redisDatabase;
    private final MenusManager menusManager;
    @Getter private final Map<UUID, PlayerData> players;

    public PlayersManager(final Practice instance) {
        debug("Init Player Manger...");
        this.instance = instance;
        this.mongoManager = instance.getMongoManager();
        
        final RedisManager redisManager = instance.getRedisManager();
        redisManager.createDatabase("players", 0);
        this.redisDatabase = redisManager.get("players");

        this.menusManager = instance.getMenusManager();
        
        this.players = new ConcurrentHashMap<>();

        //Security for already online players
        Bukkit.getOnlinePlayers().forEach(this::inject);
    }

    public void stop() {
        this.players.values().forEach(this::saveToMongo);
        this.players.clear();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§c§lServer stopping."));
    }

    public void inject(final Player player) {
        this.instance.execute(() -> {
            final UUID uuid = player.getUniqueId();
            PlayerData playerData = getFromRedis(uuid);
            if (playerData == null) {
                playerData = getFromMongo(uuid);
                if (playerData == null) {
                    playerData = new PlayerData(player);
                    this.save(playerData);
                }
                this.saveToRedis(playerData);
            }
            this.players.put(uuid, playerData);
            this.menusManager.injectPlayerGui(playerData);
        });
    }

    public void uninject(final PlayerData playerData) {
        this.instance.execute(() -> {
            this.menusManager.uninjectPlayerGui(playerData.getUuid());
            playerData.setState(PlayerState.OFFLINE);
            saveToRedis(playerData);
        });
    }

    public void delete(final PlayerData playerData)  {
        this.instance.execute(() -> {
            this.redisDatabase.del(playerData.getUuid().toString());
            this.mongoManager.reset(playerData);
        });
    }

    public PlayerData get(final Player player) { return this.get(player.getUniqueId()); }
    public PlayerData get(final UUID uuid) { return this.players.get(uuid); }

    public void save(final PlayerData playerData) {
        saveToRedis(playerData);
        saveToMongo(playerData);
    }

    public void saveToMongo(final PlayerData playerData) {
        this.mongoManager.update(playerData);
    }
    public void saveToRedis(final PlayerData playerData) {
        this.redisDatabase.set(playerData.getUuid().toString(), playerData.toDocument().toJson());
    }

    public PlayerData getFromRedis(final UUID uuid) {
        final Document document = Document.parse(this.redisDatabase.get(uuid.toString()));
        if (document == null) return null;
        return new PlayerData(document);
    }
    public PlayerData getFromMongo(final UUID uuid) {
        final Document document = this.mongoManager.getPlayers().find(Filters.eq("uuid", uuid)).limit(1).first();
        if (document == null) return null;
        return new PlayerData(document);
    }
}