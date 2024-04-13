package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomCookingRecipe
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.itemutils.ItemUtils
import kotlin.math.min

class RecipesEditGUI(player: Player) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()
    private val guiComponents: GUIComponents by inject()

    private fun recipeEditButton(recipe: CustomRecipe): ItemButton {
        return ItemButton.create(recipe.result.apply {
            editMeta {
                it.displayName(recipe.name.withDefaults())
                it.lore(listOf(Component.empty(), parseMM("<red>Shift+right click to delete.")))
            }
        }) { e ->
            if (e.isShiftClick && e.isRightClick) {
                recipeManager.deleteRecipe(recipe)
                RecipesEditGUI(player)
                return@create
            }
            when (recipe) {
                is CustomShapedRecipe -> ShapedRecipeEditGUI(player, recipe)
                is CustomShapelessRecipe -> ShapelessRecipeEditGUI(player, recipe)
                is CustomCookingRecipe -> CookingRecipeEditGUI(player, recipe)
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
