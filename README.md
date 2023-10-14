# NBTRecipes

#### A very simple plugin to add recipes that use NBT data.

## Description

With this plugin you can create recipes that check NBT data for ingredients.
It's very simple to use, the recipes for this plugin are very similar to vanilla ones.

You can find some examples later.

## Download

You can download this plugin on [Spigot](https://www.spigotmc.org/resources/nbtrecipes.107230/) or [Modrinth](https://modrinth.com/plugin/nbtrecipes).


### Commands
* `/nbtrecipes reload` - Reloads the recipes and the config file.
    * `nbtr.command` Permission needed to use the command.
* `/nbtrecipes list` - Lists all the recipes added by this plugin.
    * `nbtr.command` Permission needed to use the command.

## How to use
You can place your recipes inside the `recipes` folder in the plugin folder.
If you want to organize your recipes in subfolders you can do it, the plugin will search for recipes in every subfolder of the `recipes` folder.
The plugin will automatically load all the recipes in the subfolders and add their relative path to the recipe namespaced key.


To create a recipe just create a text file with the `.json` extension and put it in the `recipes` folder.
Edit the file with your favorite text editor and put the recipe in it as shown in the examples below.

### Config
In the config you can change the namespace of your recipes and all the messages of the plugin.
The namespace can only contain the following characters: `a-z`, `0-9`, `_`, `-`, `/`.

## Examples

### Item
<details>
<summary>Item json</summary>

```json
{
  "material": "minecraft:stone",
  "amount": 4,
  "name": "&bStone",
  "lore": [
    "&bThis is the first line",
    "&bThis is the second line"
  ],
  "nbt": "{CustomModelData:1}"
}
```

This is tha object that represents an item in the recipes, it can be used as an ingredient or as a result.
The majority of these fields are optional, the only one needed is the material.

</details>

### Discover Trigger
<details>
<summary>Recipe discovering</summary>
Every recipe registered by this plugin is automatically added to the recipe book unless you specify it.

You can add the <code>discover</code> tag to any recipe to make the recipe discoverable by the players by picking up an item.
Let's make an example:

```json
{
  "type": "crafting_shapeless",
  "ingredients": [
    {
      "material": "redstone"
    },
    {
      "material": "glowstone_dust"
    },
    {
      "material": "gunpowder"
    }
  ],
  "result": {
    "material": "diamond"
  },
  "discover": {
    "items": [
      {
        "material": "redstone"
      },
      {
        "material": "glowstone_dust"
      }
    ]
  }
}
```

In this example you can unlock this recipe picking up a redstone dust OR a glowstone dust. The list <code>items</code> contains Item objects described above.
</details>

### Shaped Recipe
<details>
  <summary>Shaped recipe json</summary>

```json
{
  "type": "crafting_shaped",
  "pattern": [
    "rrr",
    "gcg",
    "ppp"
  ],
  "key": {
    "r": {
      "material": "redstone"
    },
    "g": {
      "material": "glowstone_dust"
    },
    "p": {
      "material": "gunpowder"
    },
    "c": {
      "material": "knowledge_book",
      "nbt": "{CustomModelData: 1}"
    }
  },
  "result": {
    "material": "knowledge_book",
    "amount": 1,
    "name": "Mixed Powder",
    "lore": [
      "A mixture of gunpowder, glowstone and redstone",
      "that can be used to craft something"
    ],
    "nbt": "{CustomModelData: 2}"
  }
}
```

This can also be 2x2.

</details>

### Shapeless Recipe
<details>
  <summary>Shapeless recipe json</summary>

```json
{
  "type": "crafting_shapeless",
  "ingredients": [
    {
      "material": "redstone"
    },
    {
      "material": "glowstone_dust"
    },
    {
      "material": "gunpowder"
    }
  ],
  "result": {
    "material": "diamond"
  }
}
```

</details>

### Smelting Recipes
<details>
  <summary>Smelting recipe json</summary>

```json
{
  "type": "smelting",
  "input": {
    "material": "crimson_stem"
  },
  "result": {
    "material": "charcoal"
  },
  "experience": 0.7,
  "cooking_time": 200
}
```

This can be used for smelting, blasting, smoking and campfire cooking.
The classic furnace has type `smelting`, the blast furnace has type `blasting`, the smoker has type `smoking` and the campfire has type `campfire_cooking`.

`experience` and `cooking_time` are optional.

</details>

### Smithing Recipe
<details>
  <summary>Smithing recipe json</summary>

```json
{
  "type": "smithing",
  "base": {
    "material": "diamond_sword"
  },
  "addition": {
    "material": "nether_star"
  },
  "template": {
    "material": "glowstone_dust"
  },
  "result": {
    "material": "netherite_sword",
    "nbt": "{CustomModelData: 5}"
  }
}
```
<i>Template is only 1.20+</i> 
</details>

## Contributing

To contribute to this repository just fork this repository make your changes or add your code and make a pull request.
If you find an error or a bug you can open an issue [here](https://github.com/LoreSchaeffer/NBTRecipes/issues).

## License

NBTRecipes is released under "The 3-Clause BSD License". You can find a copy [here](https://github.com/LoreSchaeffer/NBTRecipes/blob/master/LICENSE).
