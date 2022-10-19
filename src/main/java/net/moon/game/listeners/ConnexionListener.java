package net.moon.game.listeners;

import net.moon.game.Practice;
import net.moon.game.objects.practice.lobby.Lobby;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayersManager;
import net.moon.game.objects.players.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnexionListener implements Listener {

    private final PlayersManager playerManager;
    private final Lobby lobby;

    public ConnexionListener(final Practice instance) {
        this.playerManager = instance.getPlayersManager();
        this.lobby = instance.getPracticeManager().getLobby();
    }

    @EventHandler
    public void onPreJoin(final AsyncPlayerPreLoginEvent e) {
        final UUID uuid = e.getUniqueId();
        if (!Practice.isStarted) {
            e.setKickMessage("§c§lServer is starting...\n §c§lPlease wait !");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
        final PlayerData playerData = this.playerManager.get(uuid);
        if (playerData != null && !playerData.getState().equals(PlayerState.OFFLINE)) {
            e.setKickMessage("§c§lYou are already connected !\n §c§lRetry later...");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.setJoinMessage(null);
        final Player player = e.getPlayer();
        player.teleport(this.lobby.getSpawn());
        this.playerManager.inject(player);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        e.setQuitMessage(null);
        final Player player = e.getPlayer();
        final PlayerData playerData = this.playerManager.get(player);
        this.playerManager.uninject(playerData);
    }

    @EventHandler
    public void onKick(final PlayerKickEvent e) {
        e.setLeaveMessage(null);
        final Player player = e.getPlayer();
        final PlayerData playerData = this.playerManager.get(player);
        this.playerManager.uninject(playerData);
    }
}
