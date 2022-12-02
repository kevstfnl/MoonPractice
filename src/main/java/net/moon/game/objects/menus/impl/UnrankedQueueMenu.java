package net.moon.game.objects.menus.impl;

import net.moon.api.commons.builders.ItemBuilder;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.menus.Menu;
import net.moon.game.objects.menus.MenuSize;
import net.moon.game.objects.players.PlayerKit;
import net.moon.game.objects.queues.Queue;
import net.moon.game.objects.queues.QueueManager;
import net.moon.game.objects.queues.QueueType;
import net.moon.game.objects.queues.impl.ClassicQueue;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class UnrankedQueueMenu extends Menu {

    public UnrankedQueueMenu() {
        super("§a§lUnranked", MenuSize.NORMAL, true);
        inventory.clear();
        for (int n = 0; n < inventory.getSize(); n++) {
            inventory.setItem(n, new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setAmount(1)
                    .setDamage((short) 7)
                    .build());
            updateItems();
        }
    }

    public void updateItems() {
        final QueueManager queueManager = instance.getQueueManager();
        final List<Queue> queues =  queueManager.getByType(QueueType.CLASSIC_UNRANKED);
        if (queues != null && !queues.isEmpty()) {
            for (Queue object : queues) {
                if (object instanceof ClassicQueue queue) {
                    final Kit kit = queue.getKit();
                    final PlayerKit playerKit = getPlayerData().getKits().get(kit);

                    if (getPlayerData().getPlayerQueue().getCurrentQueues().containsKey(queue)) {
                        inventory.setItem(kit.getSlot(), new ItemBuilder(kit.getIcon().getType())
                                .setDamage(kit.getIcon().getTypeId())
                                .setAmount(Math.max(1, queue.getQueue().size()))
                                .addFlag(ItemFlag.HIDE_ENCHANTS)
                                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .addFlag(ItemFlag.HIDE_POTION_EFFECTS)
                                .setName(kit.getDisplayName())
                                .setLore(Arrays.asList(
                                        "§8» §8§m---------------------§8 «",
                                        " §3Wins: §7" + playerKit.getWin(),
                                        " §3WinStreak: §7" + playerKit.getWinStreak() + " " +
                                                "§8(§7best: " + playerKit.getBestWinStreak() + "§8)",
                                        " §3Loses: §7" + playerKit.getLose(),
                                        " ",
                                        " §3Match: §7" + "0",
                                        " §3Queue: §7" + queue.getQueue().size(),
                                        " ",
                                        "§aClick here to join this queue.",
                                        "§8» §8§m---------------------§8 «"
                                ))
                                .build()
                        );
                    } else {
                        inventory.setItem(kit.getSlot(), new ItemBuilder(Material.REDSTONE)
                                .setAmount(1)
                                .addFlag(ItemFlag.HIDE_ENCHANTS)
                                .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .addFlag(ItemFlag.HIDE_POTION_EFFECTS)
                                .setName(kit.getDisplayName())
                                .setLore(Arrays.asList(
                                        "§8» §8§m---------------------§8 «",
                                        " §3Wins: §7" + playerKit.getWin(),
                                        " §3WinStreak: §7" + playerKit.getWinStreak() + " " +
                                                "§8(§7best: " + playerKit.getBestWinStreak() + "§8)",
                                        " §3Loses: §7" + playerKit.getLose(),
                                        " ",
                                        " §3Match: §7" + "0",
                                        " §3Queue: §7" + queue.getQueue().size(),
                                        " ",
                                        "§cClick here to leave this queue.",
                                        "§8» §8§m---------------------§8 «"
                                ))
                                .build()
                        );
                    }
                }
            }
        }
        getPlayerData().getPlayer().updateInventory();
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof UnrankedQueueMenu && inventory.getName().equals(getTitle())) {
            final HumanEntity who = e.getWhoClicked();
            if (who instanceof Player player) {
                final ItemStack currentItem = e.getCurrentItem();
                e.setCancelled(true);
                if (currentItem != null) {
                    final QueueManager queueManager = instance.getQueueManager();
                    final List<Queue> queues = queueManager.getByType(QueueType.CLASSIC_UNRANKED);
                    if (currentItem.getType().equals(Material.REDSTONE)) {
                        for (Queue queue : queues) {
                            if (queue.compareTo(currentItem)) {
                                getPlayerData().getPlayerQueue().remove(queue);
                            }
                        }
                    } else {
                        for (Queue queue : queues) {
                            if (queue.compareTo(currentItem)) {
                                getPlayerData().getPlayerQueue().add(queue);
                                getPlayerData().applyHotbar();
                            }
                        }
                    }
                    updateItems();
                }
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent e) {}

    @Override
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof UnrankedQueueMenu && inventory.getName().equals(getTitle())) {
            e.setCancelled(true);
        }
    }
}
