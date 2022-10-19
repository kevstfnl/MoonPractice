package net.moon.game.objects.players;

import lombok.Getter;

@Getter
public enum PlayerState {
    OFFLINE(false),
    LOBBY(true),
    SPECTATE(false),
    QUEUE(true),
    MATCH(false),
    PARTY(false);

    private final boolean visible;
    PlayerState(final boolean visible) {
        this.visible = visible;
    }
}
