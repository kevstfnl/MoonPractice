package net.moon.game.commands.admins;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.moon.game.Practice;
import net.moon.game.constants.PracticePermissions;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.kits.KitsManager;
import net.moon.game.utils.builders.ClickableBuilder;
import net.moon.spigot.Moon;
import net.moon.spigot.configurations.MoonConfig;
import net.moon.spigot.knockback.KnockbackProfile;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitCommand implements CommandExecutor {

    private final KitsManager kitsManager;
    private final BaseComponent separator;

    public KitCommand(final Practice instance) {
        this.kitsManager = instance.getKitsManager();
        this.separator = new TextComponent("§8» §8§m---------------------§8 «");
    }
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission(PracticePermissions.setup)) return false;
        if (sender instanceof Player player) {
            switch (args.length) {
                case 0 -> {
                    player.spigot().sendMessage(this.separator);
                    player.sendMessage(new TextComponent("§a§lKits Editor:"));
                    final BaseComponent create = new ClickableBuilder("§7(§aCreate§7)")
                            .setHover("§aClick here to create a new kit")
                            .setClick("/kit create ", ClickEvent.Action.SUGGEST_COMMAND).build();
                    final BaseComponent saveAll = new ClickableBuilder("§7(§eSave-All§7)")
                            .setHover("§aClick here to save all kits.")
                            .setClick("/kit saveall", ClickEvent.Action.RUN_COMMAND)
                            .build();

                    for (Kit kit : this.kitsManager.getKits().values()) {
                        final BaseComponent enable = new ClickableBuilder("  §7(" + (kit.isEnabled() ? "" : "§a") + "✓§7)")
                                .setHover(kit.isEnabled() ? "§cClick here to disable this kit." : "§aClick here to enable this kit.")
                                .setClick("/kit enable " + kit.getName(), ClickEvent.Action.RUN_COMMAND).build();
                        final BaseComponent kitName = new TextComponent("§e" + kit.getName());
                        final BaseComponent delete = new ClickableBuilder("§7(§c❌§7")
                                .setHover("§cClick here to delete this kit.")
                                .setClick("/kit delete " + kit.getName(), ClickEvent.Action.RUN_COMMAND).build();
                        final BaseComponent edit = new ClickableBuilder("§7(§e✎§7)")
                                .setHover("§7Click here to edit this kit.")
                                .setClick("/kit edit " + kit.getName(), ClickEvent.Action.RUN_COMMAND).build();
                        player.spigot().sendMessage(enable, kitName, delete, edit);
                    }
                    player.spigot().sendMessage(create, saveAll);
                    player.spigot().sendMessage(this.separator);
                }
                case 1 -> {
                    if (args[0].equalsIgnoreCase("saveall")) {
                        this.kitsManager.saveAll();
                    }
                }
                case 2 -> {
                    switch (args[0].toLowerCase()) {
                        case "create" -> {
                            final Kit kit = this.kitsManager.create(args[1]);
                            sendEditMessage(player, kit);
                            player.sendMessage("§aKit §e" + kit.getName() + " has been created.");
                        }
                        case "delete" -> {
                            final Kit kit = this.kitsManager.get(args[1]);
                            if (kit != null) {
                                this.kitsManager.delete(kit);
                                player.sendMessage("§aKit §e" + kit.getName() + " has been deleted.");
                            }
                        }
                        case "edit" -> {
                            final Kit kit = this.kitsManager.get(args[1]);
                            if (kit != null) {
                                sendEditMessage(player, kit);
                            }
                        }
                    }
                }
                case 3 -> {
                    final Kit kit = this.kitsManager.get(args[1]);
                    if (kit != null) {
                        switch (args[0].toLowerCase()) {
                            case "icon" -> {
                                final ItemStack item = player.getInventory().getItemInMainHand();
                                if (item != null && !item.getType().equals(Material.AIR)) {
                                    kit.setIcon(item);
                                    sendEditMessage(player, kit);
                                    player.sendMessage("§aIcon of §e" + kit.getName() + " has been set.");
                                } else {
                                    player.sendMessage("§cYou must have an item in your main hand !");
                                }

                            }
                            case "contents" -> {
                                kit.setContents(player.getInventory().getContents());
                                kit.setArmors(player.getInventory().getArmorContents());
                                sendEditMessage(player, kit);
                                player.sendMessage("§aKit contents has been set.");
                            }
                            case "effects" -> {
                                kit.setEffects(player.getActivePotionEffects());
                                sendEditMessage(player, kit);
                                player.sendMessage("§aKit effects has been set.");
                            }
                        }
                    }
                }
                case 4 -> {
                    final Kit kit = this.kitsManager.get(args[1]);
                    if (kit != null) {
                        switch (args[0].toLowerCase()) {
                            case "displayname" -> {
                                kit.setDisplayName(args[2]);
                                sendEditMessage(player, kit);
                                player.sendMessage("§aKit display name has been set.");
                            }
                            case "editable" -> {
                                kit.setEditable(Boolean.parseBoolean(args[2]));
                                sendEditMessage(player, kit);
                                player.sendMessage(kit.isEditable() ? "§aThis kit is now editable." : "§aThis kit is now uneditable.");
                            }
                            case "slot" -> {
                                kit.setSlot(Integer.parseInt(args[2]));
                                sendEditMessage(player, kit);
                                player.sendMessage("§aKit slot has been set.");
                            }
                            case "nodamagetick" -> {
                                kit.setNoDamageTicks(Integer.parseInt(args[2]));
                                sendEditMessage(player, kit);
                                player.sendMessage("§aKit no damage tick has been set.");
                            }
                            case "knockback" -> {
                                if (Practice.isUseMoon) {
                                    final KnockbackProfile profile = Moon.get().getKnockbackConfig().getKbProfileByName(args[2]);
                                    if (profile != null) {
                                        kit.setKnockbackProfile(profile);
                                        sendEditMessage(player, kit);
                                        player.sendMessage("§aKit knockback has been set.");
                                    } else  {
                                        player.sendMessage("§cThis knockback profile doesn't exist !");
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    private void sendEditMessage(final Player player, final Kit kit) {
        final BaseComponent displayName = new ClickableBuilder("  §eDisplay-Name: §7" + kit.getDisplayName())
                .setHover("")
                .setClick("/kit displayname " + kit.getName() + " ", ClickEvent.Action.SUGGEST_COMMAND)
                .build();
        final BaseComponent editable = new ClickableBuilder("  §eEditable: §7" + kit.getDisplayName())
                .setHover("")
                .setClick("/kit editable " + kit.getName(), ClickEvent.Action.SUGGEST_COMMAND)
                .build();
        final BaseComponent slot = new ClickableBuilder("  §eSlot: §7" + kit.getSlot())
                .setHover("")
                .setClick("/kit slot " + kit.getName() + " ", ClickEvent.Action.SUGGEST_COMMAND)
                .build();
        final BaseComponent noDamageTick = new ClickableBuilder("  §eNo Damage Tick: §7" + kit.getNoDamageTicks())
                .setHover("")
                .setClick("/kit nodamagetick " + kit.getName() + " ", ClickEvent.Action.SUGGEST_COMMAND)
                .build();
        final BaseComponent icon = new ClickableBuilder("§7§eIcon§7)")
                .setHover("")
                .setClick("/kit icon " + kit.getName(), ClickEvent.Action.RUN_COMMAND)
                .build();
        final BaseComponent contents = new ClickableBuilder("§7§eContents§7)")
                .setHover("")
                .setClick("/kit contents " + kit.getName(), ClickEvent.Action.RUN_COMMAND)
                .build();
        final BaseComponent effects = new ClickableBuilder("§7§eEffects§7)")
                .setHover("")
                .setClick("/kit effects " + kit.getName(), ClickEvent.Action.RUN_COMMAND)
                .build();
        final BaseComponent save = new ClickableBuilder("§7§eSave§7)")
                .setHover("")
                .setClick("/kit save " + kit.getName(), ClickEvent.Action.RUN_COMMAND)
                .build();

        player.spigot().sendMessage(this.separator);
        player.sendMessage(new TextComponent("§a§lKits §e§l" + kit.getName() + "§a§lEditor:"));
        player.spigot().sendMessage(displayName);
        player.spigot().sendMessage(editable);
        player.spigot().sendMessage(slot);
        player.spigot().sendMessage(noDamageTick);
        player.spigot().sendMessage(icon, contents, effects);
        player.spigot().sendMessage(save);
        player.spigot().sendMessage(this.separator);
    }
}
