package net.moon.game.listeners;

import net.moon.game.Practice;
import net.moon.game.objects.practice.hotbar.Hotbar;
import net.moon.game.objects.menus.Menu;
import net.moon.game.objects.menus.MenusManager;
import net.moon.game.objects.parties.PartyManager;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayerState;
import net.moon.game.objects.players.PlayersManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import static net.moon.game.listeners.constants.PracticeLogger.log;

public class InventoryListener implements Listener {

    private final PlayersManager playersManager;
    private final Hotbar hotbar;
    private final MenusManager menusManager;
    private final PartyManager partyManager;

    public InventoryListener(final Practice instance) {
        this.playersManager = instance.getPlayersManager();
        this.hotbar = instance.getPracticeManager().getHotbar();
        this.menusManager = instance.getMenusManager();
        this.partyManager = instance.getPartyManager();
    }

    @EventHandler
    private void onInteract(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (player.isOp() && player.getGameMode().equals(GameMode.CREATIVE)) return;
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData == null) {
            e.setCancelled(true);
            return;
        }
        if (e.getAction().equals(Action.PHYSICAL)) {
            e.setCancelled(!playerData.inPvp());
            return;
        }
        switch (playerData.getState()) {
            case OFFLINE -> e.setCancelled(true);
            case LOBBY -> {
                e.setCancelled(true);
                if (e.getItem() == null || e.getItem().getType().equals(Material.AIR)) {
                    return;
                }
                final Hotbar.LobbyItems items = this.hotbar.lobbyItemsFromItemStack(e.getItem());
                switch (items) {
                    case UNRANKED -> this.menusManager.getPlayerMenu(playerData, "unranked").open(player);
                    case RANKED, SETTINGS, PROFILE -> player.sendMessage("§6Soon...");
                    case SPECTATE -> {
                        playerData.setState(PlayerState.SPECTATE);
                        playerData.applyHotbar();
                    }
                    case KIT_EDITOR -> this.menusManager.getPlayerMenu(playerData, "kit-editor");
                    case PARTY -> this.partyManager.create(playerData);
                    case LEAVE -> playerData.getPlayerQueue().clear();
                }
            }
        }
    }

    @EventHandler
    private void onInventoryClick(final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData == null) {
            e.setCancelled(true);
            return;
        }
        if (e.getClick().isCreativeAction() && !e.getWhoClicked().isOp() && !playerData.inLobby()) {
            e.setCancelled(true);
        }

        final Inventory inventory = (e.getClickedInventory() != null) ? e.getClickedInventory() : e.getInventory();
        if (inventory != null && inventory.getHolder() instanceof Menu) {
            try {
                ((Menu) inventory.getHolder()).onClick(e);
            } catch (Exception ex) {
                log("Can't handle click for " + inventory.getName() + ": " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        } else {
            if (player.getGameMode().equals(GameMode.CREATIVE) && player.isOp()) {
                return;
            }
            if (playerData.inLobby() || playerData.inQueue()) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    private void onInventoryClose(final InventoryCloseEvent e) {
        final Player player = e.getPlayer().getKiller();

        if (player == null) return;
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData == null) return;
        if (player.getGameMode().equals(GameMode.CREATIVE) && playerData.inLobby() && player.isOp()) return;

        final Inventory inventory = e.getInventory();
        if (inventory != null && inventory.getHolder() instanceof Menu) {
            try {
                ((Menu) inventory.getHolder()).onClose(e);
            } catch (Exception ex) {
                log("Can't handle click for " + inventory.getName() + ": " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    private void onInventoryDrag(final InventoryDragEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData == null) {
            e.setCancelled(true);
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE) && playerData.inLobby() && player.isOp()) {
            return;
        }
        final Inventory inventory = e.getInventory();
        if (inventory != null && inventory.getHolder() instanceof Menu) {
            try {
                ((Menu) inventory.getHolder()).onDrag(e);
            } catch (Exception ex) {
                log("Can't handle click for " + inventory.getName() + ": " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }
    }


}
