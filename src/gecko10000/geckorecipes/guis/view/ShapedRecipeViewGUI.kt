package gecko10000.geckorecipes.guis.view;

import gecko10000.geckolib.GUI
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI

class ShapedRecipeViewGUI(player: Player, private val recipe: CustomShapedRecipe) :
    GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 45
        private val ingredientSlots = listOf(10, 11, 12, 19, 20, 21, 28, 29, 30)
        private const val resultSlot = 24
    }

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(Bukkit.createInventory(this, SIZE, plugin.config.viewRecipeName(recipe)))
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        recipe.ingredients.map {
            if (it == null) null else guiComponents.viewRecipeChoiceButton(
                player,
                it
            ) { ShapedRecipeViewGUI(player, recipe) }
        }
            .forEachIndexed { i, button ->
                val slot = ingredientSlots[i]
                if (button == null) inventory.inventory.setItem(slot, null)
                else inventory.addButton(slot, button)
            }
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.addButton(SIZE - 5, guiComponents.backButton { RecipesViewGUI(player) })
        return inventory
    }
}
