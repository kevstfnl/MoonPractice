package net.moon.game.listeners.constants;

import lombok.Getter;

@Getter
public enum AnsiColor {
    RESET("\u001B[0m"),
    BLACK("\u001B[0m"),
    RED("\u001B[0m"),
    GREEN("\u001B[0m"),
    YELLOW("\u001B[0m"),
    BLUE("\u001B[0m"),
    PURPLE("\u001B[0m"),
    CYAN("\u001B[0m"),
    WHITE("\u001B[0m");

    private final String color;
    AnsiColor(final String color) {
        this.color = color;
    }


}
