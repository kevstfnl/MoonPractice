package net.moon.game.objects.arenas;

import lombok.Data;
import net.moon.game.utils.builders.ItemBuilder;
import net.moon.game.utils.world.Structure;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Arena {

    private final World world = ArenasManager.get().getArenaWorld();
    private final String name;
    private String displayName;
    private final int id;
    private ArenaType type;
    private ItemStack icon;
    private String creator;
    private boolean enabled;

    private Structure structure;

    private List<StandAloneArena> copy;

    public Arena(final String name, final int id) {
        this.name = name;
        this.displayName = "§3" + name;
        this.id = id;
        this.type = ArenaType.CLASSIC;
        this.icon = new ItemBuilder(Material.PAPER)
                .setAmount(1)
                .setName(this.displayName)
                .build();
        this.creator = "SevenTime";
        this.enabled = false;
    }
    public Arena(final Document document) {
        this.name = document.getString("name");
        this.displayName = document.getString("displayName");
        this.id = document.getInteger("id");
        this.type = ArenaType.valueOf(document.getString("type"));
        this.icon = new ItemBuilder(Material.valueOf(document.getString("icon")))
                .setAmount(1)
                .setName(this.displayName)
                .build();
        this.enabled = document.getBoolean("enabled");
        this.structure = new Structure((Document) document.get("structure"));
    }

    public void generate(final int amount) {
        for (int i = 0; i < amount; i++) {
            generate();
        }
    }
    public void generate() {
        if (this.structure != null) {
            ArenasManager.get().execute(() -> {
                final int id = this.copy.size();
                final StandAloneArena standAloneArena = new StandAloneArena(this, id);
                this.copy.add(standAloneArena);
            });
        }
    }

    public StandAloneArena getAvailable() {
        return getAvailable(true);
    }

    public StandAloneArena getAvailable(boolean withBlock) {
        if (!withBlock) return this.copy.get(0);
        for (StandAloneArena standAloneArena : this.copy) {
            if (!standAloneArena.isUsed() && standAloneArena.isReady() && this.copy.indexOf(standAloneArena) != 0) {
                return standAloneArena;
            }
        }
        return null;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("name", this.name);
        toReturn.put("displayName", this.displayName);
        toReturn.put("id", this.id);
        toReturn.put("type", this.type);
        toReturn.put("icon", this.icon.getType().name());
        toReturn.put("creator", this.creator);
        toReturn.put("enabled", this.enabled);
        toReturn.put("structure", this.structure.toDocument());
        return toReturn;
    }
}
