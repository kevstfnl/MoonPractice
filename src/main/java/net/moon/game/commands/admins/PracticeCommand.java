package net.moon.game.commands.admins;

import net.eno.utils.builders.ClickableBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.moon.game.constants.PracticePermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PracticeCommand implements CommandExecutor {

    private final BaseComponent separator, title, arenaCommand, kitCommand, lobbyCommand;

    public PracticeCommand() {
        this.separator = new TextComponent("§8» §8§m---------------------§8 «");
        this.title = new TextComponent("§a§lPractice setup modules:");
        this.lobbyCommand = new ClickableBuilder("  §eLobby Module")
                .setHover("§7Enter into lobby module")
                .setClick("/lobby", ClickEvent.Action.RUN_COMMAND).build();
        this.arenaCommand = new ClickableBuilder("  §eArena Module")
                .setHover("§7Enter into arena module")
                .setClick("/arena", ClickEvent.Action.RUN_COMMAND).build();
        this.kitCommand = new ClickableBuilder("  §eKit Module")
                .setHover("§7Enter into kit module")
                .setClick("/kit", ClickEvent.Action.RUN_COMMAND).build();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission(PracticePermissions.setup)) return false;
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.spigot().sendMessage(this.separator);
                player.spigot().sendMessage(this.title);
                player.spigot().sendMessage(this.lobbyCommand);
                player.spigot().sendMessage(this.arenaCommand);
                player.spigot().sendMessage(this.kitCommand);
                player.spigot().sendMessage(this.separator);
                return true;
            }
            return false;
        }
        return false;
    }
}
