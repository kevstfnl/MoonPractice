package net.moon.game.commands.admins;

import net.eno.utils.builders.ClickableBuilder;
import net.eno.utils.world.Cuboid;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.moon.game.Practice;
import net.moon.game.constants.PracticePermissions;
import net.moon.game.objects.practice.lobby.Lobby;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandExecutor {

    private final Lobby lobby;
    private final BaseComponent separator, title;

    private Location borderAngle1, borderAngle2;

    public LobbyCommand(final Practice instance) {
        this.lobby = instance.getPracticeManager().getLobby();
        this.separator = new TextComponent("§8» §8§m---------------------§8 «");
        this.title = new TextComponent("§a§lPractice lobby setup module:");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission(PracticePermissions.setup)) return false;
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.spigot().sendMessage(this.separator);
                player.spigot().sendMessage(this.title);

                final BaseComponent lobby = new ClickableBuilder("§7(§aSpawn§7)")
                        .setHover("§7Click here to set main lobby spawn.")
                        .setClick("/lobby setspawn", ClickEvent.Action.RUN_COMMAND).build();
                final BaseComponent angle1 = new ClickableBuilder("§7[§eAngle 1§7] ")
                        .setHover("§7Click here to set first angle of lobby border.")
                        .setClick("/lobby angle1", ClickEvent.Action.RUN_COMMAND).build();
                final BaseComponent angle2 = new ClickableBuilder("§7[§eAngle 2§7]")
                        .setHover("§7Click here to set first angle of lobby border.")
                        .setClick("/lobby angle2", ClickEvent.Action.RUN_COMMAND).build();
                final BaseComponent border = new TextComponent("§7(§aBorder: ");
                final BaseComponent borderEnd = new TextComponent("§7)");
                player.spigot().sendMessage(lobby);
                player.spigot().sendMessage(border, angle1, angle2, borderEnd);
                player.spigot().sendMessage(this.separator);
            }
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "setspawn" -> {
                        this.lobby.setSpawn(player.getLocation());
                        player.sendMessage("§aMain lobby spawn has been set.");
                    }
                    case "angle1" -> {
                        this.borderAngle1 = player.getLocation();
                        player.sendMessage("§aFirst angle of lobby border has been set.");
                        if (this.borderAngle2 != null) {
                            this.lobby.setBorder(new Cuboid(this.borderAngle1, this.borderAngle2));
                            this.borderAngle1 = null;
                            this.borderAngle2 = null;
                            player.sendMessage("§aAll angle has been placed, the border has been set.");
                        }
                    }
                    case "angle2" -> {
                        this.borderAngle2 = player.getLocation();
                        player.sendMessage("§aSecond angle of lobby border has been set.");
                        if (this.borderAngle1 != null) {
                            this.lobby.setBorder(new Cuboid(this.borderAngle1, this.borderAngle2));
                            this.borderAngle1 = null;
                            this.borderAngle2 = null;
                            player.sendMessage("§aAll angle has been placed, the border has been set.");
                        }
                    }
                }
            }

        }
        return false;
    }
}
