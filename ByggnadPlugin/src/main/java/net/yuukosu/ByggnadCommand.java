package net.yuukosu;

import com.google.common.collect.ImmutableSet;
import net.yuukosu.data.Byggnad;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ByggnadCommand implements CommandExecutor, TabCompleter {

    private final ImmutableSet<String> args = ImmutableSet.of(
            "wand",
            "save",
            "load"
    );
    private final ImmutableSet<String> usages = ImmutableSet.of(
            "/%s wand",
            "/%s save <New Byggnad Name> [<Skip Air>]",
            "/%s load <Byggnad Name>"
    );
    private static final Map<Player, Location> pos1 = new HashMap<>();
    private static final Map<Player, Location> pos2 = new HashMap<>();

    public static void setPos1(Player player, Location location) {
        pos1.put(player, location);
    }

    public static void setPos2(Player player, Location location) {
        pos2.put(player, location);
    }

    public void printUsage(CommandSender sender, Command command) {
        for (String usage : this.usages) {
            sender.sendMessage(ChatColor.RED + String.format(" - " + usage, command.getName()));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<String, Byggnad> byggnadList = ByggnadPlugin.getByggnadList();

        if (args.length > 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cThis command can only be performed by players.");
                return true;
            }

            if (args[0].equalsIgnoreCase("load")) {
                if (args.length > 1) {
                    String name = args[1].toLowerCase();

                    if (!byggnadList.containsKey(name)) {
                        sender.sendMessage(String.format("§cNo Byggnad Data Found For \"%s\"", name));
                        return true;
                    }

                    player.sendMessage("§7Loading...");

                    Byggnad byggnad = byggnadList.get(name);
                    byggnad.byggnad(player.getLocation());

                    player.sendMessage("§aDone!");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);
                } else {
                    this.printUsage(sender, command);
                }
            } else if (args[0].equalsIgnoreCase("save")) {
                if (args.length > 1) {
                    String name = args[1].toLowerCase();

                    if (byggnadList.containsKey(name)) {
                        sender.sendMessage(String.format("§c\"%s\" Byggnad Data Already Exists.", name));
                        return true;
                    }

                    if (!pos1.containsKey(player) || !pos2.containsKey(player)) {
                        sender.sendMessage("§cYou have not selected a section.");
                        return true;
                    }

                    boolean skipAir = true;

                    if (args.length > 2) {
                        try {
                            skipAir = Boolean.parseBoolean(args[2]);
                        } catch (IllegalArgumentException e) {
                            this.printUsage(sender, command);
                            return true;
                        }
                    }

                    player.sendMessage("§7Saving...");

                    Byggnad byggnad = Byggnad.createInstance(player.getLocation(), pos1.get(player), pos2.get(player), skipAir);

                    if (!ByggnadPlugin.getByggnadList().containsKey(name)) {
                        ByggnadPlugin.getInstance().addByggnadData(name, byggnad);
                        player.sendMessage("§aDone!");
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);
                    } else {
                        player.sendMessage("§cWhat?");
                    }
                } else {
                    this.printUsage(sender, command);
                }
            } else if (args[0].equalsIgnoreCase("wand")) {
                ItemStack goldenAxe = new ItemStack(Material.GOLD_AXE);
                player.getInventory().addItem(goldenAxe);
            } else {
                this.printUsage(sender, command);
            }
        } else {
            this.printUsage(sender, command);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("byggnad.admin")) return List.of();

        if (args.length == 1) {
            return this.args.stream()
                    .filter(arg -> arg.startsWith(args[0]))
                    .toList();
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("load")) {
                return ByggnadPlugin.getByggnadList().keySet().stream()
                        .filter(key -> key.startsWith(args[1]))
                        .toList();
            }
        }

        return this.args.stream().toList();
    }
}