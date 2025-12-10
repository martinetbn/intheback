package com.example;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

/**
 * Utility class for creating and managing backpack upgrade items
 */
public class BackpackUpgrade {

    private static final NamespacedKey UPGRADE_KEY = new NamespacedKey("minecraft-backpack", "upgrade");
    private static final NamespacedKey UPGRADE_LEVEL_KEY = new NamespacedKey("minecraft-backpack", "upgrade_level");

    /**
     * Creates an upgrade item for backpacks
     * @param level The level this upgrade item provides (1=to medium, 2=to large, 3=to huge)
     * @return ItemStack representing an upgrade item
     */
    public static ItemStack createUpgradeItem(int level) {
        Material material;
        String name;
        String description;

        switch (level) {
            case 1:
                material = Material.IRON_INGOT;
                name = "§6Medium Backpack Upgrade";
                description = "§7Upgrade a Small Backpack to Medium (4 rows)";
                break;
            case 2:
                material = Material.GOLD_INGOT;
                name = "§6Large Backpack Upgrade";
                description = "§7Upgrade a Medium Backpack to Large (5 rows)";
                break;
            case 3:
                material = Material.DIAMOND;
                name = "§6Huge Backpack Upgrade";
                description = "§7Upgrade a Large Backpack to Huge (6 rows)";
                break;
            default:
                return null;
        }

        ItemStack upgrade = new ItemStack(material);
        ItemMeta meta = upgrade.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(
                description,
                "§7Combine with a backpack in a crafting table to upgrade it",
                "§8§oIn The Back"
            ));

            // Mark this as an upgrade item
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(UPGRADE_KEY, PersistentDataType.BOOLEAN, true);
            container.set(UPGRADE_LEVEL_KEY, PersistentDataType.INTEGER, level);

            upgrade.setItemMeta(meta);
        }

        return upgrade;
    }

    /**
     * Checks if an ItemStack is a backpack upgrade item
     * @param item The item to check
     * @return true if the item is an upgrade item
     */
    public static boolean isUpgradeItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(UPGRADE_KEY, PersistentDataType.BOOLEAN) &&
               container.get(UPGRADE_KEY, PersistentDataType.BOOLEAN);
    }

    /**
     * Gets the upgrade level of an upgrade item
     * @param upgradeItem The upgrade item
     * @return The upgrade level, or -1 if not an upgrade item
     */
    public static int getUpgradeLevel(ItemStack upgradeItem) {
        if (!isUpgradeItem(upgradeItem)) {
            return -1;
        }

        ItemMeta meta = upgradeItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        Integer level = container.get(UPGRADE_LEVEL_KEY, PersistentDataType.INTEGER);
        return level != null ? level : -1;
    }

    /**
     * Registers crafting recipes for upgrade items
     */
    public static void registerUpgradeRecipes() {
        // Medium Backpack Upgrade Recipe (level 1)
        NamespacedKey mediumUpgradeKey = new NamespacedKey("minecraft-backpack", "medium_upgrade");
        ShapedRecipe mediumRecipe = new ShapedRecipe(mediumUpgradeKey, createUpgradeItem(1));

        mediumRecipe.shape(
            "III",
            "IBI",
            "III"
        );

        mediumRecipe.setIngredient('I', Material.IRON_INGOT);
        mediumRecipe.setIngredient('B', Material.LEATHER);

        org.bukkit.Bukkit.addRecipe(mediumRecipe);

        // Large Backpack Upgrade Recipe (level 2)
        NamespacedKey largeUpgradeKey = new NamespacedKey("minecraft-backpack", "large_upgrade");
        ShapedRecipe largeRecipe = new ShapedRecipe(largeUpgradeKey, createUpgradeItem(2));

        largeRecipe.shape(
            "GGG",
            "GBG",
            "GGG"
        );

        largeRecipe.setIngredient('G', Material.GOLD_INGOT);
        largeRecipe.setIngredient('B', Material.LEATHER);

        org.bukkit.Bukkit.addRecipe(largeRecipe);

        // Huge Backpack Upgrade Recipe (level 3)
        NamespacedKey hugeUpgradeKey = new NamespacedKey("minecraft-backpack", "huge_upgrade");
        ShapedRecipe hugeRecipe = new ShapedRecipe(hugeUpgradeKey, createUpgradeItem(3));

        hugeRecipe.shape(
            "DDD",
            "DBD",
            "DDD"
        );

        hugeRecipe.setIngredient('D', Material.DIAMOND);
        hugeRecipe.setIngredient('B', Material.LEATHER);

        org.bukkit.Bukkit.addRecipe(hugeRecipe);
    }

    /**
     * Unregisters upgrade crafting recipes
     */
    public static void unregisterUpgradeRecipes() {
        org.bukkit.Bukkit.removeRecipe(new NamespacedKey("minecraft-backpack", "medium_upgrade"));
        org.bukkit.Bukkit.removeRecipe(new NamespacedKey("minecraft-backpack", "large_upgrade"));
        org.bukkit.Bukkit.removeRecipe(new NamespacedKey("minecraft-backpack", "huge_upgrade"));
    }
}
