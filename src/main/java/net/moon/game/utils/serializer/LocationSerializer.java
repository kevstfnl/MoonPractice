package net.moon.game.utils.serializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer {

    public static Location[] getFaces(final Location start) {
        return new Location[]{
                new Location(start.getWorld(), start.getX() + 1.0D, start.getY(), start.getZ()),
                new Location(start.getWorld(), start.getX() - 1.0D, start.getY(), start.getZ()),
                new Location(start.getWorld(), start.getX(), start.getY() + 1.0D, start.getZ()),
                new Location(start.getWorld(), start.getX(), start.getY() - 1.0D, start.getZ())};
    }

    public static String serialize(final Location location) {
        return location.getWorld().getName() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getYaw() + ":" +
                location.getPitch();
    }

    public static Location deserialize(final String source) {
        String[] split = source.split(":");
        return new Location(
                Bukkit.getServer().getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5]));
    }
}
