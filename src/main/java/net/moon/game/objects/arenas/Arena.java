package net.moon.game.objects.arenas;

import lombok.Data;
import net.mooncore.api.commons.serializers.LocationSerializer;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
public class Arena {

    private final String name;
    private final ArenaType type;

    private Location firstSpawn, secondSpawn, center;
    private Location minLocation, maxLocation;

    private String displayName;
    private ItemStack icon;



    public Arena(final String name, final ArenaType type) {
        this.name = name;
        this.type = type;
        this.displayName = "§3" + name;
    }

    public Arena(final Document document) {
        this.name = document.getString("name");
        this.type = ArenaType.valueOf(document.getString("type"));
        this.displayName = document.getString("display-name");

        final Document locations = (Document) document.get("locations");
        this.firstSpawn = LocationSerializer.deserialize(locations.getString("first-spawn"));
        this.secondSpawn = LocationSerializer.deserialize(locations.getString("second-spawn"));
        this.maxLocation = LocationSerializer.deserialize(locations.getString("max"));
        this.minLocation = LocationSerializer.deserialize(locations.getString("min"));
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("name", this.name);
        toReturn.put("type", this.type.name());
        toReturn.put("display-name", this.displayName);

        final Document locations = new Document();
        locations.put("first-spawn", LocationSerializer.serialize(this.firstSpawn));
        locations.put("second-spawn", LocationSerializer.serialize(this.secondSpawn));
        locations.put("max", LocationSerializer.serialize(this.maxLocation));
        locations.put("min", LocationSerializer.serialize(this.minLocation));

        toReturn.put("locations", locations);
        return toReturn;
    }
}
