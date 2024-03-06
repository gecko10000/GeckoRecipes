package gecko10000.geckorecipes.model.recipechoice

import kotlinx.serialization.Serializable
import org.bukkit.inventory.RecipeChoice

@Serializable
sealed interface CustomRecipeChoice {
    fun getRecipeChoice(): RecipeChoice
}
