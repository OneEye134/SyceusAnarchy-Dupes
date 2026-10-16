package com.syceus.anarchydupes;

import org.bukkit.plugin.java.JavaPlugin;

public final class SyceusAnarchyDupes extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ItemFrameDupeListener(this), this);
        getLogger().info("SyceusAnarchy-Dupes enabled. Master switch: "
                + getConfig().getBoolean("dupe-master-switch", true)
                + ", dupe percentage: " + getConfig().getInt("dupe-percentage", 25) + "%");
    }
}
