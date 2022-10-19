package net.moon.game.objects.players;

import net.moon.game.objects.parties.Party;
import net.moon.game.objects.parties.PartyManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRequests {

    private final PlayerData playerData;

    private final PartyManager partyManager;
    private final Map<Party, Long> partyInvitations;

    public PlayerRequests(final PlayerData playerData) {
        this.playerData = playerData;

        this.partyManager = playerData.getInstance().getPartyManager();
        this.partyInvitations = new ConcurrentHashMap<>();
    }

    public void addPartyInvitation(final Party party)  {
        this.partyInvitations.put(party, System.currentTimeMillis());
    }
    public void removePartyInvitation(final Party party) {
        this.partyInvitations.remove(party);
    }

    public void updateInvitationsTimestamp() {
        final long now = System.currentTimeMillis();
        for (Map.Entry<Party, Long> entry : this.partyInvitations.entrySet()) {
            final Party party = entry.getKey();
            final long date = entry.getValue();
            if (!this.partyManager.getParty().contains(party)) {
                this.partyInvitations.remove(party);
                return;
            }
            if (now - date > 60000) {
                this.partyInvitations.remove(party);
                party.removeInvite(this.playerData);
            }
        }
    }
}
