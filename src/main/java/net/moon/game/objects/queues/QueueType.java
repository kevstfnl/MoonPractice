package net.moon.game.objects.queues;

import lombok.Getter;

@Getter
public enum QueueType {
    CLASSIC_UNRANKED(false, false),
    CLASSIC_RANKED(true, false),

    PARTY_UNRANKED(false, true),
    PARTY_RANKED(true, true);

    private final boolean ranked, party;

    QueueType(final boolean ranked, final boolean party) {
        this.ranked = ranked;
        this.party = party;
    }
}
