package gecko10000.geckorecipes

import gecko10000.geckolib.misc.EventListener
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Recipe
import org.koin.core.component.inject

class RecipeManager : MyKoinComponent {

    private val plugin: GeckoRecipes by inject()

    private fun Recipe.extractCustomRecipe(): CustomRecipe? {
        val keyed = this as? Keyed ?: return null
        val key = keyed.key
        if (key.namespace != GeckoRecipes.NAMESPACE) return null
        val recipeId = key.key
        return plugin.recipes[recipeId]
    }

    init {
        plugin.recipes.values.forEach(this::addRecipe)
        EventListener(PrepareItemCraftEvent::class.java) { e ->
            val recipe = e.recipe?.extractCustomRecipe() ?: return@EventListener
            val player = e.view.player as? Player ?: return@EventListener
            if (!recipe.hasPermission(player)) {
                e.inventory.result = null
            }
        }
        EventListener(PlayerJoinEvent::class.java) { e ->
            val player = e.player
            plugin.recipes.values.forEach { recipe -> updateDiscovery(recipe, player) }
        }
    }

    private fun updateDiscovery(customRecipe: CustomRecipe, player: Player) {
        if (!customRecipe.hasPermission(player)) player.undiscoverRecipe(customRecipe.key)
    }

    private fun updateDiscovery(customRecipe: CustomRecipe) {
        Bukkit.getOnlinePlayers().forEach { player -> updateDiscovery(customRecipe, player) }
    }

    fun addRecipe(customRecipe: CustomRecipe) {
        plugin.recipes[customRecipe.id] = customRecipe
        Bukkit.removeRecipe(customRecipe.key)
        Bukkit.addRecipe(customRecipe.getRecipe(), true)
        updateDiscovery(customRecipe)
        save()
    }

    fun deleteRecipe(customRecipe: CustomRecipe) {
        if (plugin.recipes.remove(customRecipe.id, customRecipe)) {
            Bukkit.removeRecipe(customRecipe.key, true)
            save()
        }
    }

    fun deleteRecipe(recipeId: String) {
        val customRecipe = plugin.recipes.remove(recipeId) ?: return
        Bukkit.removeRecipe(customRecipe.key, true)
        save()
    }

    fun getRecipes() = plugin.recipes.values.toList()

    fun getRecipes(player: Player) = getRecipes().filter { it.hasPermission(player) }

    private fun save() = plugin.saveConfigs()

}
