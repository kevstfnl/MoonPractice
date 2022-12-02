package net.moon.game.objects.arenas;

import lombok.Data;
import net.moon.api.commons.builders.ItemBuilder;
import net.moon.api.commons.serializers.InventorySerializer;
import net.moon.api.commons.serializers.LocationSerializer;
import net.moon.api.commons.world.Cuboid;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
public class Arena {

    private final String name;
    private String displayName;
    private ItemStack icon;

    private Location firstSpawn;
    private Location secondSpawn;
    private Location center;
    private Cuboid border;
    private Cuboid area;

    public Arena(final String name) {
        this.name = name;
        this.displayName = "§3" + name;
        this.icon = new ItemBuilder(Material.PAPER)
                .setAmount(1)
                .setName(this.displayName)
                .build();
    }
    public Arena(final Document document) {
        this.name = document.getString("name");
        this.displayName = document.getString("displayName");
        this.icon = new ItemBuilder(Material.valueOf(document.getString("icon-type")))
                .setDamage(document.getInteger("icon-damage"))
                .setName(this.displayName)
                .setAmount(1)
                .build();
        this.firstSpawn = LocationSerializer.deserialize(document.getString("firstSpawn"));
        this.secondSpawn = LocationSerializer.deserialize(document.getString("secondSpawn"));

    }
}
