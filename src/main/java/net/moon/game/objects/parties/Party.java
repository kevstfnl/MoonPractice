package net.moon.game.objects.parties;

import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;
import net.moon.game.Practice;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayerState;
import net.moon.game.utils.commons.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Data
public class Party {

    private final Practice instance;
    private String name;
    private PartyState state;
    private final PlayerData leader;
    private final List<PlayerData> members;
    private final List<PlayerData> invitedPlayers;

    private boolean open;
    private int maxPlayers;


    public Party(final PlayerData playerData) {
        this.instance = playerData.getInstance();

        this.name = playerData.getPlayer().getName() + "'s party";
        this.state = PartyState.LOBBY;
        this.leader = playerData;

        this.members = new ArrayList<>();
        this.members.add(this.leader);

        this.invitedPlayers = new ArrayList<>();

        this.open = false;
        this.maxPlayers = 2;
        this.updateMember(playerData);
    }

    public void join(final PlayerData playerData) {
        final Player player = this.leader.getPlayer();
        final Player invited = playerData.getPlayer();

        if (this.members.contains(playerData)) {
            invited.sendMessage("§cYour are already in this party !");
            return;
        }

        if (!this.invitedPlayers.contains(playerData) && !this.open) {
            invited.sendMessage("§cYou aren't invited in this party !");
            return;
        }

        if (this.members.size() == this.maxPlayers) {
            invited.sendMessage("§cThis party is full !");
            player.sendMessage("§c" + invited.getName() + " has tried to join but your party is full !");
            return;
        }

        playerData.getRequests().removePartyInvitation(this);
        this.invitedPlayers.remove(playerData);
        this.members.add(playerData);
        updateMember(playerData);
        sendMessage(" §a+ §f" + player.getName() + "§7has join your party §a(" + this.members.size() + "/" + this.maxPlayers + ")");
    }
    public void leave(final PlayerData playerData) {
        if (playerData.equals(this.leader)) disband();

        this.members.remove(playerData);

        final Player player = playerData.getPlayer();
        playerData.setState(PlayerState.LOBBY);
        playerData.setParty(null);
        PlayerUtils.resetPlayer(player);
        player.getInventory().setContents(this.instance.getPracticeManager().getHotbar().getHotbar(playerData));
        sendMessage(" §c- §f" + player.getName() + "§7has left your party §c(" + this.members.size() + "/" + this.maxPlayers + ")");
    }
    public void disband() {
        this.instance.getPartyManager().delete(this);
        this.members.forEach(this::leave);
    }

    public void invite(final PlayerData playerData) {
        final Player player = this.getLeader().getPlayer();
        if (this.members.contains(playerData)) {
            player.sendMessage("§cThis player is already in your party !");
            return;
        }
        if (playerData.inParty()) {
            player.sendMessage("§cThis player is already in party !");
            return;
        }
        if (playerData.inMatch()) {
            player.sendMessage("§cThis player is busy !");
            return;
        }

        this.invitedPlayers.add(playerData);
        playerData.getRequests().addPartyInvitation(this);

        this.leader.getPlayer().sendMessage("§aInvitation to your party has been send to §e" + playerData.getPlayer().getName());
    }
    public void removeInvite(final PlayerData playerData) {
        this.invitedPlayers.remove(playerData);
        this.leader.getPlayer().sendMessage("§cYour party invitation of " + playerData.getPlayer().getName() + " has expired.");
    }

    public void updateMember(final PlayerData playerData) {
        final Player player = playerData.getPlayer();
        playerData.setState(PlayerState.PARTY);
        playerData.setParty(this);
        playerData.getPlayerQueue().clear();
        PlayerUtils.resetPlayer(player);
        player.getInventory().setContents(this.instance.getPracticeManager().getHotbar().getHotbar(playerData));
    }
    public void updateMembers() {
        this.members.forEach(this::updateMember);
    }

    public void sendMessage(final String message) {
        this.members.forEach(playerData -> {
            final Player player = playerData.getPlayer();
            player.sendMessage(message);
        });
    }
    public void sendRawMessage(final TextComponent message) {
        this.members.forEach(playerData -> {
            final Player player = playerData.getPlayer();
            player.spigot().sendMessage(message);
        });
    }
}
