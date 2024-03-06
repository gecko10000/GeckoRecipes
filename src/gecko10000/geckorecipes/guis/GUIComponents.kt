package gecko10000.geckorecipes.guis

import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.edit.RecipeChoiceEditGUI
import gecko10000.geckorecipes.guis.view.RecipeChoiceViewGUI
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

    fun getDisplayItems(recipeChoice: CustomRecipeChoice) = when (recipeChoice) {
        is CustomRecipeExactChoice -> recipeChoice.validItems
        is CustomRecipeMaterialChoice -> recipeChoice.validMaterials.map(::ItemStack)
    }

    fun viewRecipeChoiceButton(player: Player, recipeChoice: CustomRecipeChoice, callback: () -> Unit): ItemButton {
        val items = getDisplayItems(recipeChoice)
        return ItemButton.create(items.toList().getOrElse(0) { ItemStack(Material.BARRIER) }) { _ ->
            if (items.size > 1) RecipeChoiceViewGUI(
                player,
                recipeChoice,
                callback
            )
        }
    }

    fun editRecipeChoiceButton(
        player: Player,
        givenChoice: CustomRecipeChoice?,
        callback: (CustomRecipeChoice?) -> Unit,
    ): ItemButton {
        val items = givenChoice?.let { getDisplayItems(it) }
        return ItemButton.create(items?.toList()?.getOrNull(0)) { _ ->
            val recipeChoice = givenChoice ?: CustomRecipeMaterialChoice()
            RecipeChoiceEditGUI(
                player,
                givenChoice,
                recipeChoice,
                callback
            )
        }
    }

    fun backButton(callback: () -> Unit): ItemButton =
        ItemButton.create(plugin.config.backButton.item) { _ -> callback() }

}
