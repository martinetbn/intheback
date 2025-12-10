# In The Back

A Minecraft Paper plugin that adds craftable backpack functionality to your server.

## Features

- **Craftable Backpacks**: Players can craft backpacks using leather and chests
- **Upgradable Storage**: Backpacks can be upgraded from Small (27 slots) to Medium (36 slots), Large (45 slots), and Huge (54 slots)
- **Persistent Storage**: Items are stored directly in the backpack item itself
- **Multi-Backpack Support**: Each backpack has unique storage - no conflicts between multiple backpacks
- **Player Trading**: Backpacks can be safely given to other players without losing items
- **Full Item Support**: Preserves enchantments, custom names, durability, and all item properties

## Installation

1. Download the latest release JAR file
2. Place it in your server's `plugins/` folder
3. Restart your server
4. The plugin will automatically register the crafting recipe

## Crafting Recipe

Backpacks are crafted with this simple recipe:

```
L L L
L C L
L L L
```

Where:
- **L** = Leather
- **C** = Chest

## Upgrade Recipes

### Medium Backpack Upgrade
```
I I I
I L I
I I I
```
Where:
- **I** = Iron Ingot
- **L** = Leather

### Large Backpack Upgrade
```
G G G
G L G
G G G
```
Where:
- **G** = Gold Ingot
- **L** = Leather

### Huge Backpack Upgrade
```
D D D
D L D
D D D
```
Where:
- **D** = Diamond
- **L** = Leather

### Upgrading Backpacks

To upgrade a backpack, place it together with the appropriate upgrade item in a 2x2 or 3x3 crafting grid. The result will be the upgraded backpack with all items preserved.

## Usage

1. **Craft a Backpack**: Use the crafting recipe above to create a Small Backpack (27 slots)
2. **Open Backpack**: Right-click while holding the backpack in your main hand
3. **Store Items**: Place items in the inventory (size depends on backpack level)
4. **Upgrade Backpacks**: Craft upgrade items and combine them with backpacks in a crafting table
5. **Close Inventory**: Items are automatically saved when you close the inventory
6. **Share Backpacks**: Give backpacks to other players - they keep their items!

## Backpack Sizes

- **Small Backpack**: 27 slots (3 rows) - Chest appearance - Basic crafting recipe
- **Medium Backpack**: 36 slots (4 rows) - Waxed Copper Chest appearance - Upgrade with Iron Upgrade
- **Large Backpack**: 45 slots (5 rows) - Waxed Weathered Copper Chest appearance - Upgrade with Gold Upgrade
- **Huge Backpack**: 54 slots (6 rows) - Waxed Oxidized Copper Chest appearance - Upgrade with Diamond Upgrade

## Technical Details

- **Minecraft Version**: 1.21.10+
- **API**: Paper API
- **Storage**: Uses Minecraft's persistent data containers with proper serialization
- **Unique IDs**: Each backpack has a unique identifier to prevent conflicts

## Permissions

No special permissions required - any player can craft and use backpacks.

## Support

If you encounter any issues or have suggestions, please open an issue on GitHub.

---

*This plugin is open source and available under the MIT License.*
