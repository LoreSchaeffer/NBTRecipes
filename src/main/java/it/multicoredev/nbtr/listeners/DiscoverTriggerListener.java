package it.multicoredev.nbtr.listeners;

import it.multicoredev.nbtr.NBTRecipes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DiscoverTriggerListener implements Listener {
    private final NBTRecipes plugin;

    public DiscoverTriggerListener(final @NotNull NBTRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player)
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                // Getting the event item.
                final ItemStack item = event.getItem().getItemStack();
                // Iterating over list of recipes added by the plugin.
                plugin.getRecipes().forEach(recipe -> {
                    // Skipping already discovered recipes.
                    if (player.hasDiscoveredRecipe(recipe.getKey()))
                        return;
                    // Skipping unspecified or empty discoveries.
                    if (recipe.getDiscoverTrigger() == null || recipe.getDiscoverTrigger().getRequiredChoices() == null || recipe.getDiscoverTrigger().getRequiredChoices().isEmpty())
                        return;
                    // Iterating over list of choices that can discover recipe for the player.
                    for (final RecipeChoice choice : recipe.getDiscoverTrigger().getRequiredChoices()) {
                        // Testing event item against the current choice.
                        if (choice.test(item)) {
                            // Event item passed the choice test, discovering (current) recipe for the player.
                            Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                            // Breaking from the choices loop, as this recipe has been discovered now.
                            break;
                        }
                    }
                });
            });
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Player player = event.getPlayer();
            // Iterating over list of recipes added by the plugin.
            plugin.getRecipes().forEach(recipe -> {
                // Skipping already discovered recipes.
                if (player.hasDiscoveredRecipe(recipe.getKey()))
                    return;
                // Making player immediately discover recipes with no criteria specified.
                if (recipe.getDiscoverTrigger() == null)
                    Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                // Otherwise, testing each choice individually.
                else if (recipe.getDiscoverTrigger().getRequiredChoices() != null && !recipe.getDiscoverTrigger().getRequiredChoices().isEmpty()) {
                    // Iterating over contents of player's inventory.
                    for (final @Nullable ItemStack item : player.getInventory().getContents()) {
                        if (item == null || item.getType() == Material.AIR)
                            continue;
                        // Iterating over list of choices that can discover recipe for the player.
                        for (final RecipeChoice choice : recipe.getDiscoverTrigger().getRequiredChoices()) {
                            // Testing item against the current choice.
                            if (choice.test(item)) {
                                // Item passed the choice test, discovering (current) recipe for the player.
                                Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                                // Breaking from the choices loop, as this recipe has been discovered now.
                                break;
                            }
                        }
                    }
                }
            });
        });
    }

}
