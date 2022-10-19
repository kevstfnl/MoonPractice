package net.moon.game.utils.builders;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableBuilder {
    private final TextComponent textComponent; //Final text component

    public ClickableBuilder(final String message) {
        this.textComponent = new TextComponent(message);
    }

    /**
     * Set the hover text.
     * @param hover hover text
     * @return this
     */
    @SuppressWarnings("deprecation")
    public ClickableBuilder setHover(final String hover) {
        this.textComponent.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create())
        );
        return this;
    }

    /**
     * Set the click text action.
     * @param command to run
     * @param mode action result (RUN_COMMAND, SUGGEST_COMMAND, ...)
     * @return
     */
    public ClickableBuilder setClick(final String command, final ClickEvent.Action mode) {
        this.textComponent.setClickEvent(new ClickEvent(mode, command));
        return this;
    }

    /**
     * Build the final TextComponent.
     * @return TextComponent
     */
    public TextComponent build() {
        return this.textComponent;
    }
}
