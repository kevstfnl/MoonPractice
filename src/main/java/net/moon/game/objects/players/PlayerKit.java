package net.moon.game.objects.players;

import lombok.Data;
import net.moon.api.commons.serializers.InventorySerializer;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

@Data
public class PlayerKit {
    private int elo;
    private int win;
    private int winStreak;
    private int bestWinStreak;
    private int lose;

    private ItemStack[] contents;

    public PlayerKit() {
        this.elo = 1000;
        this.win = 0;
        this.lose = 0;
        this.contents = new ItemStack[36];
    }

    public PlayerKit(final Document document) {
        this.elo = document.getInteger("elo");
        this.win = document.getInteger("win");
        this.winStreak = document.getInteger("winStreak");
        this.bestWinStreak = document.getInteger("bestWinStreak");
        this.lose = document.getInteger("lose");
        final String contents = document.getString("contents");
        if (contents != null && !contents.isEmpty()) {
            this.contents = InventorySerializer.deserializeInventory(contents);
        }
    }

    public boolean isInventoryEmpty() {
        for (ItemStack item : this.contents) {
            if (item != null) return true;
        }
        return false;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("elo", this.elo);
        toReturn.put("win", this.win);
        toReturn.put("winStreak", this.winStreak);
        toReturn.put("bestWinStreak", this.bestWinStreak);
        toReturn.put("lose", this.lose);
        if (!isInventoryEmpty()) {
            toReturn.put("contents", InventorySerializer.serializeInventory(this.contents));
        }
        return toReturn;
    }
}
