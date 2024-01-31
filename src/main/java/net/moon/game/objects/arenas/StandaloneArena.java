package net.moon.game.objects.arenas;

import lombok.Getter;
import lombok.Setter;
import net.eno.utils.world.Cuboid;
import net.moon.game.Practice;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class StandaloneArena {

    private final int copyId;
    private final Arena arena;
    private final Cuboid borders;

    @Setter private boolean used;

    public StandaloneArena(final int copyNumber, final Arena arena) {
        this.copyId = copyNumber;
        this.arena = arena;

        final World world = Practice.get().getArenasManager().getWorld();

        this.borders = new Cuboid(
                new Location(
                        world,
                        arena.getBorders().getMax().getX() + this.arena.getId(),
                        arena.getBorders().getMax().getY(),
                        arena.getBorders().getMax().getZ()+ this.copyId
                ),
                new Location(
                        world,
                        arena.getBorders().getMin().getX() + this.arena.getId(),
                        arena.getBorders().getMin().getY(),
                        arena.getBorders().getMin().getZ()+ this.copyId
                )
        );

        this.used = this.copyId == 0;

        arena.getStructure().paste(new Location(
                world,
                arena.getArea().getCenter().getX() + this.arena.getId(),
                arena.getArea().getCenter().getY(),
                arena.getArea().getCenter().getZ() + this.copyId
        ));

    }

    public Location getSpawn(final int index) {
        return this.arena.getSpawns().get(index).add(this.arena.getId(), 0, this.copyId);
    }
    public Location getCenter() {
        return this.arena.getBorders().getCenter().add(this.arena.getId(), 0, this.copyId);
    }

}
