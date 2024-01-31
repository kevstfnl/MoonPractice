package net.moon.game.objects.arenas;

import lombok.Data;
import net.eno.utils.builders.ItemBuilder;
import net.eno.utils.serializers.InventorySerializer;
import net.eno.utils.serializers.LocationSerializer;
import net.eno.utils.world.Cuboid;
import net.eno.utils.world.Structure;
import net.moon.game.objects.kits.Kit;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class Arena {

    private final String name;
    private final int id;
    private String displayName;
    private String author;
    private ItemStack icon;
    private int slot;

    private Cuboid area;
    private Cuboid borders;
    private Structure structure;

    private boolean enabled;
    private final List<Location> spawns;
    private boolean blacklistMode;
    private final List<Kit> kits;

    public Arena(final String name, final int id) {
        this.name = name;
        this.id = id;
        this.displayName = "§3" + name;
        this.author = "SevenTime";
        this.icon = new ItemBuilder(Material.PAPER)
                .setAmount(1)
                .setName(this.displayName)
                .build();
        this.slot = 0;

        this.enabled = false;
        this.spawns = new ArrayList<>();
        this.blacklistMode = false;
        this.kits = new ArrayList<>();
    }
    public Arena(final Document document) {
        this.name = document.getString("name");
        this.id = document.getInteger("id");
        this.displayName = document.getString("displayName");
        this.author = document.getString("author");
        this.icon = InventorySerializer.itemStackFromBase64(document.getString("icon"));
        this.slot = document.getInteger("slot");
        this.enabled = document.getBoolean("enabled");

        this.spawns = new ArrayList<>();
        this.kits = new ArrayList<>();
    }

    public Document toDocument() {
        final Document toReturn = new Document();

        toReturn.put("name", this.name);
        toReturn.put("id", this.id);
        toReturn.put("displayName", this.displayName);
        toReturn.put("author", this.author);
        toReturn.put("icon", InventorySerializer.itemStackToBase64(this.icon));
        toReturn.put("slot", this.slot);
        toReturn.put("enabled", this.enabled);

        final Document locations = new Document();
        final Document area = new Document();
        area.put("min", LocationSerializer.serialize(this.area.getMin()));
        area.put("max", LocationSerializer.serialize(this.area.getMax()));

        final Document borders = new Document();
        borders.put("min", LocationSerializer.serialize(this.borders.getMin()));
        borders.put("max", LocationSerializer.serialize(this.borders.getMax()));

        final Document spawns = new Document();
        final List<String> serializedSpawns = new ArrayList<>();
        this.spawns.forEach(spawn -> serializedSpawns.add(LocationSerializer.serialize(spawn)));
        spawns.put("spawns", serializedSpawns);

        locations.put("area", area);
        locations.put("borders", borders);
        locations.put("spawns", spawns);

        toReturn.put("locations", locations);

        toReturn.put("blacklistMode", this.blacklistMode);
        final List<String> kits = new ArrayList<>();
        this.kits.forEach(kit -> kits.add(kit.getName()));
        toReturn.put("kits", kits);

        return toReturn;
    }
}
