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
 */
public class BackpackItem {

    private static final NamespacedKey BACKPACK_KEY = new NamespacedKey("minecraft-backpack", "backpack");
    private static final NamespacedKey BACKPACK_ID_KEY = new NamespacedKey("minecraft-backpack", "backpack_id");
    private static final NamespacedKey INVENTORY_KEY = new NamespacedKey("minecraft-backpack", "inventory");

    /**
     * Creates a new backpack item with a unique ID
     * @return ItemStack representing a backpack
     */
    public static ItemStack createBackpack() {
        ItemStack backpack = new ItemStack(Material.CHEST);
        ItemMeta meta = backpack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6Backpack");
            meta.setLore(Arrays.asList(
                "§7Right-click to open your backpack",
                "§7Items are stored persistently in the item",
                "§7Can be given to other players",
                "§8§oIn The Back"
            ));

            // Mark this item as a backpack and give it a unique ID
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(BACKPACK_KEY, PersistentDataType.BOOLEAN, true);
            container.set(BACKPACK_ID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());

            backpack.setItemMeta(meta);
        }

        return backpack;
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
        ItemStack[] contents = new ItemStack[27]; // 3 rows × 9 slots

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
}
