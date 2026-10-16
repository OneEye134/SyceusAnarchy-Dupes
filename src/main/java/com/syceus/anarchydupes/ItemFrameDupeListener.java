package com.syceus.anarchydupes;

import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tracks how many times the item inside each item frame has been rotated.
 * When a player takes the item out after at least {@link #REQUIRED_ROTATIONS} rotations,
 * there is a configurable chance that a second copy of the item drops.
 */
public final class ItemFrameDupeListener implements Listener {

    private static final int REQUIRED_ROTATIONS = 3;

    private final SyceusAnarchyDupes plugin;
    // Item frame UUID -> number of rotations since the current item was placed.
    private final Map<UUID, Integer> rotations = new HashMap<>();

    public ItemFrameDupeListener(SyceusAnarchyDupes plugin) {
        this.plugin = plugin;
    }

    /** Right click on a frame: placing an item resets the counter, clicking a filled frame rotates it. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRotate(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (!(event.getRightClicked() instanceof ItemFrame frame)) {
            return;
        }

        UUID id = frame.getUniqueId();
        ItemStack held = frame.getItem();

        if (held.getType().isAir()) {
            // Frame was empty, so this click places an item. Start counting from zero.
            rotations.remove(id);
            return;
        }

        // Frame already holds an item, so this click rotates it.
        rotations.merge(id, 1, (a, b) -> Math.min(a + b, REQUIRED_ROTATIONS));
    }

    /** Left click on a filled frame takes the item out. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTakeOut(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame frame)) {
            return;
        }
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        ItemStack item = frame.getItem();
        if (item.getType().isAir()) {
            // Hitting an empty frame breaks the frame itself, nothing to dupe.
            rotations.remove(frame.getUniqueId());
            return;
        }

        Integer count = rotations.remove(frame.getUniqueId());
        int rotated = count == null ? 0 : count;
        // Creative players don't get the item dropped by vanilla, so don't hand out free items either.
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (rotated < REQUIRED_ROTATIONS) {
            return;
        }
        if (!plugin.getConfig().getBoolean("dupe-master-switch", true)) {
            return;
        }

        int percentage = Math.clamp(plugin.getConfig().getInt("dupe-percentage", 25), 0, 100);
        if (percentage <= 0 || ThreadLocalRandom.current().nextInt(100) >= percentage) {
            return;
        }

        // Vanilla drops the original item; drop one extra copy so the player gets 2.
        ItemStack extra = item.clone();
        extra.setAmount(1);
        frame.getWorld().dropItemNaturally(frame.getLocation(), extra);
    }

    /** Clean up counters when a frame disappears for any reason. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemove(EntityRemoveEvent event) {
        if (event.getEntity() instanceof ItemFrame frame) {
            rotations.remove(frame.getUniqueId());
        }
    }
}
