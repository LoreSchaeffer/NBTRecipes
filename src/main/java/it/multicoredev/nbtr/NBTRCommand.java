package it.multicoredev.nbtr;

import it.multicoredev.mbcore.spigot.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

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
public class NBTRCommand implements CommandExecutor, TabCompleter {
    private final NBTRecipes plugin;

    public NBTRCommand(NBTRecipes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nbtr.command")) {
            Text.get().send(Text.toMiniMessage(plugin.config().insufficientPerms), sender);
            return true;
        }

        if (args.length == 0) {
            Text.get().send(Text.toMiniMessage(plugin.config().incorrectUsage), sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                // Disabling the plugin.
                plugin.onDisable();
                // Enabling the plugin again.
                plugin.onEnable();
                // Sending message to the sender.
                Text.get().send(Text.toMiniMessage(plugin.config().reloaded), sender);
            }
            case "list" -> {
                Text.get().send(Text.toMiniMessage(plugin.config().recipesList).replace("{amount}", String.valueOf(plugin.getRecipes().size())), sender);
                plugin.getRecipes().forEach(recipe -> Text.get().send(Text.toMiniMessage(plugin.config().recipesListItem).replace("{recipe}", recipe.getKey().toString()), sender));
            }
            default -> Text.get().send(Text.toMiniMessage(plugin.config().incorrectUsage), sender);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nbtr.command")) return null;
        if (args.length == 1) return TabCompleterUtil.getCompletions(args[0], "reload", "list");

        return null;
    }
}
