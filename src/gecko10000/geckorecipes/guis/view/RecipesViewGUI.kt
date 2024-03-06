package gecko10000.geckorecipes.guis.view

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.inventorygui.PaginationPanel
import redempt.redlib.itemutils.ItemUtils
import kotlin.math.min

class RecipesViewGUI(player: Player) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()

    private fun recipeButton(recipe: CustomRecipe) =
        ItemButton.create(recipe.result.apply { editMeta { it.displayName(recipe.name.withDefaults()) } }) { _ ->
            when (recipe) {
                is CustomShapedRecipe -> ShapedRecipeViewGUI(player, recipe)
                is CustomShapelessRecipe -> ShapelessRecipeViewGUI(player, recipe)
            }
        }

    override fun createInventory(): InventoryGUI {
        val recipes = recipeManager.getRecipes()
        val inventorySize = min(54, ItemUtils.minimumChestSize(recipes.size))
        val inventory = InventoryGUI(Bukkit.createInventory(this, inventorySize, plugin.config.viewGuiName))
        inventory.fill(0, inventorySize, plugin.config.fillerItem)
        val paginationPanel = PaginationPanel(inventory, plugin.config.fillerItem)
        val hasMultiplePages = recipes.size > 54
        paginationPanel.addSlots(0, if (hasMultiplePages) 45 else inventorySize)
        recipes
            .map(this::recipeButton)
            .forEach(paginationPanel::addPagedButton)
        if (hasMultiplePages) {
            inventory.addButton(
                inventorySize - 6,
                ItemButton.create(plugin.config.prevButton.item) { _ -> paginationPanel.prevPage() })
            inventory.addButton(
                inventorySize - 4,
                ItemButton.create(plugin.config.nextButton.item) { _ -> paginationPanel.nextPage() })
        }
        return inventory
    }

}
