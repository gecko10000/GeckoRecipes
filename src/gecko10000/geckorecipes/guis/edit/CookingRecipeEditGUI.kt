package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.extensions.asClickable
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckolib.misc.ChatPrompt
import gecko10000.geckolib.misc.ItemUtils
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.guis.prompt
import gecko10000.geckorecipes.model.recipe.CustomBlastingRecipe
import gecko10000.geckorecipes.model.recipe.CustomCookingRecipe
import gecko10000.geckorecipes.model.recipe.CustomFurnaceRecipe
import gecko10000.geckorecipes.model.recipe.CustomSmokingRecipe
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject

class CookingRecipeEditGUI(player: Player, private val recipe: CustomCookingRecipe) : GUI(player), MyKoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val recipeManager: RecipeManager by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 27
        private const val nameSlot = 4
        private const val xpSlot = 6
        private const val timeSlot = 8
        private const val inputSlot = 10
        private const val typeSlot = 11
        private const val resultSlot = 12
    }

    private fun getCurrentRecipe(): CustomCookingRecipe {
        val inventoryResult = inventory.inventory.getItem(resultSlot)
        val result = if (ItemUtils.isEmpty(inventoryResult)) recipe.result else inventoryResult!!
        return recipe.copy(result = result)
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
            val currentRecipe = getCurrentRecipe()
            player.closeInventory()
            prompt(
                player,
                "Enter an experience amount".asClickable(currentRecipe.experience.toString()),
                {
                    val newAmount = it.toFloatOrNull() ?: return@prompt run {
                        player.sendMessage(parseMM("<red>Invalid number. Must be a decimal."))
                        CookingRecipeEditGUI(player, currentRecipe)
                    }
                    CookingRecipeEditGUI(player, currentRecipe.copy(experience = newAmount))
                },
                { cancelReason ->
                    if (cancelReason == ChatPrompt.CancelReason.PLAYER_CANCELLED) CookingRecipeEditGUI(
                        player,
                        currentRecipe
                    )
                })
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
            val currentRecipe = getCurrentRecipe()
            player.closeInventory()
            prompt(
                player,
                "Enter a cooking time in ticks".asClickable(currentRecipe.cookingTimeTicks.toString()),
                {
                    val newAmount = it.toIntOrNull() ?: return@prompt run {
                        player.sendMessage(parseMM("<red>Invalid number. Must be an integer."))
                        CookingRecipeEditGUI(player, currentRecipe)
                    }
                    CookingRecipeEditGUI(player, currentRecipe.copy(cookingTimeTicks = newAmount))
                },
                { cancelReason ->
                    if (cancelReason == ChatPrompt.CancelReason.PLAYER_CANCELLED) CookingRecipeEditGUI(
                        player,
                        currentRecipe
                    )
                })
        }
    }

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(Bukkit.createInventory(this, SIZE, plugin.config.editRecipeName(recipe)))
        inventory.fill(0, SIZE, plugin.config.fillerItem)
        inventory.addButton(
            nameSlot,
            guiComponents.nameButton(
                player,
                recipe,
                this::getCurrentRecipe,
                callback = { CookingRecipeEditGUI(player, it) },
                cancelCallback = { CookingRecipeEditGUI(player, recipe) })
        )
        inventory.addButton(xpSlot, xpButton())
        inventory.addButton(timeSlot, timeButton())
        inventory.addButton(
            inputSlot,
            guiComponents.editRecipeChoiceButton(player, this::getCurrentRecipe, recipe.input) { r, c ->
                CookingRecipeEditGUI(
                    player,
                    r.copy(input = c ?: recipe.input)
                )
            })
        val furnace = ItemStack(
            when (recipe) {
                is CustomFurnaceRecipe -> Material.FURNACE
                is CustomBlastingRecipe -> Material.BLAST_FURNACE
                is CustomSmokingRecipe -> Material.SMOKER
            }
        ).apply {
            editMeta {
                it.displayName(parseMM("<green>Cooking Recipe Type"))
            }
        }
        inventory.addButton(typeSlot, ItemButton.create(furnace) { _ ->
            val nextRecipe = when (recipe) {
                is CustomFurnaceRecipe -> CustomBlastingRecipe(
                    recipe.id,
                    recipe.name,
                    recipe.result,
                    recipe.category,
                    recipe.requiresPermission,
                    recipe.input,
                    recipe.experience,
                    recipe.cookingTimeTicks
                )

                is CustomBlastingRecipe -> CustomSmokingRecipe(
                    recipe.id,
                    recipe.name,
                    recipe.result,
                    recipe.category,
                    recipe.requiresPermission,
                    recipe.input,
                    recipe.experience,
                    recipe.cookingTimeTicks
                )

                is CustomSmokingRecipe -> CustomFurnaceRecipe(
                    recipe.id,
                    recipe.name,
                    recipe.result,
                    recipe.category,
                    recipe.requiresPermission,
                    recipe.input,
                    recipe.experience,
                    recipe.cookingTimeTicks
                )

            }
            CookingRecipeEditGUI(player, nextRecipe)
        })
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
