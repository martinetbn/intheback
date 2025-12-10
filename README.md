# In The Back

A Minecraft Paper plugin that adds craftable backpack functionality to your server.

## Features

- **Craftable Backpacks**: Players can craft backpacks using leather and chests
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

## Usage

1. **Craft a Backpack**: Use the crafting recipe above
2. **Open Backpack**: Right-click while holding the backpack in your main hand
3. **Store Items**: Place items in the 27-slot inventory
4. **Close Inventory**: Items are automatically saved when you close the inventory
5. **Share Backpacks**: Give backpacks to other players - they keep their items!

## Technical Details

- **Minecraft Version**: 1.20+
- **API**: Paper API
- **Storage**: Uses Minecraft's persistent data containers with proper serialization
- **Unique IDs**: Each backpack has a unique identifier to prevent conflicts

## Permissions

No special permissions required - any player can craft and use backpacks.

## Support

If you encounter any issues or have suggestions, please open an issue on GitHub.

---

*This plugin is open source and available under the MIT License.*
