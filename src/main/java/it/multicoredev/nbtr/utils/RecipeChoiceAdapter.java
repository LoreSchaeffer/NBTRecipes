package it.multicoredev.nbtr.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import it.multicoredev.nbtr.model.Item;
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
            final Item item = context.deserialize(json, Item.class);
            // Throwing an exception if item validation fails.
            if (!item.isValid())
                throw new JsonParseException("Required field \"material\" does not exist.");
            // Returning MaterialChoice if metadata is empty, or ExactChoice otherwise.
            return (!item.toItemStack().hasItemMeta())
                    ? new RecipeChoice.MaterialChoice(item.toItemStack().getType())
                    : new RecipeChoice.ExactChoice(item.toItemStack());
        }
        // Reading as array of objects.
        else if (json.isJsonArray()) {
            final List<Item> items = context.deserialize(json, TypeToken.getParameterized(List.class, Item.class).getType());
            // Throwing an exception if validation of any item fails.
            if (!items.stream().allMatch(Item::isValid))
                throw new JsonParseException("Required field \"material\" does not exist on one or more elements.");
            // Returning MaterialChoice if metadata of all items is empty, or ExactChoice otherwise.
            return (items.stream().map(Item::toItemStack).allMatch(it -> !it.hasItemMeta()))
                    ? new RecipeChoice.MaterialChoice(items.stream().map(Item::toItemStack).map(ItemStack::getType).collect(Collectors.toList()))
                    : new RecipeChoice.ExactChoice(items.stream().map(Item::toItemStack).collect(Collectors.toList()));
        }
        // Throwing exception for unexpected input.
        throw new JsonParseException("Expected JsonObject or JsonArray but found " + json.getClass().getSimpleName() + ".");
    }

}
