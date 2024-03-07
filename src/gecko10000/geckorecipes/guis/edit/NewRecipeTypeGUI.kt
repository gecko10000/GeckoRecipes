package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomFurnaceRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.misc.ChatPrompt

class NewRecipeTypeGUI(player: Player) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 18
    }

    private fun recipeTypeButton(recipeType: RecipeType): ItemButton {
        return ItemButton.create(ItemStack(recipeType.icon).apply {
            editMeta {
                it.displayName(
                    Component.text(recipeType.displayName).withDefaults()
                )
            }
        }) { _ ->
            player.closeInventory()
            ChatPrompt.prompt(
                player,
                "Enter the ID of the ${recipeType.displayName}:",
                { recipeType.callback(player, it) }) {
                if (it == ChatPrompt.CancelReason.PLAYER_CANCELLED) {
                    RecipesEditGUI(player)
                }
            }
        }
    }

    override fun createInventory(): InventoryGUI {
        val inventory =
            InventoryGUI(Bukkit.createInventory(this, SIZE, MM.deserialize("<dark_green>Choose recipe type")))
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        RecipeType.entries.map(this::recipeTypeButton).forEachIndexed(inventory::addButton)
        inventory.addButton(SIZE - 5, guiComponents.backButton { RecipesEditGUI(player) })
        return inventory
    }

    enum class RecipeType(val icon: Material, val displayName: String, val callback: (Player, String) -> Unit) {
        SHAPED_CRAFTING(
            icon = Material.AMETHYST_CLUSTER,
            displayName = "Shaped Recipe",
            callback = { p, id -> ShapedRecipeEditGUI(p, CustomShapedRecipe(id = id)) }),
        SHAPELESS_CRAFTING(
            icon = Material.SLIME_BALL,
            displayName = "Shapeless Recipe",
            callback = { p, id -> ShapelessRecipeEditGUI(p, CustomShapelessRecipe(id = id)) }
        ),
        FURNACE(
            icon = Material.FURNACE,
            displayName = "Furnace Recipe",
            callback = { p, id -> FurnaceRecipeEditGUI(p, CustomFurnaceRecipe(id)) }
        )
    }

}
