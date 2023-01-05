package it.multicoredev.nbtr.model;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import it.multicoredev.mbcore.spigot.Chat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
public class Item {
    private Material material;
    private Integer amount;
    private String name;
    private List<String> lore;
    private String nbt;

    public Item(Material material, Integer amount, String name, List<String> lore, String nbt) {
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.nbt = nbt;
    }

    public Item(Material material, Integer amount, String name, List<String> lore) {
        this(material, amount, name, lore, null);
    }

    public Item(Material material, Integer amount, String name) {
        this(material, amount, name, null, null);
    }

    public Item(Material material, Integer amount) {
        this(material, amount, null, null, null);
    }

    public Item(Material material) {
        this(material, 1, null, null, null);
    }

    public Item(Material material, String name, List<String> lore, String nbt) {
        this(material, 1, name, lore, nbt);
    }

    public Item(Material material, String name, List<String> lore) {
        this(material, 1, name, lore, null);
    }

    public Item(Material material, String name) {
        this(material, 1, name, null, null);
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        if (amount != null) return amount > 0 ? amount : 1;
        return 1;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getNbt() {
        return nbt;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material);

        if (amount != null && amount > 0) {
            if (amount > material.getMaxStackSize()) item.setAmount(material.getMaxStackSize());
            else item.setAmount(amount);
        }

        if (name != null || (lore != null && !lore.isEmpty())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (name != null) meta.setDisplayName(Chat.getTranslated(name));
                if (lore != null && !lore.isEmpty()) meta.setLore(Chat.getTranslated(lore));
                item.setItemMeta(meta);
            }
        }

        if (nbt != null && !nbt.trim().isEmpty()) {
            NBTItem nbti = new NBTItem(item);

            try {
                nbti.mergeCompound(new NBTContainer(nbt));
                item = nbti.getItem();
            } catch (Exception ignored) {
                Chat.warning("Invalid NBT tag: " + nbt);
            }
        }

        return item;
    }

    public boolean isValid() {
        return material != null;
    }
}
