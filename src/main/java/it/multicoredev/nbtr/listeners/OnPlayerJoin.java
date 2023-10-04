package it.multicoredev.nbtr.listeners;

import it.multicoredev.nbtr.NBTRecipes;
import it.multicoredev.nbtr.model.recipes.RecipeWrapper;
import it.multicoredev.nbtr.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
public class OnPlayerJoin implements Listener {
    private final NBTRecipes plugin;

    public OnPlayerJoin(NBTRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPickupItem(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = event.getPlayer();
            Inventory inventory = player.getInventory();

            for (RecipeWrapper recipe : plugin.getRecipes()) {
                if (recipe.getDiscoverTrigger() == null) {
                    Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                    continue;
                } else if (recipe.getDiscoverTrigger().getRequiredItems() == null || recipe.getDiscoverTrigger().getRequiredItemStacks().isEmpty()) {
                    continue;
                }

                List<ItemStack> items = recipe.getDiscoverTrigger().getRequiredItemStacks();

                items:
                for (ItemStack item : inventory.getContents()) {
                    if (item == null) continue;

                    for (ItemStack i : items) {
                        if (ItemUtils.areItemsEquals(item, i)) {
                            Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                            continue items;
                        }
                    }
                }
            }
        });
    }
}
