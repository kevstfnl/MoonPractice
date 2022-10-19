package net.moon.game.objects.practice.hotbar;

import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.queues.impl.ClassicQueue;
import net.moon.game.utils.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class Hotbar {

    public ItemStack[] getHotbar(final PlayerData playerData) {
        ItemStack[] toReturn = new ItemStack[9];
        Arrays.fill(toReturn, null);
        switch (playerData.getState()) {
            case OFFLINE, MATCH -> {
                return null;
            }
            case LOBBY -> {
                for (LobbyItems items : LobbyItems.values()) {
                    if (items.equals(LobbyItems.LEAVE)) continue;
                    toReturn[items.slot] = new ItemBuilder(items.type).setAmount(1).setName(items.name).build();
                }
            }
            case QUEUE -> {
                final Map<ClassicQueue, Long> queues = playerData.getPlayerQueue().getCurrentQueues();
                if (!queues.isEmpty()) {
                    final ClassicQueue queue = queues.keySet().stream().findFirst().get();
                    for (LobbyItems items : LobbyItems.values()) {
                        if (items.equals(LobbyItems.LEAVE)) continue;
                        toReturn[items.slot] = new ItemBuilder(items.type).setAmount(1).setName(items.name).build();
                    }
                    if (queue.getType().isRanked()) {
                        toReturn[LobbyItems.UNRANKED.slot] = new ItemBuilder(LobbyItems.LEAVE.type).setAmount(10).setName(LobbyItems.LEAVE.name).build();
                    } else {
                        toReturn[LobbyItems.RANKED.slot] = new ItemBuilder(LobbyItems.LEAVE.type).setAmount(10).setName(LobbyItems.LEAVE.name).build();
                    }
                }
            }
            case PARTY -> {
                for (PartyItems items : PartyItems.values()) {
                    toReturn[items.slot] = new ItemBuilder(items.type).setAmount(1).setName(items.name).build();
                }
            }
            case SPECTATE -> {
                for (SpectatorItems items : SpectatorItems.values()) {
                    toReturn[items.slot] = new ItemBuilder(items.type).setAmount(1).setName(items.name).build();
                }
            }
        }
        return toReturn;
    }

    public LobbyItems lobbyItemsFromItemStack(final ItemStack itemStack) {
        for (LobbyItems items : LobbyItems.values()) {
            if (itemStack.getType().equals(items.type) && itemStack.getI18NDisplayName().equals(items.name)) return items;
        }
        return null;
    }
    public PartyItems partyItemsFromItemStack(final ItemStack itemStack) {
        for (PartyItems items : PartyItems.values()) {
            if (itemStack.getType().equals(items.type) && itemStack.getI18NDisplayName().equals(items.name)) return items;
        }
        return null;
    }
    public SpectatorItems spectatorItemsFromItemStack(final ItemStack itemStack) {
        for (SpectatorItems items : SpectatorItems.values()) {
            if (itemStack.getType().equals(items.type) && itemStack.getI18NDisplayName().equals(items.name)) return items;
        }
        return null;
    }

    public enum LobbyItems {
        UNRANKED(Material.IRON_SWORD,"§8» §7§lUnranked", 0),
        RANKED(Material.DIAMOND_SWORD,"§8» §b§lRanked", 1),
        PARTY(Material.BLAZE_POWDER,"§8» §e§lParty", 4),
        PROFILE(Material.SKULL_ITEM,"§8» §6§lProfile", 7),
        SPECTATE(Material.COMPASS,"§8» §5§lSpectate", 8),

        LEAVE(Material.REDSTONE,"§8» §c§lLeave", -1);

        private final Material type;
        private final String name;
        private final int slot;

        LobbyItems(final Material type, final String name, final int slot) {
            this.type = type;
            this.name = name;
            this.slot = slot;
        }
    }
    public enum PartyItems {
        PARTY_INFO(Material.PAPER,"§8» §e§lInformation", 4),
        PARTY_SETTINGS(Material.REDSTONE_COMPARATOR,"§8» §3§lSettings", 5),
        UNRANKED_DUO(Material.IRON_SWORD,"§8» §7§lUnranked §8(Duo)", 1),
        RANKED_DUO(Material.DIAMOND_SWORD,"§8» §b§lRanked §8(Duo)",2),
        PARTY_EVENT(Material.DIAMOND,"§8» §2§lEvents", 0),
        PROFILE(Material.SKULL_ITEM,"§8» §6§lProfile", 7),

        LEAVE(Material.REDSTONE,"§8» §c§lLeave",8);

        private final Material type;
        private final String name;
        private final int slot;

        PartyItems(final Material type, final String name, final int slot) {
            this.type = type;
            this.name = name;
            this.slot = slot;
        }
    }
    public enum SpectatorItems {
        NAVIGATION(Material.COMPASS,"§8» §a§lNavigation", 0),

        LEAVE(Material.REDSTONE,"§8» §c§lLeave", 8);

        private final Material type;
        private final String name;
        private final int slot;

        SpectatorItems(final Material type, final String name, final int slot) {
            this.type = type;
            this.name = name;
            this.slot = slot;
        }
    }
}
