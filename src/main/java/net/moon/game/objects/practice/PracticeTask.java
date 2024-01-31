package net.moon.game.objects.practice;

import net.moon.game.Practice;
import net.moon.game.constants.PracticeLogger;
import net.moon.game.objects.leaderboards.LeaderboardsManager;
import net.moon.game.objects.practice.lobby.Lobby;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayersManager;
import org.bukkit.entity.Player;


import static net.moon.game.constants.PracticeLogger.log;

public class PracticeTask implements Runnable {

    private final PlayersManager playersManager;
    private final LeaderboardsManager leaderboardsManager;
    private final Lobby lobby;
    private int ticks;
    private int seconds;
    private int minutes;

    public PracticeTask(final Practice instance) {
        this.playersManager = instance.getPlayersManager();
        this.leaderboardsManager = instance.getLeaderboardsManager();
        this.lobby = instance.getPracticeManager().getLobby();
    }

    @Override
    public void run() {
        this.ticks++;

        if (this.ticks > 20) {
            this.ticks = 0;
            this.seconds++;

            for (PlayerData playerData : this.playersManager.getPlayers().values()) {
                switch (playerData.getState()) {
                    case LOBBY, QUEUE -> {
                        final Player player = playerData.getPlayer();
                        if (!this.lobby.getBorder().isIn(player)) {
                            player.teleport(this.lobby.getSpawn());
                        }
                    }
                }
            }

            if (this.seconds > 60) {
                this.seconds = 0;
                this.minutes++;

                this.leaderboardsManager.update();

                if (this.minutes == 15) {
                    this.playersManager.getPlayers().forEach((uuid, playerData) -> this.playersManager.save(playerData));
                    log(PracticeLogger.LogLevel.INFO, "All players are be saving into redis and mongo databases.");
                }
            }
        }
    }
}
