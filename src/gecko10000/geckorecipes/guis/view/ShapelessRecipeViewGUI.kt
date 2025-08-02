package gecko10000.geckorecipes.guis.view

import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject

class ShapelessRecipeViewGUI(player: Player, private val recipe: CustomShapelessRecipe) : GUI(player), MyKoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 45
        private val ingredientSlots = listOf(10, 11, 12, 19, 20, 21, 28, 29, 30)
        private const val resultSlot = 24
        private const val slimeballSlot = 4
        private const val craftingTableSlot = 22
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
        inventory.inventory.setItem(
            slimeballSlot, ItemStack(
                Material.SLIME_BALL
            ).apply {
                editMeta {
                    it.displayName(parseMM("<green>Shapeless Recipe"))
                }
            })
        inventory.inventory.setItem(craftingTableSlot, ItemStack(Material.CRAFTING_TABLE).apply {
            editMeta {
                it.displayName(Component.empty())
            }
        })
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.addButton(SIZE - 5, guiComponents.backButton { RecipesViewGUI(player) })
        return inventory
    }
}
