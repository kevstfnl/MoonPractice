package net.moon.game.objects.queues.impl;

import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.match.Match;
import net.moon.game.objects.match.MatchType;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayerQueue;
import net.moon.game.objects.queues.Queue;
import net.moon.game.objects.queues.QueueType;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicQueue extends Queue {

    private final boolean ranked;

    public ClassicQueue(final QueueType type, final Kit kit) {
        super(type, kit);
        this.ranked = type.isRanked();
    }

    public void add(PlayerData playerData) {
        this.queue.add(playerData);
    }

    public void remove(PlayerData playerData) {
        this.queue.remove(playerData);
    }

    public void shuffle() {
        final Random random = new Random();
        final ConcurrentLinkedDeque<Object> randomData = new ConcurrentLinkedDeque<>();
        for (Object o : this.queue) {
            if (o instanceof PlayerData playerData) {
                if (random.nextBoolean()) {
                    randomData.addFirst(playerData);
                } else {
                    randomData.addLast(playerData);
                }
            }
        }
        this.queue = randomData;
    }

    @Override
    public void run() {
        shuffle();
        for (Object o : this.queue) {
            if (o instanceof PlayerData playerData) {

                if (!playerData.inQueue()) continue;
                final PlayerQueue playerQueue = playerData.getPlayerQueue();

                for (Object o2 : this.queue) {
                    if (o2 instanceof PlayerData other) {
                        if (other.equals(playerData)) continue;
                        if (!other.inQueue()) continue;
                        final PlayerQueue otherQueue = other.getPlayerQueue();

                        if (this.ranked) {
                            final long time = System.currentTimeMillis() - playerQueue.getCurrentQueues().get(this);
                            final long search = time / 1000 * 5;
                            final int playerElo = playerData.getKits().get(this.getKit()).getElo();
                            final int otherElo = other.getKits().get(this.getKit()).getElo();
                            if (!(otherElo > playerElo - search || otherElo < playerElo + search)) continue;
                        }
                        playerQueue.clear();
                        otherQueue.clear();

                        final Match match = new Match(MatchType.TEAM, this.getKit(), this.ranked);
                        match.addPlayers(playerData, other);
                        match.setReady(true);

                        final Player player = playerData.getPlayer();
                        final Player otherPlayer = other.getPlayer();
                        final String kitName = match.getKit().getName();
                        final String mapName = match.getStandaloneArena().getArena().getName();
                        player.sendMessage(
                                "§8» §8§m---------------------§8 «" + "\n" +
                                        "§a§lMatch found:\n" +
                                        "  §eKit: §7" + kitName + "\n" +
                                        "  §eMap: §7" + mapName + "\n" +
                                        "  §eOpponent: §7" + otherPlayer.getName() + (match.isRanked() ?
                                        "§8(§6" + other.getKits().get(this.getKit()).getElo() + "§7elo§8)\n" : "\n") +
                                        "  §eOpponent ping: §7" + otherPlayer.spigot().getPing() + "\n" +
                                        "§8» §8§m---------------------§8 «" + "\n"
                        );
                        otherPlayer.sendMessage(
                                "§8» §8§m---------------------§8 «" + "\n" +
                                        "§a§lMatch found:\n" +
                                        "  §eKit: §7" + kitName + "\n" +
                                        "  §eMap: §7" + mapName + "\n" +
                                        "  §eOpponent: §7" + player.getName() + (match.isRanked() ?
                                        "§8(§6" + playerData.getKits().get(this.getKit()).getElo() + "§7elo§8)\n" : "\n") +
                                        "  §eOpponent ping: §7" + player.spigot().getPing() + "\n" +
                                        "§8» §8§m---------------------§8 «" + "\n"
                        );
                    }
                }
            }
        }
    }
}
