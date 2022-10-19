package net.moon.game.utils.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generate a completely void Bukkit world.
 */
public class VoidGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(final World world,
                                       final Random random,
                                       final int chunkX,
                                       final int chunkZ,
                                       final BiomeGrid biome) {
        final ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biome.setBiome(x, z, Biome.PLAINS);
            }
        }
        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        return Collections.emptyList();
    }

    @Override
    public boolean canSpawn(final World world, final int x, final int z) {
        return true;
    }

    @Override
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0 ,100, 0);
    }
}
