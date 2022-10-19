package net.moon.game.utils.world;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

public class WorldUtils {

    /**
     * Delete a Bukkit world
     * @param name world's name
     */
    public static void deleteWorld(final String name) {
        final World world = Bukkit.getWorld(name); //Get Bukkit world by name.
        if (world != null) { //Check if world exist.
            Bukkit.getServer().unloadWorld(world, false); //Unload world.
            deleteFile(world.getWorldFolder()); //Delete world file.
        }
    }

    /**
     * Delete world file
     * @param path location of file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteFile(final File path) {
        if (path.exists()) { //Check if file exist.
            path.delete(); //Delete file.
        }
    }
}
