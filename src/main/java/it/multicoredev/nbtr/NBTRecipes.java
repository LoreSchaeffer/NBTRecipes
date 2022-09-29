package it.multicoredev.nbtr;

import it.multicoredev.nbtr.listeners.PrepareItemCraftListener;
import it.multicoredev.nbtr.model.recipes.Recipe;
import it.multicoredev.nbtr.model.recipes.ShapedRecipe;
import it.multicoredev.nbtr.model.recipes.ShapelessRecipe;
import it.multicoredev.nbtr.utils.MaterialAdapter;
import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.json.TypeAdapter;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static final GsonHelper gson = new GsonHelper(new TypeAdapter(Material.class, new MaterialAdapter()));
    private Config config;
    private final File recipesDir = new File(getDataFolder(), "recipes");
    private final List<Recipe> recipes = new ArrayList<>();

    @Override
    public void onEnable() {
        try {
            if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
                if (!getDataFolder().mkdirs()) throw new IOException("Cannot create plugin folder");
            }

            if (!recipesDir.exists() || !recipesDir.isDirectory()) {
                if (!recipesDir.mkdirs()) throw new IOException("Cannot create recipes folder");
            }

            config = gson.autoload(new File(getDataFolder(), "config.json"), new Config().init(), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            onDisable();
            return;
        }

        loadRecipes();

        getServer().getPluginManager().registerEvents(new PrepareItemCraftListener(this), this);

        NBTRCommand cmd = new NBTRCommand(this);
        getCommand("nbtr").setExecutor(cmd);
        getCommand("nbtr").setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        recipes.clear();
    }

    public Config config() {
        return config;
    }

    public List<ShapedRecipe> getShapedRecipes() {
        return recipes.stream().filter(recipe -> recipe instanceof ShapedRecipe).map(recipe -> (ShapedRecipe) recipe).collect(Collectors.toList());
    }

    public List<ShapelessRecipe> getShapelessRecipes() {
        return recipes.stream().filter(recipe -> recipe instanceof ShapelessRecipe).map(recipe -> (ShapelessRecipe) recipe).collect(Collectors.toList());
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
                Recipe recipe = gson.load(file, Recipe.class);
                if (recipe == null) continue;

                recipe.prepare();
                recipes.add(recipe);
            } catch (Exception e) {
                Chat.warning("&eLoading of recipe " + file.getName() + " failed with error: " + e.getMessage());
            }
        }

        Chat.info("&bLoaded " + recipes.size() + " recipes");
    }
}
