package gecko10000.geckorecipes.guis

import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.view.RecipeChoiceGUI
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeExactChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeMaterialChoice
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.ItemButton

class GUIComponents : KoinComponent {

    private val plugin: GeckoRecipes by inject()

    fun recipeChoiceButton(player: Player, recipeChoice: CustomRecipeChoice, callback: () -> Unit): ItemButton {
        val items = when (recipeChoice) {
            is CustomRecipeExactChoice -> recipeChoice.validItems
            is CustomRecipeMaterialChoice -> recipeChoice.validMaterials.map(::ItemStack)
        }
        return ItemButton.create(items.getOrElse(0) { ItemStack(Material.BARRIER) }) { _ ->
            if (items.size > 1) RecipeChoiceGUI(
                player,
                recipeChoice,
                callback
            )
        }
    }

    fun backButton(callback: () -> Unit): ItemButton =
        ItemButton.create(plugin.config.backButton.item) { _ -> callback() }

}
