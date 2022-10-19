package net.moon.game.objects.leaderboards;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.players.PlayerData;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Leaderboard {

    private final String kit;
    private Map<String, Integer> leaderboard;

    public Leaderboard(final String kit) {
        this.kit = kit;
        this.leaderboard = new LinkedHashMap<>(11);
    }

    public Leaderboard(final Document document) {
        this.kit = document.getString("kit");
        this.leaderboard = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : ((Document)document.get("leaderboard")).entrySet()) {
            this.leaderboard.put(entry.getKey(), (Integer) entry.getValue());
        }
    }

    public void check(final PlayerData playerData) {
        final String name = playerData.getPlayer().getName();
        final Kit kit = Practice.get().getKitsManager().get(this.kit);
        this.leaderboard.put(name, playerData.getKits().get(kit).getElo());
        update();
    }

    public void update() {
        this.leaderboard = this.leaderboard.entrySet().stream()
                .limit(10)
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public List<String> getFormattedLeaderboard() {
        final List<String> lines = new ArrayList<>();
        lines.add("§6§l" + this.kit + "'s Leaderboards");
        lines.add("§8§m-=-----------------=-");
        int number = 1;
        for (Map.Entry<String, Integer> entry : this.leaderboard.entrySet()) {
            final String playerName = entry.getKey() == null || entry.getKey().isEmpty() ? "Another" : entry.getKey();
            final int playerElo = entry.getValue() == null ? 0 : entry.getValue();
            lines.add("§f§l" + number + "§7»" + playerName + "§7(" + playerElo + "§7");
            number++;
        }
        lines.add("§8§m-=-----------------=-");
        return lines;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("kit", this.kit);
        final Document leaderboard = new Document();
        leaderboard.putAll(this.leaderboard);
        toReturn.put("leaderboard", leaderboard);
        return toReturn;
    }


}
