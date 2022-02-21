package net.moon.game.objects.arenas;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class StandAloneArena {

    private final Arena arena;
    private double distanceX, distanceZ;
    private final List<Block> blocks;


    public StandAloneArena(final Arena arena) {
        this.arena = arena;
        this.blocks = new ArrayList<>();
    }
}
