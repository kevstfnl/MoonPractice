package net.moon.game.commands.admins;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.moon.game.Practice;
import net.moon.game.constants.PracticePermissions;
import net.moon.game.objects.arenas.ArenasManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    private final ArenasManager arenasManager;
    private final BaseComponent separator;

    public ArenaCommand(final Practice instance) {
        this.arenasManager = instance.getArenasManager();
        this.separator = new TextComponent("§8» §8§m---------------------§8 «");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission(PracticePermissions.setup)) return false;
        if (sender instanceof Player player) {

        }
        return false;
    }

    /*
    » --------------------- «
    §a§lArena Editor:
    §7(✓) Test §7(§e✎§7)
    "§7(§aCreate§7)" "§7(§eSave-All§7)"
    » --------------------- «
     */

    /*
    » --------------------- «
    §a§lArena Test Editor:
    displayName;
    author;
    icon;
    slot;

    Locations:
    Kits:
    "§7§f\uD83E\uDC80§7) " "§7§eSave§7)"
    » --------------------- «

     » --------------------- «
    §a§lArena Test's locations Editor:
    Area angles: [1] [2]
    Border angles [1] [2]
    Spawn (0): [+] [-] [x]
    "§7§f\uD83E\uDC80§7) "
    » --------------------- «
     */
}
