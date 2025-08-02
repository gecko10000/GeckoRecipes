package gecko10000.geckorecipes.guis.view

import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckolib.inventorygui.PaginationPanel
import gecko10000.geckolib.misc.ItemUtils
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.model.recipe.CustomCookingRecipe
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject
import kotlin.math.min

class RecipesViewGUI(player: Player) : GUI(player), MyKoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()
    private val showAll: Boolean by lazy { plugin.config.showRecipeIconsWithoutPermission }

    private fun recipeButton(recipe: CustomRecipe): ItemButton {
        val canAccess = recipe.hasPermission(player)
        val icon = if (canAccess) {
            recipe.result
        } else {
            ItemStack(plugin.config.shownPlaceholderMaterial)
        }.apply { editMeta { it.displayName(recipe.name.withDefaults()) } }
        return ItemButton.create(icon) { _ ->
            if (!canAccess) {
                player.sendMessage(MM.deserialize("<red>You can't access this yet."))
                return@create
            }
            when (recipe) {
                is CustomShapedRecipe -> ShapedRecipeViewGUI(player, recipe)
                is CustomShapelessRecipe -> ShapelessRecipeViewGUI(player, recipe)
                is CustomCookingRecipe -> CookingRecipeViewGUI(player, recipe)
            }
        }
    }

    override fun createInventory(): InventoryGUI {
        val recipes = if (showAll) recipeManager.getRecipes() else recipeManager.getRecipes(player)
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
