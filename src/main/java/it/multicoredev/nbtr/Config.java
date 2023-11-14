package it.multicoredev.nbtr;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

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
public class Config extends JsonConfig {
    public String namespace;
    @SerializedName("parse_minimessage_in_name_and_lore")
    public Boolean parseMiniMessageInNameAndLore;
    @SerializedName("insufficient_permissions")
    public String insufficientPerms;
    @SerializedName("incorrect_usage")
    public String incorrectUsage;
    public String reloaded;
    @SerializedName("recipes_list")
    public String recipesList;
    @SerializedName("recipes_list_item")
    public String recipesListItem;

    @Override
    public Config init() {
        if (namespace == null) namespace = "nbtrecipes";
        if (parseMiniMessageInNameAndLore == null) parseMiniMessageInNameAndLore = false;
        if (insufficientPerms == null) insufficientPerms = "<red>Insufficient permissions!";
        if (incorrectUsage == null) incorrectUsage = "<red>Incorrect usage!";
        if (reloaded == null) reloaded = "<green>Plugin has been reloaded.";
        if (recipesList == null) recipesList = "<gold>There are <yellow>{amount} <gold>recipes loaded:";
        if (recipesListItem == null) recipesListItem = "  <yellow>{recipe}";
        return this;
    }
}
