package it.multicoredev.nbtr.model.recipes;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.nbtr.model.Item;
import it.multicoredev.nbtr.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class ShapedRecipe extends Recipe {
    private List<String> pattern;
    private Map<String, Item> key;
    private Item result;
    private transient ItemStack[][] recipe;
    private transient int size;

    public ShapedRecipe() {
        super(Type.SHAPED);
    }

    public boolean compare(ItemStack[][] matrix) {

    }

    @Override
    public void prepare() {
        List<ItemStack> items = new ArrayList<>();

        for (String s : pattern) {
            for (char c : s.toCharArray()) {
                if (c == ' ') {
                    items.add(new ItemStack(Material.AIR));
                } else {
                    Item item = key.get(String.valueOf(c));
                    ItemStack is = new ItemStack(item.getMaterial());

                    if (item.getName() != null && !item.getName().trim().isEmpty() || item.getLore() != null && !item.getLore().isEmpty()) {
                        ItemMeta meta = is.getItemMeta();
                        if (meta != null) {
                            if (item.getName() != null && !item.getName().trim().isEmpty()) meta.setDisplayName(Chat.getTranslated(item.getName()));
                            if (item.getLore() != null && !item.getLore().isEmpty()) meta.setLore(Chat.getTranslated(item.getLore()));
                            is.setItemMeta(meta);
                        }
                    }

                    if (item.getNbt() != null && !item.getNbt().trim().isEmpty()) {
                        NBTItem nbti = new NBTItem(is);

                        try {
                            nbti.mergeCompound(new NBTContainer(item.getNbt()));
                            is = nbti.getItem();
                        } catch (Exception ignored) {
                            Chat.warning("Invalid NBT tag: " + item.getNbt());
                        }
                    }

                    items.add(is);
                }
            }
        }

        recipe = Utils.toMatrix(items.toArray(new ItemStack[0]));
        size = recipe.length;
    }

    @Override
    public boolean isValid() {
        if (pattern == null || pattern.isEmpty()) return false;

        if (pattern.size() == 3) {
            for (String s : pattern) {
                if (s.length() != 3) return false;
            }
        } else if (pattern.size() == 2) {
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
                if (!key.containsKey(String.valueOf(c)) && c != ' ') return false;
            }
        }

        return result != null && result.isValid();
    }
}
