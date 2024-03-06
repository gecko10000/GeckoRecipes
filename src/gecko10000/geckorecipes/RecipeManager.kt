package gecko10000.geckorecipes

import gecko10000.geckorecipes.model.recipe.CustomRecipe
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeManager : KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipes: MutableMap<String, CustomRecipe>
        get() = plugin.recipeHolder.recipes

    init {
        recipes.values.forEach(this::addRecipe)
    }

    fun addRecipe(customRecipe: CustomRecipe) {
        recipes[customRecipe.id] = customRecipe
        Bukkit.removeRecipe(customRecipe.key)
        Bukkit.addRecipe(customRecipe.getRecipe())
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

    private fun save() = plugin.saveConfigs()

}
