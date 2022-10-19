package net.moon.game.objects.practice;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.databases.mongodb.MongoManager;
import net.moon.game.objects.practice.hotbar.Hotbar;
import net.moon.game.objects.practice.lobby.Lobby;
import org.bson.Document;


@Getter
public class PracticeManager {

    private final MongoManager mongoManager;
    private final Lobby lobby;
    private final Hotbar hotbar;

    public PracticeManager(final Practice instance) {
        this.mongoManager = instance.getMongoManager();
        final Document lobby = this.mongoManager.getPractice().find().first();
        if (lobby != null) {
            this.lobby = new Lobby(lobby);
        } else {
            this.lobby = new Lobby();
        }
        this.hotbar = new Hotbar();
    }

    public void stop() {
        this.mongoManager.update(this.lobby);
    }
}
