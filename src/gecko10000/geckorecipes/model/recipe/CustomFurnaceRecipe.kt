@file:UseSerializers(MMComponentSerializer::class, ItemStackSerializer::class)

package gecko10000.geckorecipes.model.recipe

import gecko10000.geckolib.config.serializers.ItemStackSerializer
import gecko10000.geckolib.config.serializers.MMComponentSerializer
import gecko10000.geckolib.extensions.MM
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeMaterialChoice
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
data class CustomFurnaceRecipe(
    override val id: String,
    override val name: Component = MM.deserialize("<green>Furnace Recipe"),
    private val _result: ItemStack = ItemStack(Material.AIR),
    override val category: CraftingBookCategory = CraftingBookCategory.MISC,
    override val requiresPermission: Boolean = false,

    override val input: CustomRecipeChoice = CustomRecipeMaterialChoice(),
    override val experience: Float = 0.1f,
    override val cookingTimeTicks: Int = 200,
) : CustomCookingRecipe {
    override val result: ItemStack
        get() = _result.clone()

    override fun getRecipe(): FurnaceRecipe {
        return FurnaceRecipe(
            key, result, input.getRecipeChoice(), experience, cookingTimeTicks
        )
    }

}
