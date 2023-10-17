# NBTRecipes
<a href="https://modrinth.com/plugin/nbtrecipes/"><img alt="modrinth" height="44" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
<a href="https://www.spigotmc.org/resources/nbtrecipes.107230/"><img alt="spigotmc" height="44" src="badges/spigotmc_vector.svg"></a>
<a href="https://hangar.papermc.io/LoreSchaeffer/NBTRecipes"><img alt="hangar" height="44" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg"></a>
<a href="https://legacy.curseforge.com/minecraft/bukkit-plugins/nbtrecipes"><img alt="curseforge" height="44" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg"></a>

Plugin designed to simplify creation of custom recipes, without the need writing any code or touching datapacks.
You can attach common data (amount, name, lore) as well as NBT tags to items processed by the recipes.
Format is very similar to the one used by datapacks.

<br />

## Usage
You can place your recipes inside the `recipes` folder in the plugin folder.
If you want to organize your recipes in subfolders you can do it, the plugin will search for recipes in every subfolder of the `recipes` folder.
The plugin will automatically load all the recipes in the subfolders and add their relative path to the recipe namespaced key.

To create a recipe just create a text file with the `.json` extension and put it in the `recipes` folder.
Edit the file with your favorite text editor and put the recipe in it as shown in the examples below.

<br />

### Config
In the config you can change the namespace of your recipes and all the messages of the plugin.
The namespace can only contain the following characters: `a-z`, `0-9`, `_`, `-`, `/`.

<br />

### Commands
* `/nbtrecipes reload` - Reloads the recipes and the config file.
  * `nbtr.command` Permission needed to use the command.
* `/nbtrecipes list` - Lists all the recipes added by this plugin.
  * `nbtr.command` Permission needed to use the command.

<br />

## Examples
Examples and more detailed description of plugin components.

<br />

### Item
This is an object that represents an item in the recipes, it can be used as an ingredient or as a result. Majority of fields are optional, in fact, the only one needed is the `material`.

When only `material` field is defined, and no other items within the choice requires any metadata (name, lore, nbt), items in the recipe are matched by material.

*See below examples for more details.*

<br />

### Discover Trigger
Recipe trigger can make any recipe to be discoverable by players, by picking up an item.
Field `discover` is optional and if not specified, recipe will be discovered and visible by default.

*See below examples for more details.*

<br />

### Shaped Recipe
Shaped recipe applies to crafting table and inventory crafting.

<details>
  <summary><b>Click here to expand/collapse JSON example.</b></summary>

```json5
{
  "type": "crafting_shaped",
  // Crafting pattern. Array must consist of either:
  // - two, two-character elements reflecting an inventory crafting grid.
  // - three, three-character elements reflecting a crafting table grid.
  "pattern": [
    "  D",
    " D ",
    "S  "
  ],
  // Key to the pattern.
  "key": {
    "S": [
      // Multiple item choices can be specified for one ingredient.
      // In case metadata (name/lore/nbt) is attached to an item, all choices are matched as EXACT.
      { "material": "stick" },
      { "material": "blaze_rod" }
    ],
    // 
    "D": { "material": "diamond" }
  },
  // Recipe result.
  "result": {
    "material": "diamond_sword",
    "amount": 1,
    "name": "Diagonally Crafted Diamond Sword",
    "lore": [
      "As the name suggests..."
    ],
    "nbt": "{CustomModelData: 2}"
  },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items to be picked-up before this recipe is "discovered" by the player.
    "items": [
      { "material": "diamond" }
    ]
  }
}
```

Field `discover` is optional.

</details>

<br />

### Shapeless Recipe
Shapeless recipe applies to crafting table and inventory crafting.

<details>
  <summary><b>Click here to expand/collapse JSON example.</b></summary>

```json5
{
  "type": "crafting_shapeless",
  // Crafting ingredients.
  "ingredients": [
    // Multiple item choices can be specified for one ingredient.
    // In case metadata (name/lore/nbt) is attached to an item, all choices are matched as EXACT.
    [
      { "material": "oak_log" },
      { "material": "spruce_log" },
      { "material": "birch_log" },
      { "material": "jungle_log" },
      { "material": "acacia_log" },
      { "material": "dark_oak_log" },
      { "material": "mangrove_log" },
      { "material": "cherry_log" }
    ],
    { "material": "flint_and_steel" }
  ],
  // Recipe result.
  "result": { "material": "charcoal" },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items to be picked-up before this recipe is "discovered" by the player.
    "items": [
      { "material": "oak_log" },
      { "material": "spruce_log" },
      { "material": "birch_log" },
      { "material": "jungle_log" },
      { "material": "acacia_log" },
      { "material": "dark_oak_log" },
      { "material": "mangrove_log" },
      { "material": "cherry_log" }
    ]
  }
}
```

Field `discover` is optional.

</details>

<br />

### Smelting Recipes
Smelting recipes can be applied to regular furnace, blast furnace, smoker or campfire.

<details>
  <summary><b>Click here to expand/collapse JSON example.</b></summary>

```json5
{
  // Recipe type. For furnace recipes you can use one of: [SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING]
  "type": "smelting",
  // Furnace input.
  "input": [
    // Multiple item choices can be specified for one ingredient.
    // In case metadata (name/lore/nbt) is attached to an item, all choices are matched as EXACT.
    { "material": "diamond_helmet" },
    { "material": "diamond_chestplate" },
    { "material": "diamond_leggings" },
    { "material": "diamond_boots" }
  ],
  // Recipe result.
  "result": { "material": "diamond" },
  // Experience to award player after taking smelting result. Optional.
  "experience": 0.7,
  // Time it takes to cook this recipe. Measured in ticks. Optional.
  "cooking_time": 200,
  // Recipe discover trigger. Optional.
  "discover": {
    // Items to be picked-up before this recipe is "discovered" by the player.
    "items": [
      { "material": "diamond_helmet" },
      { "material": "diamond_chestplate" },
      { "material": "diamond_leggings" },
      { "material": "diamond_boots" }
    ]
  }
}
```
All furnace recipe types follow the same schema.
- `smelting` - recipe for regular furnace.
- `blasting` - recipe for blast furnace.
- `smoking` - recipe for smoker.
- `campfire_cooking` - recipe for campfire.

Fields `experience`, `cooking_time` and `discover` are optional.

</details>

<br />

### Smithing Recipe
Smithing recipe applies to smithing table.

<details>
  <summary><b>Click here to expand/collapse JSON example.</b></summary>

```json5
{
  "type": "smithing",
  // Base item, you can think of it as an item which upgrades (could) be applied to. More than one item choice can be specified.
  "base": { "material": "iron_pickaxe" },
  // Template item, you can think of it as an upgrade which is applied to the base item. More than one item choice can be specified.
  // This field works only when running 1.20 or higher.
  "template": { "material": "air" },
  // Addition item. For vanilla recipes, it's usually a trim material. More than one item choice can be specified.
  "addition": { "material": "diamond" },
  // Recipe result. Metadata is not supported as it's copied directly from the base item.
  "result": { "material": "diamond_pickaxe" },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items to be picked-up before this recipe is "discovered" by the player.
    "items": [
      { "material": "iron_pickaxe" }
    ]
  }
}
``` 

Metadata (name, lore, nbt) is not supported for result items, as it's copied directly from the base item.

Field `discover` is optional.

</details>

<br />

## Contributing

To contribute to this repository just fork this repository make your changes or add your code and make a pull request.
If you find an error or a bug you can open an issue [here](https://github.com/LoreSchaeffer/NBTRecipes/issues).

<br />

## License

NBTRecipes is released under "The 3-Clause BSD License". You can find a copy [here](https://github.com/LoreSchaeffer/NBTRecipes/blob/master/LICENSE).
