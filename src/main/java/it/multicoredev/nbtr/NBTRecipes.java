package it.multicoredev.nbtr;

import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.json.TypeAdapter;
import it.multicoredev.nbtr.listeners.OnInventoryChange;
import it.multicoredev.nbtr.listeners.OnPlayerJoin;
import it.multicoredev.nbtr.model.recipes.RecipeWrapper;
import it.multicoredev.nbtr.utils.MaterialAdapter;
import it.multicoredev.nbtr.utils.RecipeChoiceAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static final GsonHelper GSON = new GsonHelper(
            new TypeAdapter(Material.class, new MaterialAdapter()),
            new TypeAdapter(RecipeChoice.class, new RecipeChoiceAdapter())
    );
    private static final String ALLOWED_NAMESPACE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789/_-";
    private Config config;
    private final File recipesDir = new File(getDataFolder(), "recipes");
    private final List<RecipeWrapper> recipes = new ArrayList<>();
    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();
    private final Metrics metrics = new Metrics(this, 17319);
    private String namespace;

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

        namespace = getNamespace();

        loadRecipes(recipesDir);
        Chat.info("&bLoaded " + recipes.size() + " recipes");

        registerRecipes();

        getServer().getPluginManager().registerEvents(new OnInventoryChange(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(this), this);

        NBTRCommand cmd = new NBTRCommand(this);
        getCommand("nbtr").setExecutor(cmd);
        getCommand("nbtr").setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        registeredRecipes.forEach(getServer()::removeRecipe);
        registeredRecipes.clear();
        recipes.clear();

        HandlerList.unregisterAll(this);
    }

    public Config config() {
        return config;
    }

    public List<RecipeWrapper> getRecipes() {
        return recipes;
    }

    private void loadRecipes(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadRecipes(file);
            } else {
                if (!file.getName().toLowerCase().endsWith(".json")) continue;

                try {
                    RecipeWrapper recipe = GSON.load(file, RecipeWrapper.class);
                    if (recipe == null) continue;
                    if (!recipe.isValid()) {
                        Chat.warning("&eRecipe " + file.getName() + " is not valid");
                        continue;
                    }

                    recipe.init(getNamespacedKey(file));
                    recipes.add(recipe);
                } catch (Exception e) {
                    Chat.warning("&eLoading of recipe " + file.getName() + " failed with error: " + e.getMessage());
                }
            }
        }
    }

    private String getNamespace() {
        String namespace = config().namespace;
        if (namespace == null || namespace.trim().isEmpty()) namespace = getName().toLowerCase(Locale.ROOT);
        namespace = namespace.replaceAll("[^" + ALLOWED_NAMESPACE_CHARS + "]", "");

        if (namespace.trim().isEmpty()) throw new IllegalArgumentException("Namespace must contain at least one alphanumeric character");
        return namespace;
    }

    private void registerRecipes() {
        recipes.forEach(recipe -> {
            try {
                getServer().addRecipe(recipe.toBukkit());
                registeredRecipes.add(recipe.getKey());
            } catch (Exception e) {
                Chat.warning("&eRecipe '" + recipe.getKey().toString() + "' registration failed: " + e.getMessage());
            }
        });
    }

    private String getRelativePath(File file) throws IllegalArgumentException {
        try {
            String parentPath = recipesDir.getPath().replace("\\", "/");
            String filePath = file.getParentFile().getPath().replace("\\", "/");

            if (!filePath.startsWith(parentPath)) throw new IllegalArgumentException("File must be inside the scripts root directory");

            String relativePath = filePath.substring(parentPath.length());
            if (relativePath.startsWith("/")) relativePath = relativePath.substring(1);

            return relativePath;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("deprecation")
    private NamespacedKey getNamespacedKey(File file) {
        String key = getRelativePath(file) + "/" + file.getName().toLowerCase(Locale.ROOT).replace(".json", "");
        if (key.contains(" ")) key = key.replace(" ", "_");
        key = key.replaceAll("[^" + ALLOWED_NAMESPACE_CHARS + "]", "");

        if (key.trim().isEmpty()) throw new IllegalArgumentException("File name must contain at least one alphanumeric character");

        return new NamespacedKey(namespace, key);
    }
}
