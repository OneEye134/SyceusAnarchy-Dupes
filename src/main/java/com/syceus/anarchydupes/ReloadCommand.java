package com.syceus.anarchydupes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/** Handles {@code /anarchydupes reload}. */
public final class ReloadCommand implements CommandExecutor, TabCompleter {

    public static final String PERMISSION = "syceusanarchydupes.reload";

    private final SyceusAnarchyDupes plugin;

    public ReloadCommand(SyceusAnarchyDupes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("Usage: /" + label + " reload");
            return true;
        }
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("You don't have permission to do that.");
            return true;
        }

        plugin.reloadPluginConfig();
        sender.sendMessage("SyceusAnarchy-Dupes config reloaded. Master switch: "
                + plugin.getConfig().getBoolean("dupe-master-switch", true)
                + ", dupe percentage: " + plugin.getConfig().getInt("dupe-percentage", 25) + "%");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(PERMISSION)
                && "reload".startsWith(args[0].toLowerCase(Locale.ROOT))) {
            return List.of("reload");
        }
        return List.of();
    }
}
