package net.moon.game.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

public class WorldUtils {

    public static void deleteWorld(final String name) {
        final World world = Bukkit.getWorld(name);
        if (world != null) {
            Bukkit.getServer().unloadWorld(world, false);
            deleteFile(world.getWorldFolder());
        }
    }

    private static void deleteFile(final File path) {
        if (path.exists()) {
            path.delete();
        }
    }
}
