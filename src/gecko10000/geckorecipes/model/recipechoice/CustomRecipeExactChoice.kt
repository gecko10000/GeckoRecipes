@file:UseSerializers(ItemStackSerializer::class)

package gecko10000.geckorecipes.model.recipechoice

import gecko10000.geckoconfig.serializers.ItemStackSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice


@Serializable
data class CustomRecipeExactChoice(
    val validItems: List<ItemStack>,
) : CustomRecipeChoice {

    override fun getRecipeChoice(): RecipeChoice.ExactChoice {
        return RecipeChoice.ExactChoice(validItems)
    }
}
