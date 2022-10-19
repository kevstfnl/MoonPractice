package net.moon.game.objects.parties;

import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.objects.players.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {

    @Getter private final List<Party> party;

    public PartyManager(final Practice instance) {
        this.party = new ArrayList<>();
    }

    public void stop() {
        this.party.forEach(Party::disband);
    }

    public void create(final PlayerData playerData) {
        this.party.add(new Party(playerData));
    }

    public void delete(final Party party) {
        this.party.remove(party);
    }

}
