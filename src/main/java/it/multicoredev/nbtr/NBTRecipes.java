package it.multicoredev.nbtr;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.json.TypeAdapter;
import it.multicoredev.nbtr.model.recipes.RecipeWrapper;
import it.multicoredev.nbtr.utils.MaterialAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * BSD 3-Clause License
 * <p>
 * Copyright (c) 2023, Lorenzo Magni
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class NBTRecipes extends JavaPlugin {
    private static final GsonHelper GSON = new GsonHelper(new TypeAdapter(Material.class, new MaterialAdapter()));
    private Config config;
    private final File recipesDir = new File(getDataFolder(), "recipes");
    private final List<RecipeWrapper> recipes = new ArrayList<>();
    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();
    private final Metrics metrics = new Metrics(this, 17319);

    //TODO Allow the use of tags ?
    //TODO Add recipes that need more than one item per slot ?
    //TODO Add recipe editor GUI ?

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
        registerRecipes();

        NBTRCommand cmd = new NBTRCommand(this);
        getCommand("nbtr").setExecutor(cmd);
        getCommand("nbtr").setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        registeredRecipes.forEach(getServer()::removeRecipe);
        registeredRecipes.clear();
        recipes.clear();
    }

    public Config config() {
        return config;
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
                RecipeWrapper recipe = GSON.load(file, RecipeWrapper.class);
                if (recipe == null) continue;
                if (!recipe.isValid()) {
                    Chat.warning("&eRecipe " + file.getName() + " is not valid");
                    continue;
                }

                recipe.init(this, file.getName().toLowerCase().replace(".json", ""));
                recipes.add(recipe);
            } catch (Exception e) {
                Chat.warning("&eLoading of recipe " + file.getName() + " failed with error: " + e.getMessage());
            }
        }

        Chat.info("&bLoaded " + recipes.size() + " recipes");
    }

    private void registerRecipes() {
        recipes.forEach(recipe -> {
            getServer().addRecipe(recipe.toBukkit());
            registeredRecipes.add(recipe.getKey());
        });
    }
}
