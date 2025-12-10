package com.example;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * Crafting recipe for backpack items
 */
public class BackpackRecipe {

    private static final NamespacedKey BACKPACK_RECIPE_KEY = new NamespacedKey("minecraft-backpack", "backpack_recipe");

    /**
     * Registers the backpack crafting recipe
     */
    public static void registerRecipe() {
        // Create the recipe
        ShapedRecipe recipe = new ShapedRecipe(BACKPACK_RECIPE_KEY, BackpackItem.createBackpack());

        // Set the crafting pattern (3x3 grid)
        recipe.shape(
            "LLL",
            "LCL",
            "LLL"
        );

        // Define the ingredients
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('C', Material.CHEST);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }

    /**
     * Unregisters the backpack crafting recipe
     */
    public static void unregisterRecipe() {
        Bukkit.removeRecipe(BACKPACK_RECIPE_KEY);
    }
}
