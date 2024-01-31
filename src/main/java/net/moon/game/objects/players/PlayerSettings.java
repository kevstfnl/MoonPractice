package net.moon.game.objects.players;

import lombok.Data;
import org.bson.Document;


@Data
public class PlayerSettings {

    private boolean playersInLobby;

    public PlayerSettings() {

    }
    public PlayerSettings(final Document document) {

    }
    public Document toDocument() {
        final Document toReturn = new Document();
        return toReturn;
    }
    /*
    ADMIN:
        SILENT Arena generator
     */

    /*
    MODERATOR
        REPORTS
     */

    /*
    PLAYERS-VIP:
        NICK
        TAG
    PLAYERS:
        SCOREBOARD
        PLAYERS IN LOBBY
        PRIVATE MESSAGE
        DUEL REQUEST
        PARTY REQUEST
     */

}
