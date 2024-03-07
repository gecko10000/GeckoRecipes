package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipe.CustomFurnaceRecipe
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.itemutils.ItemUtils
import redempt.redlib.misc.ChatPrompt

class FurnaceRecipeEditGUI(player: Player, private val recipe: CustomFurnaceRecipe) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 27
        private const val nameSlot = 4
        private const val xpSlot = 6
        private const val timeSlot = 8
        private const val inputSlot = 10
        private const val furnaceSlot = 11
        private const val resultSlot = 12
    }

    private fun xpButton(): ItemButton {
        val item = ItemStack(Material.EXPERIENCE_BOTTLE).apply {
            editMeta {
                it.displayName(parseMM("<green>Experience"))
                it.lore(
                    listOf(
                        parseMM("<gray>Current value:"),
                        parseMM("<white>${recipe.experience}")
                    )
                )
            }
        }
        return ItemButton.create(item) { _ ->
            player.closeInventory()
            ChatPrompt.prompt(player, "Enter a new experience amount for the recipe:", {
                val newAmount = it.toFloatOrNull() ?: return@prompt run {
                    player.sendMessage(parseMM("<red>Invalid number. Must be a decimal."))
                    FurnaceRecipeEditGUI(player, recipe)
                }
                FurnaceRecipeEditGUI(player, recipe.copy(experience = newAmount))
            }, { if (it == ChatPrompt.CancelReason.PLAYER_CANCELLED) FurnaceRecipeEditGUI(player, recipe) })
        }
    }

    private fun timeButton(): ItemButton {
        val item = ItemStack(Material.CLOCK).apply {
            editMeta {
                it.displayName(parseMM("<green>Cooking Time"))
                it.lore(
                    listOf(
                        parseMM("<gray>Current value:"),
                        parseMM("<white>${recipe.cookingTimeTicks} ticks (${recipe.cookingTimeTicks / 20.0} seconds)")
                    )
                )
            }
        }
        return ItemButton.create(item) { _ ->
            player.closeInventory()
            ChatPrompt.prompt(player, "Enter a new cooking time in ticks for this recipe:", {
                val newAmount = it.toIntOrNull() ?: return@prompt run {
                    player.sendMessage(parseMM("<red>Invalid number. Must be an integer."))
                    FurnaceRecipeEditGUI(player, recipe)
                }
                FurnaceRecipeEditGUI(player, recipe.copy(cookingTimeTicks = newAmount))
            }, { if (it == ChatPrompt.CancelReason.PLAYER_CANCELLED) FurnaceRecipeEditGUI(player, recipe) })
        }
    }

    private fun getCurrentRecipe(): CustomFurnaceRecipe {
        val inventoryResult = inventory.getItem(resultSlot)
        val result = if (ItemUtils.isEmpty(inventoryResult)) recipe.result else inventoryResult!!
        return recipe.copy(_result = result)
    }

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(Bukkit.createInventory(this, SIZE, plugin.config.editRecipeName(recipe)))
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        inventory.addButton(
            nameSlot,
            guiComponents.nameButton(
                player,
                recipe,
                callback = { FurnaceRecipeEditGUI(player, it as CustomFurnaceRecipe) },
                cancelCallback = { FurnaceRecipeEditGUI(player, recipe) })
        )
        inventory.addButton(xpSlot, xpButton())
        inventory.addButton(timeSlot, timeButton())
        inventory.addButton(
            inputSlot,
            guiComponents.editRecipeChoiceButton(player, recipe.input) {
                FurnaceRecipeEditGUI(
                    player,
                    recipe.copy(input = it ?: recipe.input)
                )
            })
        inventory.inventory.setItem(furnaceSlot, ItemStack(Material.FURNACE))
        inventory.inventory.setItem(resultSlot, recipe.result)
        inventory.openSlot(resultSlot)
        inventory.setReturnsItems(false)
        inventory.addButton(SIZE - 4, ItemButton.create(plugin.config.cancelButton.item) { _ ->
            RecipesEditGUI(player)
        })
        inventory.addButton(SIZE - 2, ItemButton.create(plugin.config.confirmButton.item) { _ ->
            recipeManager.addRecipe(getCurrentRecipe())
            RecipesEditGUI(player)
        })
        return inventory
    }

}
