package net.moon.game.utils.world;

import lombok.Getter;
import net.moon.game.utils.commons.TaskUtils;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Structure {

    private final String name;
    private final List<StructureBlock> structureBlocks;

    public Structure(final String name, final Cuboid cuboid) {
        this.name = name;

        final List<Block> blocks = cuboid.getBlocks();
        this.structureBlocks = new ArrayList<>();

        for (Block block : blocks) {
            if (block.getType().equals(Material.AIR)) continue;
            this.structureBlocks.add(new StructureBlock(block));
        }
    }
    public Structure(final Document document) {
        this.name = document.getString("name");
        this.structureBlocks = new ArrayList<>(document.getInteger("size"));

        final int size =  document.getInteger("size");
        final Document blocks = (Document) document.get("blocks");
        for (int i = 0 ; i < size; i++) {
            this.structureBlocks.add(new StructureBlock((Document) blocks.get(String.valueOf(i))));
        }
    }
    public Structure(final String string) {
        final Document document = Document.parse(string);
        this.name = document.getString("name");
        this.structureBlocks = new ArrayList<>(document.getInteger("size"));

        final int size =  document.getInteger("size");
        final Document blocks = (Document) document.get("blocks");
        for (int i = 0 ; i < size; i++) {
            this.structureBlocks.add(new StructureBlock((Document) blocks.get(String.valueOf(i))));
        }
    }

    public List<Block> paste(final Location location) {
        final List<Block> pastedBlocks = new ArrayList<>(this.structureBlocks.size());
        final World world = location.getWorld();
        for (StructureBlock structureBlock : this.structureBlocks) {
            final int x = location.getBlockX() - structureBlock.getPosX();
            final int z = location.getBlockZ() - structureBlock.getPosZ();
            TaskUtils.run(() -> {
                pastedBlocks.add(structureBlock.place(new Location(world, x, structureBlock.getPosZ(), z)));
            });
        }
        return pastedBlocks;
    }

    public List<StructureBlock> getBlockMaterial(final Material material) {
        final List<StructureBlock> toReturn = new ArrayList<>();
        for (StructureBlock block :  this.structureBlocks) {
            if (block.getType().equals(material)) {
                toReturn.add(block);
            }
        }
        return toReturn;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("name", this.name);
        toReturn.put("size", this.structureBlocks.size());

        final Document blocks = new Document();
        int count = 0;
        for (StructureBlock block : this.structureBlocks) {
            blocks.put(String.valueOf(count), block.toDocument());
            count++;
        }

        toReturn.put("blocks", blocks);
        return toReturn;
    }
}
