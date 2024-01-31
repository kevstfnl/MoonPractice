package net.moon.game.listeners;

import net.eno.utils.builders.ItemBuilder;
import net.minecraft.server.v1_12_R1.ItemPotion;
import net.moon.game.Practice;
import net.moon.game.objects.match.Match;
import net.moon.game.objects.players.PlayerData;
import net.moon.game.objects.players.PlayerMatch;
import net.moon.game.objects.players.PlayersManager;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MatchListener implements Listener {

    private final Practice instance;
    private final PlayersManager playersManager;

    public MatchListener(final Practice instance) {
        this.instance = instance;
        this.playersManager = instance.getPlayersManager();
    }

    @EventHandler
    private void onDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            final PlayerData playerData = this.playersManager.get(player);
            if (playerData == null || !playerData.inPvp()) {
                e.setCancelled(true);
                return;
            }

            final Match match = playerData.getPlayerMatch().getMatch();
            if (match == null || !match.getPlayers().contains(playerData) || match.getDies().contains(playerData)) {
                e.setCancelled(true);
                return;
            }
            if (e.getFinalDamage() >= player.getHealth()) {
                player.setHealth(20);
                match.handleDie(playerData);
                return;
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDamagePlayer(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
            final PlayerData victimData = this.playersManager.get(victim);
            final PlayerData attackerData = this.playersManager.get(attacker);
            if (victimData != null && attackerData != null) {
                if (victimData.inPvp() && attackerData.inPvp()) {
                    final Match match = victimData.getPlayerMatch().getMatch();
                    if (match.equals(attackerData.getPlayerMatch().getMatch())) {
                        match.handleHit(attackerData, victimData);
                        return;
                    }
                    e.setCancelled(true);
                }
                e.setCancelled(true);
            }
            e.setCancelled(true);
        }
        e.setCancelled(true);
    }

    @EventHandler
    private void onItemDrop(final PlayerDropItemEvent e) {
        final Player player = e.getPlayer();
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData != null) {
            if (playerData.inMatch()) {
                final Match match = playerData.getPlayerMatch().getMatch();
                if (match != null && !match.getDies().contains(playerData)) {
                    if (e.getItemDrop() instanceof ItemPotion potion) {
                        ((Entity)potion).remove();
                        return;
                    }
                    match.addEntity(e.getItemDrop());
                    return;
                }
                e.setCancelled(true);
            }
            e.setCancelled(true);
        }
        e.setCancelled(true);
    }

    @EventHandler
    private void onItemPickup(final PlayerAttemptPickupItemEvent e) {
        final Item item = e.getItem();
        final Player player = e.getPlayer();
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData != null) {
            if (playerData.inMatch()) {
                final Match match = playerData.getPlayerMatch().getMatch();
                if (match != null && !match.getDies().contains(playerData) && match.getEntities().contains(item)) {
                    match.removeEntity(e.getItem());
                    return;
                }
                e.setCancelled(true);
            }
            e.setCancelled(true);
        }
        e.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(final BlockBreakEvent e) {
        //TODO
    }

    @EventHandler
    private void onBlockPlace(final BlockPlaceEvent e) {
        //TODO
    }


    @EventHandler
    private void onPotionSplash(final PotionSplashEvent e) {
        for (LivingEntity entity : e.getAffectedEntities()) {
            if (entity instanceof Player player) {
                PlayerData playerData = this.playersManager.get(player);
                if (playerData != null) {
                    if (playerData.inMatch() && e.getEntity().getShooter().equals(playerData.getPlayer())) {
                        final Match match = playerData.getPlayerMatch().getMatch();
                        if (match != null) {
                            match.handlePotionSplash(playerData, e.getIntensity(entity) * 8.0);
                            return;
                        }
                        e.setCancelled(true);
                    }
                    e.setCancelled(true);
                }
                e.setCancelled(true);
            }
            e.setCancelled(true);
        }
        e.setCancelled(true);
    }

    @EventHandler
    private void onProjectileLaunch(final ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player player) {
            final PlayerData playerData = this.playersManager.get(player);
            if (playerData != null) {
                if (playerData.inMatch()) {
                    final PlayerMatch playerMatch = playerData.getPlayerMatch();
                    final Match match = playerMatch.getMatch();
                    if (match != null) {
                        if (e.getEntity() instanceof ThrownPotion) {
                            match.handlePotionThrown(playerData);
                        }
                        if (e.getEntity() instanceof EnderPearl) {
                            playerMatch.setLastPearl(System.currentTimeMillis());
                        }
                        match.addEntity(e.getEntity());
                        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
                            match.removeEntity(e.getEntity());
                            e.getEntity().remove();
                        }, 200L);
                        return;
                    }
                    e.setCancelled(true);
                }
                e.setCancelled(true);
            }
            e.setCancelled(true);
        }
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onInteract(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final PlayerData playerData = this.playersManager.get(player);
        if (playerData != null) {
            if (playerData.inPvp()) {
                final Match match = playerData.getPlayerMatch().getMatch();
                if (match != null && !match.getDies().contains(playerData)) {
                    if (e.getAction().name().contains("RIGHT")) {
                        switch (e.getItem().getType()) {
                            case ENDER_PEARL -> {
                                long last = playerData.getPlayerMatch().getLastPearl() / 1000 + 16;
                                long cooldown = last - (System.currentTimeMillis() / 1000);
                                if (!(cooldown <= 0)) {
                                    e.setCancelled(true);
                                    e.getPlayer().sendMessage("§6§lMatch §7Wait " + cooldown + "seconds before next pearl.");
                                    return;
                                }
                                return;
                            }
                            case MUSHROOM_SOUP -> {
                                e.setCancelled(true);
                                player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
                                        new ItemBuilder(Material.BOWL).setAmount(1).build());
                                player.setHealth(Math.min(player.getHealth() + 7, player.getMaxHealth()));
                                player.setFoodLevel(40);
                                player.setSaturation(12.7F);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onDeath(final PlayerDeathEvent e) {
        e.setDroppedExp(0);
        e.setDeathMessage(null);
        e.getDrops().clear();
    }
}
