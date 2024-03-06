@file:UseSerializers(ItemStackSerializer::class)

package gecko10000.geckorecipes.model.recipechoice

import gecko10000.geckoconfig.serializers.ItemStackSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bukkit.Material
import org.bukkit.inventory.RecipeChoice


@Serializable
data class CustomRecipeMaterialChoice(
    val validMaterials: List<Material>,
) : CustomRecipeChoice {
    override fun getRecipeChoice(): RecipeChoice.MaterialChoice {
        return RecipeChoice.MaterialChoice(validMaterials)
    }
}
