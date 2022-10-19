package net.moon.game.utils.serializer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class InventorySerializer {

    public static ItemStack[] fixInventoryOrder(final ItemStack[] source) {
        final ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static String serializeInventory(final ItemStack[] source) {
        final StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : source) {
            builder.append(serializeItemStack(itemStack));
            builder.append(";");
        }

        return builder.toString();
    }

    public static ItemStack[] deserializeInventory(final String source) {
        final List<ItemStack> items = new ArrayList<>();
        final String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeItemStack(piece));
        }

        return items.toArray(new ItemStack[items.size()]);
    }

    public static String serializeEffects(final List<PotionEffect> source) {
        final StringBuilder builder = new StringBuilder();
        if (source.size() == 0) return null;

        for (PotionEffect potionEffect : source) {
            final String potionString = serializeEffect(potionEffect);
            if (potionString.equals("null")) continue;

            builder.append(potionString);
            builder.append(";");
        }

        return builder.toString();
    }

    public static List<PotionEffect> deserializeEffects(final String source) {
        final List<PotionEffect> items = new ArrayList<>();

        if (source.equalsIgnoreCase(""))
            return null;

        final String[] split = source.split(";");

        for (String piece : split) {
            items.add(deserializeEffect(piece));
        }

        return items;
    }

    @SuppressWarnings("deprecation")
    public static String serializeItemStack(final ItemStack item) {
        final StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        final String isType = isPotion(item) ? "373" : String.valueOf(item.getType().getId());
        builder.append("t@").append(isType);

        if (item.getDurability() != 0) {
            String isDurability = String.valueOf(item.getDurability());
            builder.append(":d@").append(isDurability);
        }

        if (item.getAmount() != 1) {
            String isAmount = String.valueOf(item.getAmount());
            builder.append(":a@").append(isAmount);
        }

        final Map<Enchantment, Integer> enchantments = item.getEnchantments();

        if (enchantments.size() > 0) {
            for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                builder.append(":e@").append(enchantment.getKey().getId()).append("@").append(enchantment.getValue());
            }
        }

        if (isPotion(item)) {
            try {
                final PotionMeta potMeta = (PotionMeta) item.getItemMeta();
                final PotionData potData = potMeta.getBasePotionData();

                final String id = getPotionType(item) + potData.getType().getEffectType().getName() + (potData.isExtended() ? "_UP" : (potData.isUpgraded() ? "_2" : "_1"));

                final String isDurability = String.valueOf(PotionID.valueOf(id).itemId);
                builder.append(":d@").append(isDurability);
            } catch (EnumConstantNotPresentException | NullPointerException ignored) {}
        }


        if (item.hasItemMeta()) {
            final ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                builder.append(":dn@").append(itemMeta.getDisplayName());
            }

            if (itemMeta.hasLore()) {
                builder.append(":l@").append(itemMeta.getLore());
            }
        }
        return builder.toString();
    }

    @SuppressWarnings("deprecation")
    public static ItemStack deserializeItemStack(final String in) {
        ItemStack item = null;
        ItemMeta meta = null;

        if (in.equals("null")) {
            return new ItemStack(Material.AIR);
        }

        final String[] split = in.split(":");

        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String attributeId = itemAttribute[0];

            switch (attributeId) {
                case "t" -> {
                    item = new ItemStack(Material.getMaterial(Integer.parseInt(itemAttribute[1])));
                    meta = item.getItemMeta();
                }
                case "d" -> {
                    if (item != null) {
                        item.setDurability(Short.parseShort(itemAttribute[1]));
                    }
                }
                case "a" -> {
                    if (item != null) {
                        item.setAmount(Integer.parseInt(itemAttribute[1]));
                    }
                }
                case "e" -> {
                    if (item != null) {
                        item.addUnsafeEnchantment(
                                Enchantment.getById(Integer.parseInt(itemAttribute[1])),
                                Integer.parseInt(itemAttribute[2])
                        );
                    }
                }
                case "dn" -> {
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                    }
                }
                case "l" -> {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    final List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);
                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }
                                lore.set(x, s);
                            }
                        }
                    }

                    if (meta != null) {
                        meta.setLore(lore);
                    }
                }
            }
        }

        if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
        }

        return item;
    }

    public static String serializeEffect(final PotionEffect item) {
        if (item == null) {
            return "null";
        }

        return "t@" + item.getType().getName() +
                ":d@" + item.getDuration() +
                ":a@" + item.getAmplifier();
    }

    public static PotionEffect deserializeEffect(final String in) {
        PotionEffectType type = null;
        int duration = 0;
        int amplifier = 0;

        String[] split = in.split(":");

        for (String itemInfo : split) {
            String[] itemAttribute = itemInfo.split("@");
            String attributeId = itemAttribute[0];

            switch (attributeId) {
                case "t" -> type = PotionEffectType.getByName(itemAttribute[1]);
                case "d" -> duration = Integer.parseInt(itemAttribute[1]);
                case "a" -> amplifier = Integer.parseInt(itemAttribute[1]);
            }
        }
        return new PotionEffect(type, duration, amplifier);
    }

    public static void removeCrafting(final JavaPlugin plugin, final Material material) {
        final Iterator<Recipe> iterator = plugin.getServer().recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (recipe != null && recipe.getResult().getType() == material) {
                iterator.remove();
            }
        }
    }

    public static boolean isPotion(final ItemStack item) {
        return item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION) || item.getType().equals(Material.LINGERING_POTION);
    }

    private static String getPotionType(final ItemStack itemStack) {
        switch (itemStack.getType()) {
            case LINGERING_POTION:
                return "LINGERING_";
            case SPLASH_POTION:
                return "SPLASH_";
            case POTION:
                break;
        }
        return "";
    }

    private enum PotionID {
        SPLASH_HEAL_2(16421, PotionEffectType.HEAL),
        SPLASH_HEAL_1(16453, PotionEffectType.HEAL),
        SPLASH_POISON_2(16420, PotionEffectType.POISON),
        SPLASH_SLOWNESS(16426, PotionEffectType.SLOW),

        FIRE_RESISTANCE_UP(8259, PotionEffectType.FIRE_RESISTANCE),
        SPEED_2(8226, PotionEffectType.SPEED);

        public final int itemId;
        public final PotionEffectType effectType;
        PotionID(int itemId, PotionEffectType effectType) {
            this.itemId = itemId;
            this.effectType = effectType;
        }

        public static PotionEffectType getEffect(final int id) {
            for (PotionID potion : values()) {
                if (potion.itemId == id) {
                    return potion.effectType;
                }
            }
            return null;
        }
    }
}
