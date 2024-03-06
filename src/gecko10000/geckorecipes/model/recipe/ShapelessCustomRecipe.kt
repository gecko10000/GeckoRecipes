@file:UseSerializers(ItemStackSerializer::class, MMComponentSerializer::class)

package gecko10000.geckorecipes.model.recipe

import gecko10000.geckoconfig.serializers.ItemStackSerializer
import gecko10000.geckoconfig.serializers.MMComponentSerializer
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
data class ShapelessCustomRecipe(
    override val id: String,
    override val name: Component,
    override val result: ItemStack,
    override val category: CraftingBookCategory = CraftingBookCategory.MISC,
    val ingredients: List<CustomRecipeChoice>,
) : CustomRecipe {
    override fun getRecipe(): ShapelessRecipe {
        return ShapelessRecipe(key, result).apply {
            ingredients
                .map(CustomRecipeChoice::getRecipeChoice)
                .forEach(this::addIngredient)
        }
    }
}
