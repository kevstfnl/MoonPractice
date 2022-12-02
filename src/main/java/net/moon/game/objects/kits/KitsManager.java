package net.moon.game.objects.kits;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.databases.mongodb.MongoManager;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.moon.game.listeners.constants.PracticeLogger.debug;

public class KitsManager {

    private final MongoManager mongoManager;
    @Getter private final Map<String, Kit> kits;

    public KitsManager(final Practice instance) {
        debug("Init Kit Manager...");
        this.mongoManager = instance.getMongoManager();
        this.kits = new ConcurrentHashMap<>();

        if (this.mongoManager.getKits() != null) {
            for (Document document : this.mongoManager.getKits().find()) {
                this.kits.put(document.getString("name"), new Kit(document));
            }
        }
    }

    public void stop() {
        for (Kit kit : this.kits.values()) {
            this.mongoManager.update(kit);
        }
        this.kits.clear();
    }

    public Kit create(final String name) {
        final Kit kit = new Kit(name);
        this.kits.put(name, kit);
        return kit;
    }

    public void delete(final Kit kit) {
        this.mongoManager.reset(kit);
        this.kits.remove(kit.getName());
    }

    public Kit get(final String name) {
        return this.kits.get(name);
    }

    public void save(final Kit kit) {
        this.mongoManager.update(kit);
    }
    public void saveAll() {
        this.kits.values().forEach(this::save);
    }
}
