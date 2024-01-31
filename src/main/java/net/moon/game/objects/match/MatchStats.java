package net.moon.game.objects.match;

import lombok.Data;
import lombok.Getter;
import net.moon.game.objects.players.PlayerData;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
public class MatchStats {

    private final String playerName;
    private final UUID uuid;

    private int hits;
    private int combos;
    private int comboHit;
    private int longestComboHit;

    private int hitsReceived;
    private int comboReceived;
    private int comboHitReceived;
    private int longestComboHitReceived;

    private ItemStack[] armor;
    private ItemStack[] contents;
    private Collection<PotionEffect> effects;
    private double health;
    private int hunger;

    private int potionsThrown;
    private int potionsReceived;
    private double potionsAccuracy = 8.0;
    private double lifeWasted = 0.0;

    public MatchStats(final PlayerData playerData) {
        this.playerName = playerData.getPlayer().getName();
        this.uuid = playerData.getUuid();

        this.hits = 0;
        this.combos = 0;
        this.comboHit = 0;
        this.longestComboHit = 0;

        this.hitsReceived = 0;
        this.comboReceived = 0;
        this.comboHitReceived = 0;
        this.longestComboHitReceived = 0;

        this.armor = new ItemStack[4];
        this.contents = new ItemStack[36];
        this.effects = new ArrayList<>();
        this.health = 20.0D;
        this.hunger = 20;
    }

    public MatchStats(final Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.playerName = document.getString("name");
    }

    public void hit() {
        this.hits++;
        this.comboHit++;
        if (this.comboHit == 3) {
            this.combos++;
        }
        if (this.comboHit >= 2 && this.comboHit > this.longestComboHit) {
            this.longestComboHit = this.comboHit;
        }
    }
    public void receiveHit() {
        this.hitsReceived++;
        this.comboHitReceived++;
        this.comboHit = 0;

        if (this.comboHitReceived == 3) {
            this.comboReceived++;
        }
        if (this.comboHitReceived >= 2 && this.comboHitReceived > this.longestComboHitReceived) {
            this.longestComboHitReceived = this.comboHitReceived;
        }
    }

    public void addPotionsAccuracy(double heal) { this.potionsAccuracy += heal; }
    public void addPotionsThrown() { this.potionsThrown++; }
    public void addPotionsReceived() { this.potionsReceived++; }
    public void addWastedLife(double life) { if (life > 0) this.lifeWasted += life; }
    public double getPotionsAccuracy() { return (this.potionsAccuracy  / 8.0 * 100.0) / (this.potionsThrown + 1); }

    public void finish(final Player player) {
        this.armor = player.getInventory().getArmorContents().clone();
        this.contents = player.getInventory().getContents().clone();
        this.effects = player.getActivePotionEffects();
        this.health = player.getHealth();
        this.hunger = player.getFoodLevel();
    }

    public Document toDocument() {
        return new Document();
    }

}
