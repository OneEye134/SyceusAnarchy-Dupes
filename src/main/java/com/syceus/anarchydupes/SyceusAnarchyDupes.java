package com.syceus.anarchydupes;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SyceusAnarchyDupes extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ItemFrameDupeListener(this), this);

        PluginCommand command = getCommand("anarchydupes");
        if (command != null) {
            ReloadCommand reloadCommand = new ReloadCommand(this);
            command.setExecutor(reloadCommand);
            command.setTabCompleter(reloadCommand);
        }

        logSettings("enabled");
    }

    /** Re-reads config.yml from disk. The listener reads values live, so changes apply immediately. */
    public void reloadPluginConfig() {
        reloadConfig();
        int percentage = getConfig().getInt("dupe-percentage", 25);
        if (percentage < 0 || percentage > 100) {
            getLogger().warning("dupe-percentage " + percentage + " is outside 0-100 and will be clamped.");
        }
        logSettings("config reloaded");
    }

    private void logSettings(String what) {
        getLogger().info("SyceusAnarchy-Dupes " + what + ". Master switch: "
                + getConfig().getBoolean("dupe-master-switch", true)
                + ", dupe percentage: " + getConfig().getInt("dupe-percentage", 25) + "%");
    }
}
