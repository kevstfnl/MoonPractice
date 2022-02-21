package net.moon.practice.objects.arenas;

import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import javax.xml.stream.Location;

public class Arena {

    private String name;
    private ArenaType type;

    private Location firstSpawn, secondSpawn, center;
    private Location minLocation, maxLocation;

    private String displayName;
    private ItemStack icon;


    public Arena() {
    }

    public Arena(final Document document) {
    }
}
