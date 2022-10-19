package net.moon.game.objects.players;

import lombok.Data;
import net.moon.game.Practice;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.parties.Party;
import net.moon.game.objects.parties.PartyState;
import net.moon.game.utils.commons.PlayerUtils;
import net.moon.game.utils.commons.TaskUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

@Data
public class PlayerData {

    private final Practice instance; //Main practice instance.

    /*
    Basic datas
     */
    private final UUID uuid;
    private final Player player;
    private PlayerState state;


    private LinkedHashMap<Kit, PlayerKit> kits;


    private final PlayerRequests requests;
    private Party party;
    private final PlayerQueue playerQueue;


    public PlayerData(final Player player) {
        this.instance = Practice.get();
        this.uuid = player.getUniqueId();
        this.player = player;

        this.kits = new LinkedHashMap<>();
        for (Kit kit : this.instance.getKitsManager().getKits().values()) {
            this.kits.put(kit, new PlayerKit());
        }
        this.requests = new PlayerRequests(this);
        this.playerQueue = new PlayerQueue(this);

        this.init();
    }

    public PlayerData(final Document document) {
        this.instance = Practice.get();
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.player = Bukkit.getPlayer(this.uuid);

        final Document kits = (Document) document.get("kits");
        if (kits != null) {
            for (String key : kits.keySet()) {
                this.kits.put(this.instance.getKitsManager().get(key), new PlayerKit((Document) kits.get(key)));
            }
        }
        this.requests = new PlayerRequests(this);
        this.playerQueue = new PlayerQueue(this);

        this.init();
    }

    public void init() {
        PlayerUtils.resetPlayer(this.player);
        this.state = PlayerState.LOBBY;
        applyHotbar();
    }

    public void applyHotbar() {
        TaskUtils.run(() -> {
            this.player.getInventory().setContents(this.instance.getPracticeManager().getHotbar().getHotbar(this));
            this.player.updateInventory();
        });
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
        return this.state.equals(PlayerState.MATCH) /*&& this.match.getState.equal(MatchState.INPROGRESS)*/
                || this.state.equals(PlayerState.PARTY) && this.party.getState().equals(PartyState.FIGHTING);
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("uuid", this.uuid.toString());

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
