package net.moon.game.objects.match;

import lombok.Data;
import net.moon.game.Practice;
import net.moon.game.objects.arenas.ArenasManager;
import net.moon.game.objects.arenas.StandaloneArena;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.parties.Party;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayerMatch;
import net.moon.game.objects.players.PlayerState;
import net.moon.game.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Match {
    private final ArenasManager arenasManager;
    private final int id;

    private MatchState state;

    private final MatchTask task;
    private final MatchType type;
    private final Kit kit;
    private final boolean ranked;

    private StandaloneArena standaloneArena;

    private final List<Entity> entities;
    private final List<PlayerData> players, firstTeam, secondTeam, spectators, dies;
    private final Map<Integer, MatchStats> snapshots;
    private boolean ready;


    private long pending, start, end;

    public Match(final MatchType type, final Kit kit, final boolean ranked) {
        final Practice instance = Practice.get();
        this.arenasManager = instance.getArenasManager();
        this.id = this.hashCode();

        this.state = MatchState.CREATING;

        this.task = new MatchTask(this);
        this.type = type;
        this.kit = kit;
        this.ranked = ranked;

        this.arenasManager.generate(this.standaloneArena.getArena());
        this.standaloneArena = this.arenasManager.getAvailableArena(this.kit);

        this.entities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.firstTeam = new ArrayList<>();
        this.secondTeam = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.dies = new ArrayList<>();
        this.snapshots = new ConcurrentHashMap<>();
        this.ready = false;
        instance.getServer().getScheduler().runTaskTimer(instance, this.task, 0, 1);
    }

    public void sendMessage(final String message) {
        this.players.forEach(playerData -> playerData.getPlayer().sendMessage(message));
    }

    public void setTeams(final List<PlayerData> team1, final List<PlayerData> team2) {
        this.firstTeam.addAll(team1);
        this.secondTeam.addAll(team2);
    }

    public void addParty(final Party party) {
        this.players.addAll(party.getMembers());
    }

    public void addPlayer(final PlayerData playerData) {
        this.players.add(playerData);
    }

    public void addPlayers(final PlayerData... playersDatas) {
        this.players.addAll(List.of(playersDatas));
    }

    public void removePlayer(final PlayerData playerData) {
        this.players.remove(playerData);
    }

    public void addSpectator(final PlayerData playerData) {
        this.spectators.add(playerData);
    }

    public void removeSpectator(final PlayerData playerData) {
        this.spectators.remove(playerData);
    }

    public void addEntity(final Entity entity) {
        this.entities.add(entity);
    }
    public void removeEntity(final Entity entity) {
        this.entities.remove(entity);
    }

    private void splitPlayers() {
        Collections.shuffle(this.players);
        this.firstTeam.addAll(this.players.subList(0, this.players.size() / 2 + this.players.size() % 2));
        this.secondTeam.addAll(this.players.subList(this.players.size() / 2 + this.players.size() % 2, this.players.size()));
    }

    public void apply() {
        this.standaloneArena.setUsed(true);
        if (this.players.isEmpty()) {
            this.players.addAll(this.firstTeam);
            this.players.addAll(this.secondTeam);
        }
        if (this.firstTeam.isEmpty() || this.secondTeam.isEmpty()) {
            splitPlayers();
        }
        applyPlayer();
        createMatchStats();
        this.pending = System.currentTimeMillis();
    }

    private void applyPlayer() {
        this.players.forEach(playerData -> {

            final Player player = playerData.getPlayer();
            playerData.setState(PlayerState.MATCH);
            playerData.getPlayerMatch().setMatch(this);

            switch (this.type) {
                case FFA -> {
                    final Random random = new Random();
                    player.teleport(this.standaloneArena.getCenter().add(random.nextInt() * 0.25D, 0, random.nextInt() * 0.25D));
                    final List<PlayerData> clone = this.players;
                    clone.remove(playerData);
                    playerData.getPlayerMatch().getOpponents().addAll(clone);
                }
                case TEAM -> {
                    if (this.firstTeam.contains(playerData)) {
                        player.teleport(this.standaloneArena.getSpawn(0));
                        playerData.getPlayerMatch().getOpponents().addAll(this.secondTeam);
                    } else if (this.secondTeam.contains(playerData)) {
                        player.teleport(this.standaloneArena.getSpawn(1));
                        playerData.getPlayerMatch().getOpponents().addAll(this.firstTeam);
                    }
                }
            }
            this.kit.applyKit(playerData);
        });
    }

    private void createMatchStats() {
        int i = 0;
        for (PlayerData playerData : this.players) {
            this.snapshots.put(i, new MatchStats(playerData));
            i++;
        }
    }

    public MatchStats getStats(final String username) {
        for (MatchStats snapshot : this.snapshots.values()) {
            if (snapshot.getPlayerName().equals(username)) return snapshot;
        }
        return null;
    }

    public void handleHit(final PlayerData attacker, final PlayerData victim) {
        final MatchStats attackerStats = this.getStats(attacker.getPlayer().getName());
        final MatchStats victimStats = this.getStats(victim.getPlayer().getName());
        victim.getPlayerMatch().setLastDamage(attacker);
        if (attackerStats == null || victimStats == null) return;

        attackerStats.hit();
        victimStats.receiveHit();
    }

    public void handleDie(final PlayerData victim) {
        this.dies.add(victim);

        final MatchStats victimSnapshot = this.getStats(victim.getPlayer().getName());
        if (victimSnapshot != null) {
            victimSnapshot.finish(victim.getPlayer());
        }
        final Player player = victim.getPlayer();
        player.getInventory().clear();
        player.updateInventory();
        PlayerUtils.animateDeath(player);

        if (!isWin()) {
            if (victimSnapshot != null) {
                final Location location = player.getLocation();
                if (victimSnapshot.getContents() != null) {
                    for (ItemStack item : victimSnapshot.getContents()) {
                        this.addEntity(location.getWorld().dropItem(location, item));
                    }
                }
                if (victimSnapshot.getArmor() != null) {
                    for (ItemStack item : victimSnapshot.getArmor()) {
                        this.addEntity(location.getWorld().dropItem(location, item));
                    }
                }
            }
        }
    }

    public void handlePotionSplash(final PlayerData playerData, final double heal) {
        final Player player = playerData.getPlayer();
        final MatchStats stats = this.getStats(player.getName());
        stats.addPotionsAccuracy(heal);
        stats.addWastedLife(heal - player.getMaxHealth() + player.getHealth());
        stats.addPotionsReceived();
    }
    public void handlePotionThrown(final PlayerData playerData) {
        final MatchStats stats = this.getStats(playerData.getPlayer().getName());
        stats.addPotionsThrown();
    }

    public boolean isWin() {
        switch (this.type) {
            case FFA -> {
                final List<PlayerData> alive = this.players;
                alive.removeAll(this.dies);
                if (alive.size() == 1) {
                    end(alive, this.dies);
                    return true;
                }
                return false;
            }
            case TEAM -> {
                final List<PlayerData> firstTeamAlive = this.firstTeam;
                final List<PlayerData> secondTeamAlive = this.secondTeam;
                firstTeamAlive.removeAll(this.dies);
                secondTeamAlive.removeAll(this.dies);
                if (firstTeamAlive.size() == 0) {
                    end(secondTeamAlive, firstTeamAlive);
                    return true;
                }
                if (secondTeamAlive.size() == 0) {
                    end(firstTeamAlive, secondTeamAlive);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void end(final List<PlayerData> winners, final List<PlayerData> losers) {
        this.entities.forEach(Entity::remove);
        this.entities.clear();

        for (PlayerData playerData : this.players) {
            final PlayerMatch playerMatch = playerData.getPlayerMatch();
            if (winners.contains(playerData)) {

            } else if (losers.contains(playerData)) {

            }
        }
    }
}
