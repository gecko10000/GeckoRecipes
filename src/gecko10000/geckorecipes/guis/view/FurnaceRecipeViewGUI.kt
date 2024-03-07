package gecko10000.geckorecipes.guis.view

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomFurnaceRecipe
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import java.text.DecimalFormat

class FurnaceRecipeViewGUI(player: Player, private val recipe: CustomFurnaceRecipe) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 27
        private const val inputSlot = 10
        private const val infoSlot = 13
        private const val resultSlot = 16
        private val decimalFormat = DecimalFormat("0.##")
    }

    private fun infoIcon(): ItemStack = ItemStack(Material.FURNACE).apply {
        editMeta {
            it.displayName(recipe.name.withDefaults())
            it.lore(
                listOf(
                    MM.deserialize(
                        "<gold>Cooking Time: <time>",
                        Placeholder.unparsed(
                            "time",
                            String.format(
                                "%s second%s",
                                decimalFormat.format(recipe.cookingTimeTicks / 20.0),
                                if (recipe.cookingTimeTicks == 20) "" else "s"
                            )
                        )
                    ).withDefaults(),
                    MM.deserialize(
                        "<green><experience> experience",
                        Placeholder.unparsed(
                            "experience",
                            decimalFormat.format(recipe.experience.toDouble())
                        )
                    ).withDefaults(),
                )
            )
        }
    }

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(Bukkit.createInventory(this, SIZE, plugin.config.viewRecipeName(recipe)))
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        inventory.addButton(inputSlot,
            guiComponents.viewRecipeChoiceButton(player, recipe.input) { FurnaceRecipeViewGUI(player, recipe) }
        )
        inventory.inventory.setItem(infoSlot, infoIcon())
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.addButton(SIZE - 5, guiComponents.backButton { RecipesViewGUI(player) })
        return inventory
    }

}
