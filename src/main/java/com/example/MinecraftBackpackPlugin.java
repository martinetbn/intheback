package com.example;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Minecraft Backpack Plugin - Main plugin class
 * A Paper plugin that adds backpack functionality to Minecraft
 */
public class MinecraftBackpackPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("In The Back has been enabled!");

        // Register event listeners
        getServer().getPluginManager().registerEvents(new BackpackListener(this), this);

        // Register crafting recipes
        BackpackRecipe.registerRecipe();
        BackpackUpgrade.registerUpgradeRecipes();

        getLogger().info("Backpack and upgrade crafting recipes registered!");
    }

    @Override
    public void onDisable() {
        // Unregister crafting recipes
        BackpackRecipe.unregisterRecipe();
        BackpackUpgrade.unregisterUpgradeRecipes();

        // Plugin shutdown logic
        getLogger().info("In The Back has been disabled!");
    }
}
