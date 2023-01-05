package it.multicoredev.nbtr.model.recipes;

import it.multicoredev.nbtr.model.Item;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

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
public class ShapedRecipeWrapper extends RecipeWrapper {
    private String[] pattern;
    private Map<Character, Item> key;
    private Item result;

    public ShapedRecipeWrapper() {
        super(Type.SHAPED);
    }

    @Override
    public ShapedRecipe toBukkit() {
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result.toItemStack());
        recipe.shape(pattern);

        for (Map.Entry<Character, Item> entry : key.entrySet()) {
            recipe.setIngredient(entry.getKey(), new RecipeChoice.ExactChoice(entry.getValue().toItemStack()));
        }

        return recipe;
    }

    @Override
    public boolean isValid() {
        if (pattern == null || pattern.length == 0) return false;

        if (pattern.length == 3) {
            for (String s : pattern) {
                if (s.length() != 3) return false;
            }
        } else if (pattern.length == 2) {
            for (String s : pattern) {
                if (s.length() != 2) return false;
            }
        } else {
            return false;
        }

        if (key == null || key.isEmpty()) return false;

        for (Item item : key.values()) {
            if (item == null) return false;
            if (!item.isValid()) return false;
        }

        for (String s : pattern) {
            for (char c : s.toCharArray()) {
                if (!key.containsKey(c) && c != ' ') return false;
            }
        }

        return result != null && result.isValid();
    }
}
