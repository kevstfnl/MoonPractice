package net.moon.game.utils.commons;

import net.moon.game.Practice;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtils {

    public static void run(final Runnable runnable) {
        getPlugin().getServer().getScheduler().runTask(getPlugin(), runnable);
    }

    public static void runTimer(final Runnable runnable, final long delay, final long timer) {
        getPlugin().getServer().getScheduler().runTaskTimer(getPlugin(), runnable, delay, timer);
    }

    public static void runTimer(final BukkitRunnable runnable, final long delay, final long timer) {
        runnable.runTaskTimer(getPlugin(), delay, timer);
    }

    public static void runLater(final Runnable runnable, final long delay) {
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), runnable, delay);
    }

    public static void runLaterAsync(final Runnable runnable, final long delay) {
        getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), runnable, delay);
    }

    public static void runAsync(final Runnable runnable) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), runnable);
    }

    private static JavaPlugin getPlugin() {
        return Practice.get();
    }
}
