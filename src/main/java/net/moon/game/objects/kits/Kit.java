package net.moon.game.objects.kits;

import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

@Data
public class Kit {
    private final String name;
    private String displayName;
    private int slot;
    private boolean enabled;
    private ItemStack icon;

    public Kit(final String name) {
        this.name = name;
        this.displayName = "§3" + name;
    }

    public Kit(final Document document) {
        this.name = document.getString("name");
        this.displayName = document.getString("display-name");
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("name", this.name);
        toReturn.put("display-name", this.displayName);
        return toReturn;
    }
}
