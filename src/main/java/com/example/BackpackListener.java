package com.example;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
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
        int backpackSlots = BackpackItem.getBackpackSlots(itemInHand);

        // Create and open the backpack inventory with the correct size
        Inventory backpackInventory = Bukkit.createInventory(player, backpackSlots, "Backpack");
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

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        ItemStack backpack = null;
        ItemStack upgrade = null;
        int itemCount = 0;
        boolean hasCustomItem = false;

        // Scan the crafting matrix for custom items
        for (ItemStack item : matrix) {
            if (item != null && item.getType() != Material.AIR) {
                itemCount++;
                if (BackpackItem.isBackpack(item)) {
                    backpack = item;
                    hasCustomItem = true;
                } else if (BackpackUpgrade.isUpgradeItem(item)) {
                    upgrade = item;
                    hasCustomItem = true;
                }
            }
        }

        // If no custom items are involved, let vanilla crafting proceed
        if (!hasCustomItem) {
            return;
        }

        // Custom items are in the grid - only allow our specific recipes
        // Check for valid backpack upgrade: exactly one backpack + one upgrade, nothing else
        if (itemCount == 2 && backpack != null && upgrade != null) {
            int currentLevel = BackpackItem.getBackpackLevel(backpack);
            int upgradeLevel = BackpackUpgrade.getUpgradeLevel(upgrade);

            // Check if this upgrade can be applied to this backpack
            if (currentLevel >= 0 && upgradeLevel >= 1 && currentLevel == upgradeLevel - 1) {
                // Valid upgrade combination - create the upgraded backpack
                ItemStack upgradedBackpack = backpack.clone();

                // Load current contents
                ItemStack[] contents = BackpackItem.loadInventory(backpack);

                // Apply the upgrade
                if (BackpackItem.upgradeBackpack(upgradedBackpack)) {
                    // Save the contents to the upgraded backpack
                    BackpackItem.saveInventory(upgradedBackpack, contents);
                    inventory.setResult(upgradedBackpack);
                    return;
                }
            }
        }

        // If we reach here, custom items are being used in an invalid recipe
        // Cancel the craft by setting result to null
        inventory.setResult(null);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is a backpack
        if (!event.getView().getTitle().equals("Backpack")) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        // Check if we're trying to place a backpack into the backpack inventory
        if (cursorItem != null && BackpackItem.isBackpack(cursorItem) &&
            clickedInventory != null && clickedInventory.equals(topInventory)) {
            // Prevent placing backpacks into backpack inventories (top inventory)
            event.setCancelled(true);
            return;
        }

        // Check shift-clicking behavior - determine if item is being moved TO or FROM backpack
        if (event.isShiftClick() && currentItem != null && BackpackItem.isBackpack(currentItem)) {
            // Determine which inventory the clicked item is in
            Inventory bottomInventory = event.getView().getBottomInventory();

            // If the backpack item is in the top inventory (backpack), allow moving it out
            // If the backpack item is in the bottom inventory (player), prevent moving it in
            if (clickedInventory != null && clickedInventory.equals(bottomInventory)) {
                // Player is trying to shift-click a backpack from their inventory INTO the backpack
                event.setCancelled(true);
                return;
            }
            // If clicked inventory is top inventory, allow moving backpack out (no cancellation needed)
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
