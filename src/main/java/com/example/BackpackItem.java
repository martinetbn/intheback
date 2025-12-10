package com.example;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * Utility class for creating and managing backpack items with proper persistence
 * Each backpack has a unique ID to prevent conflicts between multiple backpacks
 * Backpacks can be upgraded to have more inventory slots
 */
public class BackpackItem {

    // Backpack size constants (rows × 9 slots)
    public static final int SMALL_BACKPACK_SIZE = 27;  // 3 rows (basic)
    public static final int MEDIUM_BACKPACK_SIZE = 36; // 4 rows
    public static final int LARGE_BACKPACK_SIZE = 45;  // 5 rows
    public static final int HUGE_BACKPACK_SIZE = 54;   // 6 rows

    // Maximum upgrade level
    public static final int MAX_BACKPACK_LEVEL = 3; // 0=small, 1=medium, 2=large, 3=huge

    private static final NamespacedKey BACKPACK_KEY = new NamespacedKey("minecraft-backpack", "backpack");
    private static final NamespacedKey BACKPACK_ID_KEY = new NamespacedKey("minecraft-backpack", "backpack_id");
    private static final NamespacedKey BACKPACK_LEVEL_KEY = new NamespacedKey("minecraft-backpack", "backpack_level");
    private static final NamespacedKey INVENTORY_KEY = new NamespacedKey("minecraft-backpack", "inventory");

    /**
     * Gets the appropriate chest material for a backpack level
     * @param level The backpack level
     * @return Material for the backpack
     */
    private static Material getChestMaterialForLevel(int level) {
        switch (level) {
            case 0: return Material.CHEST; // Small backpack
            case 1: return Material.WAXED_COPPER_CHEST; // Medium backpack (iron upgrade)
            case 2: return Material.WAXED_WEATHERED_COPPER_CHEST; // Large backpack (gold upgrade)
            case 3: return Material.WAXED_OXIDIZED_COPPER_CHEST; // Huge backpack (diamond upgrade)
            default: return Material.CHEST;
        }
    }

    /**
     * Creates a new backpack item with a unique ID
     * @param level The backpack level (0=small, 1=medium, 2=large, 3=huge)
     * @return ItemStack representing a backpack
     */
    public static ItemStack createBackpack(int level) {
        level = Math.max(0, Math.min(level, MAX_BACKPACK_LEVEL));

        Material chestMaterial = getChestMaterialForLevel(level);
        ItemStack backpack = new ItemStack(chestMaterial);
        ItemMeta meta = backpack.getItemMeta();

        if (meta != null) {
            String sizeName = getSizeName(level);
            int slots = getSlotsForLevel(level);

            meta.setDisplayName("§6" + sizeName + " Backpack");
            meta.setLore(Arrays.asList(
                "§7Right-click to open your backpack",
                "§7Items are stored persistently in the item",
                "§7Can be given to other players",
                "§7Size: " + slots + " slots (" + (slots / 9) + " rows)",
                "§8§oIn The Back"
            ));

            // Mark this item as a backpack and give it a unique ID and level
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(BACKPACK_KEY, PersistentDataType.BOOLEAN, true);
            container.set(BACKPACK_ID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
            container.set(BACKPACK_LEVEL_KEY, PersistentDataType.INTEGER, level);

            backpack.setItemMeta(meta);
        }

        return backpack;
    }

    /**
     * Creates a basic small backpack (for backward compatibility)
     * @return ItemStack representing a small backpack
     */
    public static ItemStack createBackpack() {
        return createBackpack(0);
    }

    /**
     * Checks if an ItemStack is a backpack
     * @param item The item to check
     * @return true if the item is a backpack
     */
    public static boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(BACKPACK_KEY, PersistentDataType.BOOLEAN) &&
               container.get(BACKPACK_KEY, PersistentDataType.BOOLEAN);
    }

    /**
     * Gets the unique ID of a backpack
     * @param backpack The backpack item
     * @return The unique ID string, or null if not a backpack
     */
    public static String getBackpackId(ItemStack backpack) {
        if (!isBackpack(backpack)) {
            return null;
        }

        ItemMeta meta = backpack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.get(BACKPACK_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Gets the level of a backpack
     * @param backpack The backpack item
     * @return The level (0=small, 1=medium, 2=large, 3=huge), or -1 if not a backpack
     */
    public static int getBackpackLevel(ItemStack backpack) {
        if (!isBackpack(backpack)) {
            return -1;
        }

        ItemMeta meta = backpack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Default to level 0 for existing backpacks without level data
        Integer level = container.get(BACKPACK_LEVEL_KEY, PersistentDataType.INTEGER);
        return level != null ? level : 0;
    }

    /**
     * Gets the inventory size for a backpack level
     * @param level The backpack level
     * @return Number of slots
     */
    public static int getSlotsForLevel(int level) {
        switch (level) {
            case 0: return SMALL_BACKPACK_SIZE;
            case 1: return MEDIUM_BACKPACK_SIZE;
            case 2: return LARGE_BACKPACK_SIZE;
            case 3: return HUGE_BACKPACK_SIZE;
            default: return SMALL_BACKPACK_SIZE;
        }
    }

    /**
     * Gets the display name for a backpack level
     * @param level The backpack level
     * @return Display name
     */
    public static String getSizeName(int level) {
        switch (level) {
            case 0: return "Small";
            case 1: return "Medium";
            case 2: return "Large";
            case 3: return "Huge";
            default: return "Small";
        }
    }

    /**
     * Gets the number of slots in a backpack
     * @param backpack The backpack item
     * @return Number of slots, or 27 if not a backpack
     */
    public static int getBackpackSlots(ItemStack backpack) {
        int level = getBackpackLevel(backpack);
        return getSlotsForLevel(level);
    }

    /**
     * Saves inventory contents to a backpack item using serialization
     * @param backpack The backpack item
     * @param inventoryContents The inventory contents to save
     */
    public static void saveInventory(ItemStack backpack, ItemStack[] inventoryContents) {
        if (!isBackpack(backpack) || inventoryContents == null) {
            return;
        }

        try {
            // Serialize the inventory contents to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the length first
            dataOutput.writeInt(inventoryContents.length);

            // Write each item
            for (ItemStack item : inventoryContents) {
                if (item != null && item.getType() != Material.AIR) {
                    dataOutput.writeBoolean(true); // has item
                    dataOutput.writeObject(item);
                } else {
                    dataOutput.writeBoolean(false); // empty slot
                }
            }

            dataOutput.close();
            byte[] inventoryData = outputStream.toByteArray();

            // Save to the backpack's persistent data
            ItemMeta meta = backpack.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, inventoryData);
                backpack.setItemMeta(meta);
            }

        } catch (Exception e) {
            // Log error but don't crash
            System.err.println("Failed to save backpack inventory: " + e.getMessage());
        }
    }

    /**
     * Loads inventory contents from a backpack item using deserialization
     * @param backpack The backpack item
     * @return ItemStack array representing the inventory contents
     */
    public static ItemStack[] loadInventory(ItemStack backpack) {
        int slots = getBackpackSlots(backpack);
        ItemStack[] contents = new ItemStack[slots];

        if (!isBackpack(backpack)) {
            return contents;
        }

        ItemMeta meta = backpack.getItemMeta();
        if (meta == null) {
            return contents;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(INVENTORY_KEY, PersistentDataType.BYTE_ARRAY)) {
            return contents;
        }

        try {
            byte[] inventoryData = container.get(INVENTORY_KEY, PersistentDataType.BYTE_ARRAY);
            if (inventoryData == null || inventoryData.length == 0) {
                return contents;
            }

            // Deserialize the inventory contents
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inventoryData);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int length = dataInput.readInt();
            int maxSlots = Math.min(length, contents.length);

            for (int i = 0; i < maxSlots; i++) {
                boolean hasItem = dataInput.readBoolean();
                if (hasItem) {
                    ItemStack item = (ItemStack) dataInput.readObject();
                    contents[i] = item;
                }
                // If no item, slot remains null (empty)
            }

            dataInput.close();

        } catch (Exception e) {
            // Log error but return empty inventory
            System.err.println("Failed to load backpack inventory: " + e.getMessage());
            // Return empty contents array
        }

        return contents;
    }

    /**
     * Upgrades a backpack to the next level while preserving contents
     * @param backpack The backpack to upgrade
     * @return true if upgrade was successful, false otherwise
     */
    public static boolean upgradeBackpack(ItemStack backpack) {
        if (!isBackpack(backpack)) {
            return false;
        }

        int currentLevel = getBackpackLevel(backpack);
        if (currentLevel >= MAX_BACKPACK_LEVEL) {
            return false; // Already at max level
        }

        int newLevel = currentLevel + 1;

        // Load current contents
        ItemStack[] currentContents = loadInventory(backpack);

        // Change the backpack material to match the new level
        Material newMaterial = getChestMaterialForLevel(newLevel);
        backpack.setType(newMaterial);

        // Update the backpack's metadata
        ItemMeta meta = backpack.getItemMeta();
        if (meta != null) {
            String sizeName = getSizeName(newLevel);
            int slots = getSlotsForLevel(newLevel);

            meta.setDisplayName("§6" + sizeName + " Backpack");
            meta.setLore(Arrays.asList(
                "§7Right-click to open your backpack",
                "§7Items are stored persistently in the item",
                "§7Can be given to other players",
                "§7Size: " + slots + " slots (" + (slots / 9) + " rows)",
                "§8§oIn The Back"
            ));

            // Update level
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(BACKPACK_LEVEL_KEY, PersistentDataType.INTEGER, newLevel);

            backpack.setItemMeta(meta);

            // Save the contents (they will be truncated or expanded as needed)
            saveInventory(backpack, currentContents);

            return true;
        }

        return false;
    }
}
