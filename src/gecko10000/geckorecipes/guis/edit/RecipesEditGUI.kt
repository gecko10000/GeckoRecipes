package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckolib.misc.ItemUtils
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.model.recipe.CustomCookingRecipe
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.inject
import kotlin.math.min

class RecipesEditGUI(player: Player) : GUI(player), MyKoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()

    private fun recipeEditButton(recipe: CustomRecipe): ItemButton {
        return ItemButton.create(recipe.result.apply {
            editMeta {
                it.displayName(recipe.name.withDefaults())
                it.lore(
                    listOf(
                        Component.empty(),
                        parseMM("<red>Left click to edit"),
                        parseMM("<red>Shift+left click to move left"),
                        parseMM("<red>Shift+right click to move right."),
                        parseMM("<dark_red>Press Q to delete."),
                    )
                )
            }
        }) { e ->
            if (e.isShiftClick) {
                if (!e.isRightClick && !e.isLeftClick) return@create
                val left = e.isLeftClick
                val right = !left
                val entries = plugin.recipes.toList().toMutableList()
                val index = entries.indexOfFirst { it.first == recipe.id }
                if (index == -1) return@create
                if (left && index == 0) return@create
                if (right && index == entries.size - 1) return@create
                val toMove = entries.removeAt(index)
                entries.add(index + (if (left) -1 else 1), toMove)
                plugin.recipes.clear()
                plugin.recipes.putAll(entries)
                plugin.saveConfigs()
                RecipesEditGUI(player)
                return@create
            }
            if (e.isLeftClick) {
                when (recipe) {
                    is CustomShapedRecipe -> ShapedRecipeEditGUI(player, recipe)
                    is CustomShapelessRecipe -> ShapelessRecipeEditGUI(player, recipe)
                    is CustomCookingRecipe -> CookingRecipeEditGUI(player, recipe)
                }
            }
            if (e.click == ClickType.DROP) {
                recipeManager.deleteRecipe(recipe)
                RecipesEditGUI(player)
                return@create
            }
        }
    }

    override fun createInventory(): InventoryGUI {
        val recipes = recipeManager.getRecipes()
        val inventorySize = min(54, ItemUtils.minimumChestSize(recipes.size + 1) + 9)
        val inventory =
            InventoryGUI(Bukkit.createInventory(this, inventorySize, MM.deserialize("<#0085e6>Custom Recipe Editor")))
        inventory.fill(0, inventorySize, plugin.config.fillerItem)
        recipes.map(this::recipeEditButton).forEachIndexed(inventory::addButton)
        inventory.addButton(recipes.size, ItemButton.create(plugin.config.createButton.item) { _ ->
            NewRecipeTypeGUI(player)
        })
        return inventory
    }
}
