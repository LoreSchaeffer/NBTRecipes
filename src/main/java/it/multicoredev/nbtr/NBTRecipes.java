package it.multicoredev.nbtr;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.json.TypeAdapter;
import it.multicoredev.nbtr.model.recipes.*;
import it.multicoredev.nbtr.utils.MaterialAdapter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright © 2022 by Lorenzo Magni
 * This file is part of NBTRecipes.
 * NBTRecipes is under "The 3-Clause BSD License", you can find a copy <a href="https://opensource.org/licenses/BSD-3-Clause">here</a>.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
public class NBTRecipes extends JavaPlugin {
    private static final GsonHelper GSON = new GsonHelper(new TypeAdapter(Material.class, new MaterialAdapter()));
    private Config config;
    private final File recipesDir = new File(getDataFolder(), "recipes");
    private final Map<Recipe.Type, List<Recipe>> recipes = new HashMap<>();
    private final List<String> registeredRecipes = new ArrayList<>();

    //TODO Allow the use of tags
    //TODO Add recipes to recipe book
    //TODO Add recipes that need more than one item per slot
    //TODO Add recipe editor GUI

    @Override
    public void onEnable() {
        try {
            if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
                if (!getDataFolder().mkdirs()) throw new IOException("Cannot create plugin folder");
            }

            if (!recipesDir.exists() || !recipesDir.isDirectory()) {
                if (!recipesDir.mkdirs()) throw new IOException("Cannot create recipes folder");
            }

            config = GSON.autoload(new File(getDataFolder(), "config.json"), new Config().init(), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            onDisable();
            return;
        }

        loadRecipes();

        //getServer().getPluginManager().registerEvents(new PrepareItemCraftListener(this), this);
        //getServer().getPluginManager().registerEvents(new PrepareSmithingListener(this), this);
        //getServer().getPluginManager().registerEvents(new BrewListener(this), this);

        registerRecipes();

        NBTRCommand cmd = new NBTRCommand(this);
        getCommand("nbtr").setExecutor(cmd);
        getCommand("nbtr").setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        registeredRecipes.forEach(recipe -> getServer().removeRecipe(new NamespacedKey(this, recipe)));
        registeredRecipes.clear();
        recipes.clear();
    }

    public Config config() {
        return config;
    }

    public <T> List<T> getRecipes(Recipe.Type type) {
        return recipes.get(type).stream().map(r -> (T) r).collect(Collectors.toList());
    }

    private void loadRecipes() {
        File[] files = recipesDir.listFiles();
        if (files == null || files.length == 0) {
            Chat.info("&bLoaded 0 recipes");
            return;
        }

        for (File file : files) {
            if (!file.getName().toLowerCase().endsWith(".json")) continue;

            try {
                Recipe recipe = GSON.load(file, Recipe.class);
                if (recipe == null) continue;
                if (!recipe.isValid()) {
                    Chat.warning("&eRecipe " + file.getName() + " is not valid");
                    continue;
                }

                recipe.init(this, file.getName().toLowerCase().replace(".json", ""));
                recipe.prepare();

                if (recipes.containsKey(recipe.getType())) {
                    recipes.get(recipe.getType()).add(recipe);
                } else {
                    List<Recipe> list = new ArrayList<>();
                    list.add(recipe);
                    recipes.put(recipe.getType(), list);
                }
            } catch (Exception e) {
                Chat.warning("&eLoading of recipe " + file.getName() + " failed with error: " + e.getMessage());
            }
        }

        for (Recipe.Type type : recipes.keySet()) {
            Chat.info("&bLoaded " + recipes.get(type).size() + " " + type.name().toLowerCase() + " recipes");
        }
    }

    private void registerRecipes() {
        List<ShapedRecipe> shapedRecipes = getRecipes(Recipe.Type.SHAPED);
        for (ShapedRecipe recipe : shapedRecipes) {
            getServer().addRecipe(recipe.getBukkitRecipe());
        }

        List<SmeltingRecipe> furnaceRecipes = getRecipes(Recipe.Type.SMELTING);
        for (SmeltingRecipe recipe : furnaceRecipes) {
            String id = recipe.getResult().getType().getKey().toString().replace(":", "_") +
                    "_smelting_from_" +
                    recipe.getInput().getType().getKey().toString().replace(":", "_");
            registeredRecipes.add(id);


            getServer().addRecipe(new org.bukkit.inventory.FurnaceRecipe(
                    new NamespacedKey(this, id),
                    recipe.getResult(),
                    new RecipeChoice.ExactChoice(recipe.getInput()),
                    recipe.getExperience(),
                    recipe.getCookingTime()
            ));
        }

        List<BlastingRecipe> blastingRecipes = getRecipes(Recipe.Type.BLASTING);
        for (BlastingRecipe recipe : blastingRecipes) {
            String id = recipe.getResult().getType().getKey().toString().replace(":", "_") +
                    "_blasting_from_" +
                    recipe.getInput().getType().getKey().toString().replace(":", "_");
            registeredRecipes.add(id);


            getServer().addRecipe(new org.bukkit.inventory.BlastingRecipe(
                    new NamespacedKey(this, id),
                    recipe.getResult(),
                    new RecipeChoice.ExactChoice(recipe.getInput()),
                    recipe.getExperience(),
                    recipe.getCookingTime()
            ));
        }

        List<SmokingRecipe> smokingRecipes = getRecipes(Recipe.Type.SMOKING);
        for (SmokingRecipe recipe : smokingRecipes) {
            String id = recipe.getResult().getType().getKey().toString().replace(":", "_") +
                    "_smoking_from_" +
                    recipe.getInput().getType().getKey().toString().replace(":", "_");
            registeredRecipes.add(id);


            getServer().addRecipe(new org.bukkit.inventory.SmokingRecipe(
                    new NamespacedKey(this, id),
                    recipe.getResult(),
                    new RecipeChoice.ExactChoice(recipe.getInput()),
                    recipe.getExperience(),
                    recipe.getCookingTime()
            ));
        }
    }
}
