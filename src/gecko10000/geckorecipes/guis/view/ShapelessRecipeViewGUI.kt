package gecko10000.geckorecipes.guis.view

import gecko10000.geckolib.GUI
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI

class ShapelessRecipeViewGUI(player: Player, private val recipe: CustomShapelessRecipe) : GUI(player), KoinComponent {

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
        ingredientSlots.forEach { inventory.inventory.setItem(it, null) }
        recipe.ingredients.map {
            guiComponents.viewRecipeChoiceButton(player, it) {
                ShapelessRecipeViewGUI(
                    player,
                    recipe
                )
            }
        }
            .forEachIndexed { i, button ->
                inventory.addButton(
                    ingredientSlots[i], button
                )
            }
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.addButton(SIZE - 5, guiComponents.backButton { RecipesViewGUI(player) })
        return inventory
    }
}
