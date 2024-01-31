package net.moon.game.objects.players;

import lombok.Data;
import net.moon.game.objects.match.Match;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerMatch {

    private final PlayerData playerData;
    private Match match;
    private final List<PlayerData> opponents;
    private PlayerData lastDamage;
    private long lastPearl;

    public PlayerMatch(final PlayerData playerData) {
        this.playerData = playerData;
        this.match = null;
        this.opponents = new ArrayList<>();
        this.lastDamage = null;
        this.lastPearl = 0;
    }
}
