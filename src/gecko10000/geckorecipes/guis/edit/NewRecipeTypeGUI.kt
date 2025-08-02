package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckolib.misc.ChatPrompt
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.guis.prompt
import gecko10000.geckorecipes.model.recipe.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject

class NewRecipeTypeGUI(player: Player) : GUI(player), MyKoinComponent {

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
            prompt(
                player, MM.deserialize("<green>Enter a unique ID for the ${recipeType.displayName}"),
                { recipeType.callback(player, it) }, { cancelReason ->
                    if (cancelReason == ChatPrompt.CancelReason.PLAYER_CANCELLED) {
                        RecipesEditGUI(player)
                    }
                })
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
            callback = { p, id -> CookingRecipeEditGUI(p, CustomFurnaceRecipe(id)) }
        ),
        BLAST_FURNACE(
            icon = Material.BLAST_FURNACE,
            displayName = "Blast Furnace Recipe",
            callback = { p, id -> CookingRecipeEditGUI(p, CustomBlastingRecipe(id)) }
        ),
        SMOKER(
            icon = Material.SMOKER,
            displayName = "Smoker Recipe",
            callback = { p, id -> CookingRecipeEditGUI(p, CustomSmokingRecipe(id)) }
        )
    }

}
