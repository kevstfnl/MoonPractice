package net.moon.game.objects.arenas;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.databases.mongodb.MongoManager;
import net.moon.game.utils.world.VoidGenerator;
import net.moon.game.utils.world.WorldUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static net.moon.game.constants.PracticeLogger.log;

public class ArenasManager {

    private static ArenasManager arenaManager;
    private final Practice instance;
    private final MongoManager mongoManager;
    private final ExecutorService threadArenaGenerator;
    private final Map<String, Arena> arenas;
    @Getter private final World arenaWorld;

    public ArenasManager(final Practice instance) {
        arenaManager = this;
        this.instance = instance;
        this.mongoManager = instance.getMongoManager();
        this.threadArenaGenerator = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder().setPriority(1).setNameFormat("Arena Generator Executor").build());
        this.arenas = new ConcurrentHashMap<>();

        WorldUtils.deleteWorld("arenas"); //Delete arena world if server are stopped caused by crash or kill.
        WorldCreator wc = new WorldCreator("arenas"); //Create arena world.
        wc.generator(new VoidGenerator()); //Set custom void generator.
        log("Generating world arenas...");
        this.arenaWorld = wc.createWorld();//Generate arena world.

        final MongoManager mongo = instance.getMongoManager();
        if (mongo.getArenas() != null) {
            for (Document document : mongo.getKits().find()) {
                this.arenas.put(document.getString("name"), new Arena(document));
            }
        }
    }

    public void stop() {
        final MongoManager mongo = this.instance.getMongoManager();
        for (Arena arena : this.arenas.values()) {
            mongo.update(arena);
        }
    }

    public static ArenasManager get() { return arenaManager; }

    public void execute(final Runnable runnable) {
        this.threadArenaGenerator.execute(runnable);
    }
    public void create(final String name) {
        final int id = this.arenas.size();
        this.arenas.put(name, new Arena(name, id));
    }

    public void delete(final String name) {
        final Arena arena = get(name);
        if (arena != null) {
            arena.setEnabled(false);
            this.instance.getMongoManager().reset(arena);
        }
    }

    public Arena get(final String name) {
        return this.arenas.get(name);
    }

    public Arena getRandom() {
        if (!this.arenas.isEmpty()) {
            final List<Arena> availableArena = new ArrayList<>();
            for (Arena arena : this.arenas.values()) {
                if (arena.isEnabled()) {
                    availableArena.add(arena);
                }
            }
            return availableArena.get(ThreadLocalRandom.current().nextInt(availableArena.size()));
        }
        return null;
    }
    public Arena getRandomWithType(final ArenaType type) {
        if (!this.arenas.isEmpty()) {
            final List<Arena> availableArena = new ArrayList<>();
            for (Arena arena : this.arenas.values()) {
                if (arena.isEnabled() && arena.getType().equals(type)) {
                    availableArena.add(arena);
                }
            }
            return availableArena.get(ThreadLocalRandom.current().nextInt(availableArena.size()));
        }
        return null;
    }

    public void save(final Arena arena) {
        this.mongoManager.update(arena);
    }
    public void saveAll() {
        this.arenas.values().forEach(this::save);
    }

}
