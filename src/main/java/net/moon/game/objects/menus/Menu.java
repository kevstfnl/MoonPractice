package net.moon.game.objects.menus;

import lombok.Getter;
import lombok.Setter;
import net.moon.game.Practice;
import net.moon.game.objects.players.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public abstract class Menu implements InventoryHolder {

    public final Practice instance;
    private final String title;
    private final MenuSize size;

    @Setter private PlayerData playerData;
    private final boolean unique;

    public Inventory inventory;

    public Menu(final String title, final MenuSize size, final boolean unique) {
        this.instance = Practice.get();
        this.title = title;
        this.size = size;

        this.unique = unique;

        this.inventory = Bukkit.createInventory(this, this.size.getSize(), this.title);
    }

    public void open(final Player player) {
        player.openInventory(this.inventory);
    }

    public abstract void onClick(final InventoryClickEvent e);
    public abstract void onClose(final InventoryCloseEvent e);
    public abstract void onDrag(final InventoryDragEvent e);
}
