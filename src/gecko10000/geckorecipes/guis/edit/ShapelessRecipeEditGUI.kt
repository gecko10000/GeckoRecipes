package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.extensions.updated
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.inject

class ShapelessRecipeEditGUI(player: Player, private val recipe: CustomShapelessRecipe) : GUI(player), MyKoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 45
        private val ingredientSlots = listOf(10, 11, 12, 19, 20, 21, 28, 29, 30)
        private const val resultSlot = 24
        private const val nameSlot = 5
        private const val permissionSlot = 7
    }

    private fun getCurrentRecipe(): CustomShapelessRecipe =
        recipe.copy(_result = inventory.inventory.getItem(resultSlot) ?: recipe.result)

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(Bukkit.createInventory(this, SIZE, plugin.config.editRecipeName(recipe)))
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        inventory.addButton(
            nameSlot,
            guiComponents.nameButton(
                player,
                recipe,
                this::getCurrentRecipe,
                { ShapelessRecipeEditGUI(player, it) },
                { ShapelessRecipeEditGUI(player, recipe) })
        )
        inventory.addButton(permissionSlot, guiComponents.togglePermissionButton(recipe) {
            val current = getCurrentRecipe()
            ShapelessRecipeEditGUI(player, current.copy(requiresPermission = !current.requiresPermission))
        })
        ingredientSlots.forEach { inventory.inventory.setItem(it, null) }
        List(ingredientSlots.size) { index -> recipe.ingredients.getOrNull(index) }
            .mapIndexed { index, ingredient ->
                guiComponents.editRecipeChoiceButton(player, this::getCurrentRecipe, ingredient) { r, c ->
                    val newIngredients = if (c == null) {
                        r.ingredients.filterIndexed { i, _ -> i != index }
                    } else {
                        if (index >= r.ingredients.size) {
                            r.ingredients.plus(c)
                        } else {
                            r.ingredients.updated(index, c)
                        }
                    }
                    ShapelessRecipeEditGUI(
                        player,
                        r.copy(ingredients = newIngredients)
                    )
                }
            }
            .forEachIndexed { i, button ->
                inventory.addButton(
                    ingredientSlots[i], button
                )
            }
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.openSlot(resultSlot)
        inventory.setReturnsItems(false)
        inventory.addButton(
            SIZE - 4,
            ItemButton.create(plugin.config.cancelButton.item) { _ -> RecipesEditGUI(player) })
        inventory.addButton(SIZE - 2, ItemButton.create(plugin.config.confirmButton.item) { _ ->
            recipeManager.addRecipe(getCurrentRecipe())
            RecipesEditGUI(player)
        })
        return inventory
    }

}
