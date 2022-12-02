package net.moon.game.objects.menus.impl;

import net.moon.api.commons.builders.ItemBuilder;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.kits.KitsManager;
import net.moon.game.objects.menus.Menu;
import net.moon.game.objects.menus.MenuSize;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Arrays;

public class KitEditorMenu extends Menu {

    public KitEditorMenu() {
        super("§a§lKit Editor", MenuSize.NORMAL, false);

        inventory.clear();
        for (int n = 0; n < inventory.getSize(); n++) {
            inventory.setItem(n, new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setAmount(1)
                    .setDamage((short) 7)
                    .build());
        }
        updateItems();
    }

    public void updateItems() {
        final KitsManager kitsManager = instance.getKitsManager();
        for (Kit kit : kitsManager.getKits().values()) {
            inventory.setItem(kit.getSlot(), new ItemBuilder(kit.getIcon().getType())
                    .setDamage(kit.getIcon().getTypeId())
                    .setAmount(1)
                    .setName(kit.getDisplayName())
                    .setLore(Arrays.asList(
                            "§8» §8§m---------------------§8 «",
                            "   §7Click here to edit.",
                            "§8» §8§m---------------------§8 «"
                    ))
                    .build()
            );
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }

    @Override
    public void onDrag(InventoryDragEvent e) {

    }

}
