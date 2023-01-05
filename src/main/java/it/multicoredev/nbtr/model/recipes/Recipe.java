package it.multicoredev.nbtr.model.recipes;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
@JsonAdapter(Recipe.Adapter.class)
public abstract class Recipe {
    protected String type;
    protected transient String id;
    protected transient Plugin plugin;

    public Recipe(Type type) {
        this.type = type.getType();
    }

    public Type getType() {
        return Type.getFromString(type);
    }

    public void init(Plugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public abstract ItemStack getResult();

    public abstract boolean isValid();

    public abstract void prepare();

    public enum Type {
        SHAPED("crafting_shaped", ShapedRecipe.class),
        SHAPELESS("crafting_shapeless", ShapelessRecipe.class),
        SMELTING("smelting", SmeltingRecipe.class),
        BLASTING("blasting", BlastingRecipe.class),
        SMOKING("smoking", SmokingRecipe.class),
        //CAMPFIRE("campfire_cooking", CampfireRecipe.class),
        SMITHING_RECIPE("smithing", SmithingRecipe.class),
        BREWING_RECIPE("brewing", BrewingRecipe.class);

        private final String type;
        private final Class<? extends Recipe> clazz;

        Type(String type, Class<? extends Recipe> clazz) {
            this.type = type;
            this.clazz = clazz;
        }

        public String getType() {
            return type;
        }

        public Class<? extends Recipe> getRecipeClass() {
            return clazz;
        }

        public static Type getFromString(String type) {
            for (Type t : Type.values()) {
                if (t.getType().equals(type)) {
                    return t;
                }
            }
            return null;
        }
    }

    public static class Adapter implements JsonSerializer<Recipe>, JsonDeserializer<Recipe> {

        @Override
        public Recipe deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Recipe must be an object");

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("type")) throw new JsonParseException("Recipe must have a type");

            Type t = Type.getFromString(obj.get("type").getAsString());
            if (t == null) throw new JsonParseException("Invalid recipe type");

            return ctx.deserialize(json, t.getRecipeClass());
        }

        @Override
        public JsonElement serialize(Recipe recipe, java.lang.reflect.Type type, JsonSerializationContext ctx) {
            return ctx.serialize(recipe, type);
        }
    }
}
