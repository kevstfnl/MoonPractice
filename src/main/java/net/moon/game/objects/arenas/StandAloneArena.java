package net.moon.game.objects.arenas;

import lombok.Data;
import net.moon.game.Practice;
import net.moon.game.constants.PracticeLogger;
import net.moon.game.utils.commons.TaskUtils;
import net.moon.game.utils.world.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

import static net.moon.game.constants.PracticeLogger.log;
import static net.moon.game.constants.PracticeLogger.silent;

@Data
public class StandAloneArena {

    private boolean ready;
    private final Arena arena;
    private final int id;

    private boolean used;
    private final List<Block> placedBlock;

    private Cuboid border;
    private Location firstSpawn, secondSpawn;
    private Location center;
    private List<Location> bridgeGoal1, bridgeGoal2;
    private List<Block> bed1, bed2;

    public StandAloneArena(final Arena arena, final int id) {
        this.used = false;
        this.arena = arena;
        this.id = id;
        this.placedBlock = new ArrayList<>();
        this.center = new Location(this.arena.getWorld(), this.id * 500, 50, id * 500);
        this.ready = generate();
        if (!this.ready) {
            final String log = "Copy of " + arena.getName() + "(id:" + arena.getId() + ")" + " number: " + id + "has problem with location setup !";
            log(log);
            silent(PracticeLogger.LogLevel.INFO, log);
        }
    }

    private boolean generate() {
        final List<Block> skulls = new ArrayList<>();
        final List<Block> cauldrons = new ArrayList<>();

        final List<Block> lava = new ArrayList<>();
        final List<Block> water = new ArrayList<>();

        //final List<Block> rails = new ArrayList<>();
        //final List<Block> poweredRails = new ArrayList<>();

        for (Block block : this.arena.getStructure().paste(this.center)) {
            switch (block.getType()) {
                case SKULL -> skulls.add(block);
                case CAULDRON -> cauldrons.add(block);
                case LAVA -> lava.add(block);
                case WATER -> water.add(block);
                //case RAILS -> rails.add(block);
                //case POWERED_RAIL -> poweredRails.add(block);
            }
        }

        if (skulls.size() != 2 || cauldrons.size() != 2) return false;
        this.firstSpawn = skulls.get(0).getLocation();
        this.secondSpawn = skulls.get(1).getLocation();
        this.border = new Cuboid(cauldrons.get(0).getLocation(), cauldrons.get(1).getLocation());
        TaskUtils.run(() -> {
            for (Block block : skulls) block.setType(Material.AIR);
            for (Block block : cauldrons) block.setType(Material.BARRIER);
        });

        switch (this.arena.getType()) {
            case BRIDGE -> {
                if (!(lava.size() > 0) && !(water.size() > 0)) return false;
                TaskUtils.run(() -> {
                    for (Block block : lava) block.setType(Material.ENDER_PORTAL_FRAME);
                    for (Block block : water) block.setType(Material.ENDER_PORTAL_FRAME);
                });
            }
            case SPLEEF, TNTRUN -> TaskUtils.run(() -> {
                for (Block block : lava) block.setType(Material.AIR);
            });
            case BEDWARS -> {

            }
        }
        return true;
    }

    private void place(final Block block) {
        this.placedBlock.add(block);
    }

    private void clearBlocks() {
        ArenasManager.get().execute(() -> {
            for (Block block : this.placedBlock) {
                this.placedBlock.remove(block);
                TaskUtils.run(() -> block.setType(Material.AIR, false));
            }
            try {
                Thread.sleep(5L);
            } catch (InterruptedException ignored) {}
        });
    }
}
