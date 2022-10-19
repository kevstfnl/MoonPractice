package net.moon.game.utils.builders;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemBuilder {

    private String name;
    private int amount;
    private List<String> lore = new ArrayList<>();
    private Material type;
    private short damage;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> flags = new HashSet<>();


    public ItemBuilder(final Material type) {
        this.type = type;
    }

    public ItemBuilder(final ItemStack itemStack) {
        this.type = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.damage = itemStack.getDurability();
        this.enchantments = new HashMap<>(itemStack.getEnchantments());
        this.name = itemStack.getItemMeta().getDisplayName();
        this.lore = itemStack.getItemMeta().getLore();
        this.flags = itemStack.getItemMeta().getItemFlags();
    }

    public Material getType() {
        return type;
    }

    public ItemBuilder setType(final Material type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemBuilder setLore(final List<String> lore) {
        this.lore = lore;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public ItemBuilder setAmount(final int amount) {
        this.amount = amount;
        return this;
    }

    public short getDamage() {
        return damage;
    }

    public ItemBuilder setDamage(final int n) {
        this.damage = (short) n;
        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ItemBuilder addEnchantments(final Enchantment enchantment, final int power) {
        this.enchantments.put(enchantment, power);
        return this;
    }

    public Set<ItemFlag> getFlags() {
        return flags;
    }

    public ItemBuilder addFlag(final ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(type);
        itemStack.setAmount(amount);
        itemStack.setDurability(damage);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        // Add enchantments
        for (Enchantment ench : enchantments.keySet()) {
            itemMeta.addEnchant(ench, enchantments.get(ench), true);
        }
        // Add flags
        for (ItemFlag flag : flags) {
            itemMeta.addItemFlags(flag);
        }
        // Set meta
        itemStack.setItemMeta(itemMeta);
        // Return item
        return itemStack;
    }
}
