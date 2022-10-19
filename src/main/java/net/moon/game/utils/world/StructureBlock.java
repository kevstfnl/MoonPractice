package net.moon.game.utils.world;

import lombok.Data;
import net.moon.game.utils.commons.TaskUtils;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Data
public class StructureBlock {

    private final Material type; //Block type.
    private final byte data; //Block data.
    private final int posX, posY, posZ; //Block location

    @SuppressWarnings("deprecation")
    public StructureBlock(final Block block) {
        this.type = block.getType();
        this.data = block.getData();
        this.posX = block.getX();
        this.posY = block.getY();
        this.posZ = block.getZ();
    }

    public StructureBlock(final Document document) {
        this.type = Material.valueOf(document.getString("type"));
        this.data = (byte)document.get("data");
        this.posX = document.getInteger("posX");
        this.posY = document.getInteger("posY");
        this.posZ = document.getInteger("posZ");
    }

    @SuppressWarnings("deprecation")
    public Block place(final Location location) {
        final Block toPlace = location.getBlock();
        toPlace.setTypeIdAndData(this.type.getId(), this.data, false);
        return toPlace;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("type", this.type.name());
        toReturn.put("data", this.data);
        toReturn.put("posX", this.posX);
        toReturn.put("posY", this.posY);
        toReturn.put("posZ", this.posZ);
        return toReturn;
    }
}
