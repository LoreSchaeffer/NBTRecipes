package it.multicoredev.nbtr.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import it.multicoredev.nbtr.model.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public final class RecipeChoiceAdapter implements JsonDeserializer<RecipeChoice> {

    @Override
    public RecipeChoice deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        // Reading as single object.
        if (json.isJsonObject() == true) {
            final Item item = context.deserialize(json, Item.class);
            // ...
            if (item.isValid() == false)
                throw new JsonParseException("Required field \"material\" does not exist.");
            // ...
            return (item.toItemStack().hasItemMeta() == false)
                    ? new RecipeChoice.MaterialChoice(item.toItemStack().getType())
                    : new RecipeChoice.ExactChoice(item.toItemStack());
        }
        // Reading as array of objects.
        else if (json.isJsonArray() == true) {
            final List<Item> items = context.deserialize(json, TypeToken.getParameterized(List.class, Item.class).getType());
            // ...
            if (items.stream().allMatch(Item::isValid) == false)
                throw new JsonParseException("Required field \"material\" does not exist on one or more elements.");
            // ...
            return (items.stream().map(Item::toItemStack).allMatch(it -> it.hasItemMeta() == false) == true)
                    ? new RecipeChoice.MaterialChoice(items.stream().map(Item::toItemStack).map(ItemStack::getType).collect(Collectors.toList()))
                    : new RecipeChoice.ExactChoice(items.stream().map(Item::toItemStack).collect(Collectors.toList()));
        }
        // Throwing exception for unexpected input.
        throw new JsonParseException("Expected BEGIN_OBJECT or BEGIN_ARRAY but found something else.");
    }

}
