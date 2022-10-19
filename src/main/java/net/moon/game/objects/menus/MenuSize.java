package net.moon.game.objects.menus;

import lombok.Getter;

@Getter
public enum MenuSize {
    VERY_SMALL(9),
    SMALL(27),
    NORMAL(36),
    BIG(54);

    private final int size;
    MenuSize(final int size) {
        this.size = size;
    }
}
