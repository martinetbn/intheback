package com.example;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event listener for backpack interactions
 */
public class BackpackListener implements Listener {

    private final MinecraftBackpackPlugin plugin;
    private final Map<UUID, ItemStack> openBackpacks = new HashMap<>();

    public BackpackListener(MinecraftBackpackPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if player right-clicked
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the item is a backpack
        if (!BackpackItem.isBackpack(itemInHand)) {
            return;
        }

        // Cancel the event to prevent normal item usage
        event.setCancelled(true);

        // Load backpack contents
        ItemStack[] contents = BackpackItem.loadInventory(itemInHand);

        // Create and open the backpack inventory
        Inventory backpackInventory = Bukkit.createInventory(player, 27, "Backpack");
        backpackInventory.setContents(contents);

        // Track which backpack is being opened
        openBackpacks.put(player.getUniqueId(), itemInHand.clone());

        player.openInventory(backpackInventory);

        plugin.getLogger().info(player.getName() + " opened their backpack!");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Check if the closed inventory is a backpack
        if (!event.getView().getTitle().equals("Backpack")) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        // Get the backpack that was opened
        ItemStack openedBackpack = openBackpacks.remove(player.getUniqueId());
        if (openedBackpack == null) {
            return;
        }

        // Find the backpack item in the player's current inventory and update it
        PlayerInventory playerInv = player.getInventory();
        boolean foundAndUpdated = false;

        // Check main inventory
        for (int i = 0; i < playerInv.getSize(); i++) {
            ItemStack item = playerInv.getItem(i);
            if (isSameBackpack(item, openedBackpack)) {
                ItemStack[] contents = inventory.getContents();
                BackpackItem.saveInventory(item, contents);
                foundAndUpdated = true;
                break;
            }
        }

        // If not found in main inventory, check off-hand
        if (!foundAndUpdated) {
            ItemStack offHandItem = playerInv.getItemInOffHand();
            if (isSameBackpack(offHandItem, openedBackpack)) {
                ItemStack[] contents = inventory.getContents();
                BackpackItem.saveInventory(offHandItem, contents);
                foundAndUpdated = true;
            }
        }

        if (foundAndUpdated) {
            plugin.getLogger().info(player.getName() + " closed their backpack - items saved!");
        } else {
            plugin.getLogger().warning(player.getName() + " closed backpack but couldn't find the item to save!");
        }
    }

    /**
     * Checks if two backpack items are the same (by comparing their NBT data)
     */
    private boolean isSameBackpack(ItemStack item1, ItemStack item2) {
        return BackpackItem.isBackpack(item1) &&
               BackpackItem.isBackpack(item2) &&
               item1.isSimilar(item2);
    }
}
