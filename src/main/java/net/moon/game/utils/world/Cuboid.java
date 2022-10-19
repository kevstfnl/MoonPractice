package net.moon.game.utils.world;

import lombok.Getter;
import net.moon.game.utils.serializer.LocationSerializer;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Cuboid implements Iterable<Block> {

    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;
    private final int zMin;
    private final int zMax;

    private final double xMinCentered;
    private final double xMaxCentered;
    private final double yMinCentered;
    private final double yMaxCentered;
    private final double zMinCentered;
    private final double zMaxCentered;

    private final World world;


    public Cuboid(final Location min, final Location max) {
        this.xMin = Math.min(min.getBlockX(), max.getBlockX());
        this.xMax = Math.max(min.getBlockX(), max.getBlockX());
        this.yMin = Math.min(min.getBlockY(), max.getBlockY());
        this.yMax = Math.max(min.getBlockY(), max.getBlockY());
        this.zMin = Math.min(min.getBlockZ(), max.getBlockZ());
        this.zMax = Math.max(min.getBlockZ(), max.getBlockZ());
        this.world = min.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public Cuboid(final Document document) {
        final Location min = LocationSerializer.deserialize(document.getString("min"));
        final Location max  = LocationSerializer.deserialize(document.getString("max"));

        this.xMin = Math.min(min.getBlockX(), max.getBlockX());
        this.xMax = Math.max(min.getBlockX(), max.getBlockX());
        this.yMin = Math.min(min.getBlockY(), max.getBlockY());
        this.yMax = Math.max(min.getBlockY(), max.getBlockY());
        this.zMin = Math.min(min.getBlockZ(), max.getBlockZ());
        this.zMax = Math.max(min.getBlockZ(), max.getBlockZ());
        this.world = min.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public List<Block> getBlocks() {
        final Iterator<Block> blockIterator = this.iterator();
        final List<Block> copy = new ArrayList<>();

        while (blockIterator.hasNext()) {
            copy.add(blockIterator.next());
        }

        return copy;
    }

    public Location getCenter() {
        return new Location(
                this.world,
                (this.xMax - this.xMin) / 2.0D + this.xMin,
                (this.yMax - this.yMin) / 2.0D + this.yMin,
                (this.zMax - this.zMin) / 2.0D + this.zMin
        );
    }

    public double getDistance() {
        return this.getMin().distance(this.getMax());
    }

    public double getDistanceSquared() {
        return this.getMin().distanceSquared(this.getMax());
    }

    public Location getMin() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }

    public Location getMax() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }

    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(this.world, x, y, z);
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getWidth() * this.getLength();
    }

    public int getWidth() {
        return this.yMax - this.yMin + 1;
    }

    public int getHeight() {
        return this.xMax - this.xMin + 1;
    }

    public int getLength() {
        return this.zMax - this.zMin + 1;
    }

    public boolean isIn(final Location loc) {
        return loc.getWorld() == this.world &&
                loc.getBlockX() >= this.xMin &&
                loc.getBlockX() <= this.xMax &&
                loc.getBlockY() >= this.yMin &&
                loc.getBlockY() <= this.yMax && loc
                .getBlockZ() >= this.zMin &&
                loc.getBlockZ() <= this.zMax;
    }

    public boolean isIn(final Player player) {
        return this.isIn(player.getLocation());
    }

    public boolean isInWithMarge(final Location loc, final double marge) {
        return loc.getWorld() == this.world &&
                loc.getX() >= this.xMinCentered - marge &&
                loc.getX() <= this.xMaxCentered + marge &&
                loc.getY() >= this.yMinCentered - marge &&
                loc.getY() <= this.yMaxCentered + marge &&
                loc.getZ() >= this.zMinCentered - marge &&
                loc.getZ() <= this.zMaxCentered + marge;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("min", LocationSerializer.serialize(this.getMin()));
        toReturn.put("max", LocationSerializer.serialize(this.getMax()));
        return toReturn;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(this.getWorld(),
                this.xMin,
                this.yMin,
                this.zMin,
                this.xMax,
                this.yMax,
                this.zMax);
    }

    public static class CuboidIterator implements Iterator<Block> {
        private final World world;
        private final int baseX, baseY, baseZ;
        private int x, y, z;
        private final int sizeX, sizeY, sizeZ;

        public CuboidIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.world = world;
            this.baseX = x1;
            this.baseY = y1;
            this.baseZ = z1;
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;
            this.x = this.y = this.z = 0;
        }

        @Override
        public boolean hasNext() {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        @Override
        public Block next() {
            Block block = this.world.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);

            if (++x >= this.sizeX) {
                this.x = 0;

                if (++this.y >= this.sizeY) {
                    this.y = 0;
                    ++this.z;
                }
            }

            return block;
        }

        @Override
        public void remove() {
        }
    }
}