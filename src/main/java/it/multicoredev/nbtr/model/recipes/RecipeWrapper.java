package it.multicoredev.nbtr.model.recipes;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import it.multicoredev.nbtr.model.DiscoverTrigger;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;

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
@JsonAdapter(RecipeWrapper.Adapter.class)
public abstract class RecipeWrapper {
    protected String type;
    @SerializedName("discover")
    protected DiscoverTrigger discoverTrigger;
    protected transient NamespacedKey namespacedKey;

    public RecipeWrapper(Type type) {
        this.type = type.getType();
    }

    public Type getType() {
        return Type.getFromString(type);
    }

    public DiscoverTrigger getDiscoverTrigger() {
        return discoverTrigger;
    }

    public void init(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        if (discoverTrigger != null) discoverTrigger.init();
    }

    public NamespacedKey getKey() {
        return namespacedKey;
    }

    public abstract Recipe toBukkit();

    public abstract boolean isValid();

    public enum Type {
        SHAPED("crafting_shaped", ShapedRecipeWrapper.class),
        SHAPELESS("crafting_shapeless", ShapelessRecipeWrapper.class),
        SMELTING("smelting", SmeltingRecipeWrapper.class),
        BLASTING("blasting", BlastingRecipeWrapper.class),
        SMOKING("smoking", SmokingRecipeWrapper.class),
        CAMPFIRE("campfire_cooking", CampfireRecipeWrapper.class),
        SMITHING_RECIPE("smithing", SmithingRecipeWrapper.class);

        private final String type;
        private final Class<? extends RecipeWrapper> clazz;

        Type(String type, Class<? extends RecipeWrapper> clazz) {
            this.type = type;
            this.clazz = clazz;
        }

        public String getType() {
            return type;
        }

        public Class<? extends RecipeWrapper> getRecipeClass() {
            return clazz;
        }

        public static Type getFromString(String type) {
            for (Type t : Type.values()) {
                if (t.getType().equalsIgnoreCase(type)) {
                    return t;
                }
            }
            return null;
        }
    }

    public static class Adapter implements JsonSerializer<RecipeWrapper>, JsonDeserializer<RecipeWrapper> {

        @Override
        public RecipeWrapper deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Expected JsonObject but found " + json.getClass().getSimpleName() + ".");

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("type")) throw new JsonParseException("Required field \"type\" has not been specified. Must be one of " + Arrays.toString(Type.values()));

            Type t = Type.getFromString(obj.get("type").getAsString());
            if (t == null) throw new JsonParseException("Required field \"type\" is not a valid recipe type. Must be one of " + Arrays.toString(Type.values()));

            return ctx.deserialize(json, t.getRecipeClass());
        }

        @Override
        public JsonElement serialize(RecipeWrapper recipe, java.lang.reflect.Type type, JsonSerializationContext ctx) {
            return ctx.serialize(recipe, type);
        }
    }
}
