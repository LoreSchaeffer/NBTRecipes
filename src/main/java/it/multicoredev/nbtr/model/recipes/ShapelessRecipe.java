package it.multicoredev.nbtr.model.recipes;

import it.multicoredev.nbtr.model.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static it.multicoredev.nbtr.utils.Utils.compareItems;

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
public class ShapelessRecipe extends Recipe {
    private List<Item> ingredients;
    private Item result;
    private transient List<ItemStack> recipe;
    private transient int size;
    private transient ItemStack resultItem;

    public ShapelessRecipe() {
        super(Type.SHAPELESS);
    }

    public boolean compare(List<ItemStack> items) {
        if (size != items.size()) return false;

        List<ItemStack> neededItems = new ArrayList<>(recipe);

        for (ItemStack item : items) {
            if (!contains(neededItems, item)) return false;
        }

        return neededItems.isEmpty();
    }

    @Override
    public void prepare() {
        recipe = new ArrayList<>();
        ingredients.forEach(ingredient -> recipe.add(ingredient.toItemStack()));
        size = recipe.size();

        resultItem = result.toItemStack();
    }

    @Override
    public ItemStack getResult() {
        return resultItem;
    }

    @Override
    public boolean isValid() {
        if (ingredients == null || ingredients.isEmpty() || ingredients.size() > 9) return false;

        for (Item item : ingredients) {
            if (item == null) return false;
            if (!item.isValid()) return false;
        }

        return result != null && result.isValid();
    }

    private boolean contains(List<ItemStack> neededItems, ItemStack item) {
        for (ItemStack neededItem : neededItems) {
            if (compareItems(neededItem, item)) {
                neededItems.remove(neededItem);
                return true;
            }
        }

        return false;
    }
}
