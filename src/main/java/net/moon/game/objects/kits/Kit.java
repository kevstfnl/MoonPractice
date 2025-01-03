package net.moon.game.objects.kits;

import lombok.Data;
import net.eno.Eno;
import net.eno.configurations.CombatConfig;
import net.eno.knockback.KnockbackProfile;
import net.eno.utils.builders.ItemBuilder;
import net.eno.utils.serializers.EffectsSerializer;
import net.eno.utils.serializers.InventorySerializer;
import net.moon.game.Practice;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.utils.PlayerUtils;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class Kit {
    private final String name;
    private String displayName;
    private ItemStack icon;
    private int slot;

    //private ArenaType arenaType;

    private ItemStack[] contents;
    private ItemStack[] armors;
    private Collection<PotionEffect> effects;
    private int noDamageTicks;

    private KnockbackProfile knockbackProfile;

    private boolean enabled;
    private boolean editable;

    public Kit(final String name) {
        this.name = name;
        this.displayName = "§3" + name;
        this.icon = new ItemBuilder(Material.PAPER)
                .setAmount(1)
                .setName(this.displayName)
                .build();
        this.slot = 0;
        //this.arenaType = null;
        this.contents = new ItemStack[36];
        this.armors = new ItemStack[4];
        this.effects = new ArrayList<>();
        this.noDamageTicks = 20;

        if (Practice.isUseMoon) {
            this.knockbackProfile = Eno.get().getCombatConfig().getCurrentKb();
        }

        this.enabled = false;
        this.editable = false;
    }

    public Kit(final Document document) {
        this.name = document.getString("name");
        this.displayName = document.getString("displayName");
        this.icon = new ItemBuilder(Material.valueOf(document.getString("icon")))
                .setAmount(1)
                .setName(this.displayName)
                .build();
        //this.arenaType = ArenaType.valueOf(document.getString("arenaType"));

        final Document inventory = document.get("inventory", Document.class);
        this.contents = InventorySerializer.deserializeInventory(inventory.getString("contents"));
        this.armors = InventorySerializer.deserializeInventory(inventory.getString("armors"));
        this.effects = EffectsSerializer.deserializeEffects(inventory.getString("effects"));
        this.noDamageTicks = inventory.getInteger("noDamageTicks");

        if (Practice.isUseMoon) {
            final CombatConfig config = Eno.get().getCombatConfig();
            this.knockbackProfile = config.getKbProfileByName(document.getString("knockbackProfile"));
            if (this.knockbackProfile == null) {
                this.knockbackProfile = config.getCurrentKb();
            }
        }

        this.enabled = document.getBoolean("enabled");
        this.editable = document.getBoolean("editable");
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName.replaceAll("&", "§");
    }

    public void applyKit(final PlayerData playerData) {
        final Player player = playerData.getPlayer();

        PlayerUtils.resetPlayer(player);
        applyInventoryContents(playerData);
        applyInventoryArmor(player);
        applyEffects(player);
        applyKnockbackProfile(player);
        player.updateInventory();
    }

    public void applyEffects(final Player player) {
        if (!this.effects.isEmpty()) {
            for (final PotionEffect effect : this.effects) {
                player.addPotionEffect(effect);
            }
        }
        player.setMaximumNoDamageTicks(this.noDamageTicks);
    }

    public void applyInventoryContents(final PlayerData playerData) {
        applyInventoryContents(playerData, false);
    }

    public void applyInventoryContents(final PlayerData playerData, boolean update) {
        final Player player = playerData.getPlayer();
        if (!playerData.getKits().get(this).isInventoryEmpty()) {
            player.getInventory().setContents(playerData.getKits().get(this).getContents());
        } else {
            player.getInventory().setContents(this.contents);
        }
        if (update) player.updateInventory();
    }

    public void applyInventoryArmor(final Player player) {
        applyInventoryArmor(player, false);
    }

    public void applyInventoryArmor(final Player player, boolean update) {
        player.getInventory().setArmorContents(this.armors);
        if (update) player.updateInventory();
    }

    public void applyKnockbackProfile(final Player player) {
        if (Practice.isUseMoon) {
            final CombatConfig config = Eno.get().getCombatConfig();
            if (config.getKbProfileByName(this.knockbackProfile.getName()) == null) {
                this.knockbackProfile = config.getCurrentKb();
            }
            player.setKnockbackProfile(this.knockbackProfile);
        }
    }

    public boolean containBlock() {
        for (ItemStack item : this.contents) {
            if (item.getType().isBlock()) {
                return true;
            }
        }
        return false;
    }

    public Document toDocument() {
        final Document toReturn = new Document();
        toReturn.put("name", this.name);
        toReturn.put("displayName", this.displayName);
        toReturn.put("icon", this.icon.getType().name());
        //toReturn.put("arenaType", this.arenaType.name());

        final Document inventory = new Document();
        inventory.put("contents", InventorySerializer.serializeInventory(this.contents));
        inventory.put("armors", InventorySerializer.serializeInventory(this.armors));
        inventory.put("effects", EffectsSerializer.serializeEffects(this.effects.stream().toList()));
        inventory.put("noDamageTicks", this.noDamageTicks);
        if (Practice.isUseMoon) {
            inventory.put("knockbackProfile", this.knockbackProfile.getName());
        }

        toReturn.put("inventory", inventory);
        toReturn.put("enabled", this.enabled);
        return toReturn;
    }
}
