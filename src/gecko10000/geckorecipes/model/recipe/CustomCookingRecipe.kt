package gecko10000.geckorecipes.model.recipe

import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
sealed interface CustomCookingRecipe : CustomRecipe {
    val input: CustomRecipeChoice
    val experience: Float
    val cookingTimeTicks: Int

    fun copy(
        name: Component = this.name,
        result: ItemStack = this.result,
        category: CraftingBookCategory = this.category,
        requiresPermission: Boolean = this.requiresPermission,
        input: CustomRecipeChoice = this.input,
        experience: Float = this.experience,
        cookingTimeTicks: Int = this.cookingTimeTicks,
    ): CustomCookingRecipe = when (this) {
        is CustomFurnaceRecipe -> this.copy(
            id = id,
            name = name,
            _result = result,
            category = category,
            requiresPermission = requiresPermission,
            input = input,
            experience = experience,
            cookingTimeTicks = cookingTimeTicks
        )

        is CustomBlastingRecipe -> this.copy(
            id = id,
            name = name,
            _result = result,
            category = category,
            requiresPermission = requiresPermission,
            input = input,
            experience = experience,
            cookingTimeTicks = cookingTimeTicks
        )

        is CustomSmokingRecipe -> this.copy(
            id = id,
            name = name,
            _result = result,
            category = category,
            requiresPermission = requiresPermission,
            input = input,
            experience = experience,
            cookingTimeTicks = cookingTimeTicks
        )
    }
}
