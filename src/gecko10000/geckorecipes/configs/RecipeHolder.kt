package gecko10000.geckorecipes.configs

import gecko10000.geckorecipes.model.recipe.CustomRecipe
import kotlinx.serialization.Serializable

@Serializable
data class RecipeHolder(
    val recipes: MutableMap<String, CustomRecipe> = LinkedHashMap(),
)
