package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.updated
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton

class ShapedRecipeEditGUI(player: Player, private val recipe: CustomShapedRecipe) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()
    private val recipeManager: RecipeManager by inject()

    companion object {
        private const val SIZE = 45
        private val ingredientSlots = listOf(10, 11, 12, 19, 20, 21, 28, 29, 30)
        private const val resultSlot = 24
        private const val nameSlot = 6
    }

    private fun getCurrentRecipe(): CustomShapedRecipe =
        recipe.copy(_result = inventory.getItem(resultSlot) ?: recipe.result)

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(
            Bukkit.createInventory(
                this,
                SIZE,
                MM.deserialize("<dark_green>Editing recipe <id>", Placeholder.unparsed("id", recipe.id))
            )
        )
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        inventory.addButton(
            nameSlot,
            guiComponents.nameButton(
                player,
                recipe,
                { ShapedRecipeEditGUI(player, it as CustomShapedRecipe) },
                { ShapedRecipeEditGUI(player, recipe) })
        )
        recipe.ingredients
            .mapIndexed { i, ingredient ->
                guiComponents.editRecipeChoiceButton(player, ingredient) {
                    ShapedRecipeEditGUI(player, recipe.copy(ingredients = recipe.ingredients.updated(i, it)))
                }
            }
            .forEachIndexed { i, button ->
                val slot = ingredientSlots[i]
                inventory.addButton(slot, button)
            }
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.openSlot(resultSlot)
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
