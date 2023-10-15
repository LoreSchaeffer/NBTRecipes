package it.multicoredev.nbtr.model.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.multicoredev.nbtr.model.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class RecipeChoiceAdapter implements TypeAdapterFactory {

    @Override @SuppressWarnings("unchecked") // Unchecked casts warnings can be suppressed as they should never fail.
    public <T> TypeAdapter<T> create(final @NotNull Gson gson, final @NotNull TypeToken<T> type) {
        if (type.getRawType().isAssignableFrom(RecipeChoice.class) == false)
            return null;
        // ...
        return new TypeAdapter<T>() {

            @Override
            public T read(final JsonReader in) throws IOException {
                // Reading as single object.
                if (in.peek() == JsonToken.BEGIN_OBJECT) {
                    System.out.println(1);
                    final Item item = gson.getAdapter(Item.class).read(in);
                    // ...
                    if (item.isValid() == false)
                        throw new JsonParseException(in.getPath() + ": Required field \"material\" does not exist.");
                    // ...
                    return (item.toItemStack().hasItemMeta() == false)
                            ? (T) new RecipeChoice.MaterialChoice(item.toItemStack().getType())
                            : (T) new RecipeChoice.ExactChoice(item.toItemStack());
                }
                // Reading as array of objects.
                else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    System.out.println(2);
                    final List<Item> items = (List<Item>) gson.getAdapter(TypeToken.getParameterized(List.class, Item.class)).read(in);
                    // ...
                    if (items.stream().allMatch(Item::isValid) == false)
                        throw new JsonParseException(in.getPath() + ": Required field \"material\" does not exist on one or more elements.");
                    // ...
                    return (items.stream().map(Item::toItemStack).allMatch(it -> it.hasItemMeta() == false) == true)
                            ? (T) new RecipeChoice.MaterialChoice(items.stream().map(Item::toItemStack).map(ItemStack::getType).collect(Collectors.toList()))
                            : (T) new RecipeChoice.ExactChoice(items.stream().map(Item::toItemStack).collect(Collectors.toList()));
                }
                // Throwing exception for unexpected input.
                throw new JsonParseException(in.getPath() + ": Expected BEGIN_OBJECT or BEGIN_ARRAY but found " + in.peek());
            }

            @Override
            public void write(final JsonWriter out, final T value) throws IOException {
                throw new UnsupportedOperationException("NOT IMPLEMENTED");
            }

        };
    }
}
