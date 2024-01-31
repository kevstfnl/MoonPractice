package net.moon.game.objects.arenas;

import lombok.Getter;
import net.eno.utils.world.generators.VoidGenerator;
import net.moon.game.Practice;
import net.moon.game.objects.databases.mongodb.MongoManager;
import net.moon.game.objects.kits.Kit;
import net.moon.game.utils.WorldUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static net.moon.game.constants.PracticeLogger.debug;

public class ArenasManager {

    private final MongoManager mongoManager;
    @Getter private final World world;
    @Getter private final Map<String, Arena> arenas;
    @Getter private final Map<Arena, List<StandaloneArena>> copies;

    public ArenasManager(final Practice instance) {
        debug("Init Arena Manager...");
        this.mongoManager = instance.getMongoManager();

        WorldUtils.deleteWorld("arenas");
        this.world = Bukkit.createWorld(new WorldCreator("arenas").generator(new VoidGenerator()));

        this.arenas = new ConcurrentHashMap<>();
        this.copies = new HashMap<>();

        if (this.mongoManager.getArenas() != null) {
            for (Document document : this.mongoManager.getArenas().find()) {
                this.arenas.put(document.getString("name"), new Arena(document));
            }
        }
        if (!this.arenas.isEmpty()) this.arenas.values().forEach(this::generate);
    }

    public void stop() {
        for (Arena arena : this.getArenas().values()) {
            this.mongoManager.update(arena);
        }
        this.arenas.clear();
        WorldUtils.deleteWorld("arenas");
    }

    public void generate(final Arena arena, final int number) {
        for (int i = 0; i < number; i++) {
            generate(arena);
        }
    }
    public void generate(final Arena arena) {
        final int id = this.copies.get(arena).size();
        this.copies.get(arena).add(new StandaloneArena(id, arena));
    }

    public Arena getRandomArena() {
        if (!this.arenas.isEmpty()) {
            final List<Arena> toReturn = new ArrayList<>();
            for (Arena arena : this.arenas.values()) {
                if (arena.isEnabled()) {
                    toReturn.add(arena);
                }
            }
            return toReturn.get(ThreadLocalRandom.current().nextInt(toReturn.size()));
        }
        return null;
    }
    public Arena getRandomArena(final Kit kit) {
        if (!this.arenas.isEmpty()) {
            final List<Arena> toReturn = new ArrayList<>();
            for (Arena arena : this.arenas.values()) {
                if (arena.isEnabled()) {
                    if (arena.isBlacklistMode() && !arena.getKits().contains(kit)) toReturn.add(arena);
                    if (arena.getKits().isEmpty() || (!arena.isBlacklistMode() && arena.getKits().contains(kit))) {
                        toReturn.add(arena);
                    }
                }
            }
            return toReturn.get(ThreadLocalRandom.current().nextInt(toReturn.size()));
        }
        return null;
    }
    public StandaloneArena getAvailableArena(final Arena arena) {
        for (StandaloneArena toReturn : this.copies.get(arena)) if (!toReturn.isUsed()) return toReturn;
        return null;
    }
    public StandaloneArena getAvailableArena(final Arena arena, boolean withBuild) {
        if (!withBuild) return this.copies.get(arena).get(0);
        for (StandaloneArena toReturn : this.copies.get(arena)) if (!toReturn.isUsed()) return toReturn;
        return null;
    }
    public StandaloneArena getAvailableArena(final Kit kit) {
        final Arena arena = this.getRandomArena(kit);
        return this.getAvailableArena(arena, kit.containBlock());
    }

    public Arena get(final String name) {
        return this.arenas.get(name);
    }
    public void save(final Arena arena) {
        this.mongoManager.update(arena);
    }
    public void saveAll() {
        this.arenas.values().forEach(this::save);
    }
}
