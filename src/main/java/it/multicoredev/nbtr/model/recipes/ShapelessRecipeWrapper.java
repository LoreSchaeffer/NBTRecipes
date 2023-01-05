package it.multicoredev.nbtr.model.recipes;

import it.multicoredev.nbtr.model.Item;
import org.bukkit.inventory.ShapelessRecipe;

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
public class ShapelessRecipeWrapper extends RecipeWrapper {
    private List<Item> ingredients;
    private Item result;

    public ShapelessRecipeWrapper() {
        super(Type.SHAPELESS);
    }

    @Override
    public ShapelessRecipe toBukkit() {
        ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, result.toItemStack());
        ingredients.forEach(item -> recipe.addIngredient(item.toItemStack().getType()));

        return recipe;
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
}
