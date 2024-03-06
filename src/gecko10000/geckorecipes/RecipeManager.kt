package gecko10000.geckorecipes

import gecko10000.geckorecipes.model.recipe.CustomRecipe
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.misc.EventListener

class RecipeManager : KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipes: MutableMap<String, CustomRecipe>
        get() = plugin.recipeHolder.recipes

    init {
        recipes.values.forEach(this::addRecipe)
        EventListener(PrepareItemCraftEvent::class.java) { e ->
            val keyed = e.recipe as? Keyed ?: return@EventListener
            val key = keyed.key
            if (key.namespace != GeckoRecipes.NAMESPACE) return@EventListener
            val recipeId = key.key
            val recipe = recipes[recipeId] ?: return@EventListener
            val player = e.view.player as? Player ?: return@EventListener
            if (!recipe.hasPermission(player)) {
                e.inventory.result = null
            }
        }
    }

    private fun updateDiscovery(customRecipe: CustomRecipe) {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (customRecipe.hasPermission(player)) {
                player.discoverRecipe(customRecipe.key)
            } else {
                player.undiscoverRecipe(customRecipe.key)
            }
        }
    }

    fun addRecipe(customRecipe: CustomRecipe) {
        recipes[customRecipe.id] = customRecipe
        Bukkit.removeRecipe(customRecipe.key)
        Bukkit.addRecipe(customRecipe.getRecipe())
        updateDiscovery(customRecipe)
        save()
    }

    fun deleteRecipe(customRecipe: CustomRecipe) {
        if (recipes.remove(customRecipe.id, customRecipe)) {
            Bukkit.removeRecipe(customRecipe.key)
            save()
        }
    }

    fun deleteRecipe(recipeId: String) {
        val customRecipe = recipes.remove(recipeId) ?: return
        Bukkit.removeRecipe(customRecipe.key)
        save()
    }

    fun getRecipes() = recipes.values.toList()

    fun getRecipes(player: Player) = getRecipes().filter { it.hasPermission(player) }

    private fun save() = plugin.saveConfigs()

}
