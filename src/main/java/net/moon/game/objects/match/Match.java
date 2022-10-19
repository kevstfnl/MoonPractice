package net.moon.game.objects.match;

import lombok.Data;
import net.moon.game.Practice;
import net.moon.game.objects.arenas.Arena;
import net.moon.game.objects.arenas.ArenasManager;
import net.moon.game.objects.arenas.StandAloneArena;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.parties.Party;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayerState;
import net.moon.game.tasks.MatchTask;
import net.moon.game.utils.commons.PlayerUtils;
import net.moon.game.utils.commons.TaskUtils;
import org.bukkit.entity.Player;

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
    private final Arena arena;
    private StandAloneArena standAloneArena;

    private final List<PlayerData> players, firstTeam, secondTeam, spectators, dies;
    private final Map<Integer, MatchSnapshot> snapshots;

    private long pending, start, end;


    public Match(final MatchType type, final Kit kit) {
        this.arenasManager = Practice.get().getArenasManager();
        this.id = this.hashCode();

        this.state = MatchState.CREATING;

        this.task = new MatchTask(this);
        this.type = type;
        this.kit = kit;
        this.arena = this.arenasManager.getRandomWithType(this.kit.getArenaType());
        this.standAloneArena = this.arena.getAvailable(this.kit.containBlock());
        if (this.standAloneArena == null) {
            this.arena.generate(5);
        }

        this.players = new ArrayList<>();
        this.firstTeam = new ArrayList<>();
        this.secondTeam = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.dies = new ArrayList<>();
        this.snapshots = new ConcurrentHashMap<>();

        TaskUtils.run(this.task);
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
    public void removePlayer(final PlayerData playerData) {
        this.players.remove(playerData);
    }
    public void addSpectator(final PlayerData playerData) {
        this.spectators.add(playerData);
    }
    public void removeSpectator(final PlayerData playerData) {
        this.spectators.remove(playerData);
    }

    public void apply() {
        if (this.players.isEmpty()) {
            this.players.addAll(this.firstTeam);
            this.players.addAll(this.secondTeam);
        }
        if (this.firstTeam.isEmpty() || this.secondTeam.isEmpty()) {
            splitPlayers();
        }
        createMatchStats();
        applyPlayer();
        this.pending = System.currentTimeMillis();
    }
    private void splitPlayers() {
        Collections.shuffle(this.players);
        this.firstTeam.addAll(this.players.subList(0, this.players.size() / 2 + this.players.size() % 2));
        this.secondTeam.addAll(this.players.subList(this.players.size() / 2 + this.players.size() % 2, this.players.size()));
    }
    private void createMatchStats() {
        int i = 0;
        for (PlayerData playerData : this.players) {
            this.snapshots.put(i, new MatchSnapshot(playerData));
            i++;
        }
    }
    public MatchSnapshot getSnapshot(final String username) {
        for (MatchSnapshot snapshot : this.snapshots.values()) {
            if (snapshot.getPlayerName().equals(username)) return snapshot;
        }
        return null;
    }
    private void applyPlayer() {
        this.players.forEach(playerData -> {

            final Player player = playerData.getPlayer();
            playerData.setState(PlayerState.MATCH);
            PlayerUtils.resetPlayer(player);

            if (this.type == MatchType.FFA) {
                final Random random = new Random();
                player.teleport(this.standAloneArena.getCenter().add(random.nextInt() * 0.25D, 0, random.nextInt() * 0.25D));
            } else {
                if (this.firstTeam.contains(playerData)) {
                    player.teleport(this.standAloneArena.getFirstSpawn());
                } else if (this.secondTeam.contains(playerData)) {
                    player.teleport(this.standAloneArena.getSecondSpawn());
                }
            }

            this.kit.applyKit(playerData);
        });
    }

    public void handleHit(final PlayerData attacker, final PlayerData victim) {
        final MatchSnapshot attackerSnapshot = this.getSnapshot(attacker.getPlayer().getName());
        final MatchSnapshot victimSnapshot = this.getSnapshot(victim.getPlayer().getName());

        if (attackerSnapshot == null || victimSnapshot == null) return;

        attackerSnapshot.hit();
        victimSnapshot.receiveHit();
    }
    public void handleDie(final PlayerData attacker, final PlayerData victim) {
        this.dies.add(victim);
        this.firstTeam.remove(victim);
        this.secondTeam.remove(victim);
        this.players.remove(victim);

        final MatchSnapshot victimSnapshot = this.getSnapshot(victim.getPlayer().getName());
        if (victimSnapshot != null) {
            victimSnapshot.finish(victim.getPlayer());
        }
        checkWin();
    }

    public void checkWin() {

    }
}
