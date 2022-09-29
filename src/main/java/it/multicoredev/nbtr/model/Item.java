package it.multicoredev.nbtr.model;

import org.bukkit.Material;

import java.util.List;

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

    public boolean isValid() {
        return material != null;
    }
}
