package net.moon.game.objects.practice.lobby;

import lombok.Data;
import net.eno.utils.serializers.LocationSerializer;
import net.eno.utils.world.Cuboid;
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
        this.border = new Cuboid(
                LocationSerializer.deserialize(document.getString("border-max")),
                LocationSerializer.deserialize(document.getString("border-min"))
        );
        this.radius = document.getInteger("radius");
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("spawn", LocationSerializer.serialize(this.spawn));
        toReturn.put("border-max", LocationSerializer.serialize(this.border.getMax()));
        toReturn.put("border-min", LocationSerializer.serialize(this.border.getMin()));
        toReturn.put("radius", this.radius);
        return toReturn;
    }
}
