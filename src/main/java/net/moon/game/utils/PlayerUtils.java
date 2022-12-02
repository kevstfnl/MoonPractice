package net.moon.game.utils;

import net.minecraft.server.v1_12_R1.*;
import net.moon.game.Practice;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {

    private static final Practice plugin = Practice.get();
    private static final BukkitScheduler scheduler = Practice.get().getServer().getScheduler();

    public static void animateDeath(final Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final List<Player> viewers = getTrackedPlayers(player);

        final List<Packet<?>> packets = new ArrayList<>();
        packets.add(new PacketPlayOutSpawnEntityLiving(entityPlayer));
        packets.add(new PacketPlayOutEntityStatus(entityPlayer, (byte) 3));
        final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());

        scheduler.runTaskLaterAsynchronously(plugin, () -> {
            sendPackets(viewers, packets);
            scheduler.runTaskLaterAsynchronously(plugin, () -> sendPacket(viewers, destroy), 60L);
        }, 1L);
    }

    public static void resetPlayer(final Player player) {
        if (player.isDead()) {
            player.spigot().respawn();
        }
        player.setHealthScale(20.0D);
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setCanPickupItems(false);
        player.getInventory().clear();
        player.getPlayer().getInventory().setArmorContents(null);
        player.getInventory().setHeldItemSlot(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setTotalExperience(0);
        player.setExp(0.0F);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFireTicks(0);
        player.setNoDamageTicks(0);
        player.setMaximumNoDamageTicks(20);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setWalkSpeed(0.2F);
        player.setItemOnCursor(null);
        player.setFallDistance(0.0F);
        ((CraftPlayer) player).getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b), 0);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        player.setGameMode(GameMode.SURVIVAL);
    }

    public static void sendPacket(final Player player, final Packet<?> packet) {
        final List<Player> viewers = getTrackedPlayers(player);
        sendPacket(viewers, packet);
    }

    public static void sendPackets(final Player player, final List<Packet<?>> packets) {
        final List<Player> viewers = getTrackedPlayers(player);
        sendPackets(viewers, packets);
    }

    public static void sendPacket(final Player player, final Packet<?> packet, final boolean includeSelf) {
        final List<Player> viewers = getTrackedPlayers(player);
        if (includeSelf) viewers.add(player);
        sendPacket(viewers, packet);
    }

    public static void sendPackets(final Player player, final List<Packet<?>> packets, final boolean includeSelf) {
        final List<Player> viewers = getTrackedPlayers(player);
        if (includeSelf) viewers.add(player);
        sendPackets(viewers, packets);
    }

    public static void sendPacket(final List<Player> players, final Packet<?> packet) {
        for (Player viewer : players) {
            final EntityPlayer entityPlayer = ((CraftPlayer) viewer).getHandle();
            entityPlayer.playerConnection.sendPacket(packet);
        }
    }

    public static void sendPackets(final List<Player> players, final List<Packet<?>> packets) {
        for (Player viewer : players) {
            final EntityPlayer entityPlayer = ((CraftPlayer) viewer).getHandle();
            for (Packet<?> packet : packets) {
                entityPlayer.playerConnection.sendPacket(packet);
            }
        }
    }

    public static List<Player> getTrackedPlayers(final Player player) {
        final List<Player> players = new ArrayList<>();

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final EntityTracker tracker = ((WorldServer) entityPlayer.world).tracker;
        final EntityTrackerEntry entry = tracker.trackedEntities.get(entityPlayer.getBukkitEntity().getEntityId());

        if (entry != null) {
            for (EntityPlayer trackedPlayer : entry.trackedPlayers) {
                players.add(trackedPlayer.getBukkitEntity().getPlayer());
            }
        }
        return players;
    }
}

