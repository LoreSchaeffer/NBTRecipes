package it.multicoredev.nbtr.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import it.multicoredev.nbtr.model.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public final class RecipeChoiceAdapter implements JsonDeserializer<RecipeChoice> {

    @Override
    public RecipeChoice deserialize(final @NotNull JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        // Reading as single object.
        if (json.isJsonObject()) {
            if (json.getAsJsonObject().get("material") != null) {
                final Item item = context.deserialize(json, Item.class);
                // Throwing an exception if item validation fails.
                if (!item.isValid())
                    throw new JsonParseException("Required field \"material\" does not exist.");
                // Returning MaterialChoice if metadata is empty, or ExactChoice otherwise.
                return (!item.toItemStack().hasItemMeta())
                        ? new RecipeChoice.MaterialChoice(item.toItemStack().getType())
                        : new RecipeChoice.ExactChoice(item.toItemStack());
            } else if (json.getAsJsonObject().get("tag") != null) {
                String key = json.getAsJsonObject().get("tag").getAsString();
                // Stripping '#' char from the beginning, if present.
                if (key.charAt(0) == '#')
                    key = key.substring(1);
                // Throwing an exception if key is null or NamespacedKey validation fails.
                if (NamespacedKey.fromString(key) == null)
                    throw new JsonParseException("Required field \"key\" does not represent a valid namespaced key.");
                // Getting tag from the items registry.
                final Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, NamespacedKey.fromString(key), Material.class);
                // Throwing an exception if tag is null.
                if (tag == null) throw new JsonParseException("Required field \"key\" does not represent a valid item/material tag.");
                // Returning MaterialChoice if metadata is empty, or ExactChoice otherwise.
                return new RecipeChoice.MaterialChoice(tag);
            }
            // Throwing exception for unexpected input.
            throw new JsonParseException("Expected a JsonObject with either the \"material\" or \"tag\" property, but neither was found.");
        }
        // Reading as array of objects.
        else if (json.isJsonArray()) {
            final List<Item> items = context.deserialize(json, TypeToken.getParameterized(List.class, Item.class).getType());
            // Throwing an exception if validation of any item fails.
            if (!items.stream().allMatch(Item::isValid))
                throw new JsonParseException("Required field \"material\" does not exist on one or more elements.");
            // Returning MaterialChoice if metadata of all items is empty, or ExactChoice otherwise.
            return (items.stream().map(Item::toItemStack).noneMatch(ItemStack::hasItemMeta))
                    ? new RecipeChoice.MaterialChoice(items.stream().map(Item::toItemStack).map(ItemStack::getType).collect(Collectors.toList()))
                    : new RecipeChoice.ExactChoice(items.stream().map(Item::toItemStack).collect(Collectors.toList()));
        }
        // Throwing exception for unexpected input.
        throw new JsonParseException("Expected JsonObject or JsonArray but found " + json.getClass().getSimpleName() + ".");
    }

}
