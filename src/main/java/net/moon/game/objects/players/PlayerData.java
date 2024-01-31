package net.moon.game.objects.players;

import lombok.Data;
import net.moon.game.Practice;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.match.MatchState;
import net.moon.game.objects.parties.Party;
import net.moon.game.objects.parties.PartyState;
import net.moon.game.utils.PlayerUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

@Data
public class PlayerData {

    protected final Practice instance; //Main practice instance.

    /*
    Basic datas
     */
    private final UUID uuid;
    private final Player player;
    private PlayerState state;

    private LinkedHashMap<Kit, PlayerKit> kits;

    private final PlayerSettings settings;
    private final PlayerRequests requests;
    private Party party;
    private final PlayerQueue playerQueue;
    private final PlayerMatch playerMatch;


    public PlayerData(final Player player) {
        this.instance = Practice.get();
        this.uuid = player.getUniqueId();
        this.player = player;

        this.kits = new LinkedHashMap<>();
        for (Kit kit : this.instance.getKitsManager().getKits().values()) {
            this.kits.put(kit, new PlayerKit());
        }
        this.settings = new PlayerSettings();
        this.requests = new PlayerRequests(this);
        this.playerQueue = new PlayerQueue(this);
        this.playerMatch = new PlayerMatch(this);

        this.init();
    }

    public PlayerData(final Document document) {
        this.instance = Practice.get();
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.player = Bukkit.getPlayer(this.uuid);

        final Document kits = document.get("kits", Document.class);
        if (kits != null) {
            for (String key : kits.keySet()) {
                this.kits.put(this.instance.getKitsManager().get(key), new PlayerKit((Document) kits.get(key)));
            }
        }
        this.settings = new PlayerSettings(document.get("settings", Document.class));
        this.requests = new PlayerRequests(this);
        this.playerQueue = new PlayerQueue(this);
        this.playerMatch = new PlayerMatch(this);

        this.init();
    }

    public void init() {
        this.instance.runSync(() -> {
            PlayerUtils.resetPlayer(this.player);
            this.state = PlayerState.LOBBY;
            applyHotbar();
        });
    }

    public void applyHotbar() {
        this.player.getInventory().setContents(this.instance.getPracticeManager().getHotbar().getHotbar(this));
        this.player.updateInventory();
    }

    public boolean inLobby() {
        return this.state.equals(PlayerState.LOBBY);
    }
    public boolean inMatch() {
        return this.state.equals(PlayerState.MATCH);
    }
    public boolean inQueue() {
        return this.state.equals(PlayerState.QUEUE);
    }
    public boolean inSpectate() {
        return this.state.equals(PlayerState.SPECTATE);
    }
    public boolean inParty() {
        return this.state.equals(PlayerState.PARTY);
    }
    public boolean inPvp() {
        return this.state.equals(PlayerState.MATCH) &&
                this.playerMatch.getMatch().getState().equals(MatchState.INPROGRESS) &&
                !this.playerMatch.getMatch().getDies().contains(this) ||
                this.state.equals(PlayerState.PARTY) &&
                this.party.getState().equals(PartyState.FIGHTING) /*&& !this.party.getMatch().contains(this)*/;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("uuid", this.uuid.toString());
        toReturn.put("settings", this.settings.toDocument());
        final Document kits = new Document();
        for (Kit kit : this.instance.getKitsManager().getKits().values()) {
            PlayerKit pk = this.kits.get(kit);
            if (pk == null) pk = new PlayerKit();
            kits.put(kit.getName(), pk);
        }
        toReturn.put("kits", kits);


        return toReturn;
    }
}
