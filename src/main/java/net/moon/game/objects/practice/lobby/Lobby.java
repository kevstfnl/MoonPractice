package net.moon.game.objects.practice.lobby;

import lombok.Data;
import net.moon.game.utils.serializer.LocationSerializer;
import net.moon.game.utils.world.Cuboid;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
public class Lobby {

    private Location spawn;
    private Cuboid border;
    private int radius;

    public Lobby() {
        final Location spawn = new Location(Bukkit.getWorld("world"), 0, 50, 0);
        this.spawn = spawn;
        this.border = new Cuboid(spawn.subtract(10,10,10), spawn.add(10,10,10));
        this.radius = 20;
    }

    public Lobby(final Document document) {
        this.spawn = LocationSerializer.deserialize(document.getString("spawn"));
        this.border = new Cuboid((Document) document.get("border"));
        this.radius = document.getInteger("radius");
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("spawn", LocationSerializer.serialize(this.spawn));
        toReturn.put("border", this.border.toDocument());
        toReturn.put("radius", this.radius);
        return toReturn;
    }
}
