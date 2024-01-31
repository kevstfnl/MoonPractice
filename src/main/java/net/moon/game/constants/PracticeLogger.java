package net.moon.game.constants;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PracticeLogger {

    public static String SILENT_PREFIXX = "§c§lSILENT §7";


    public static void silent(final LogLevel level, final String message) {
        for (Player ops : Bukkit.getOnlinePlayers()) {
            if (ops.hasPermission(PracticePermissions.silent)) {
                ops.sendMessage("§c§lSILENT §7" + level.prefix + message);
            }
        }
    }

    public static void log(final String message) {
        log(LogLevel.INFO, message);
    }

    public static void debug(final String message) {
        log(LogLevel.DEBUG, message);
    }

    public static void log(final LogLevel level, final String message) {
        if (level.equals(LogLevel.DEBUG)) { //Check if log is a debug and if debug are enabled.
            return;
        }
        System.out.println(level.color.getColor() + level.prefix + " " + message); //Send message into console.
    }

    public enum LogLevel {
        INFO("", AnsiColor.WHITE),
        DEBUG("[Debug]", AnsiColor.GREEN),
        WARNING("[Warn]", AnsiColor.YELLOW),
        ERROR("[Error]", AnsiColor.RED);

        private final String prefix;
        private final AnsiColor color;

        LogLevel(final String prefix, final AnsiColor color) {
            this.prefix = prefix;
            this.color = color;
        }
    }
}
