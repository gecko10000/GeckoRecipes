package gecko10000.geckorecipes.guis.edit

import gecko10000.geckolib.GUI
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeExactChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeMaterialChoice
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.itemutils.ItemUtils
import redempt.redlib.misc.EventListener

class RecipeChoiceEditGUI(
    player: Player,
    private val originalRecipeChoice: CustomRecipeChoice?,
    private val recipeChoice: CustomRecipeChoice,
    private val callback: (CustomRecipeChoice?) -> Unit,
) : GUI(player), KoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()

    companion object {
        private const val SIZE = 54
    }

    private fun existingChoiceButton(item: ItemStack): ItemButton {
        return ItemButton.create(item) { _ ->
            val newRecipeChoice = when (recipeChoice) {
                is CustomRecipeExactChoice -> recipeChoice.copy(validItems = recipeChoice.validItems.filterNot { it == item }
                    .toSet())

                is CustomRecipeMaterialChoice -> recipeChoice.copy(validMaterials = recipeChoice.validMaterials.filterNot { it == item.type }
                    .toSet())
            }
            RecipeChoiceEditGUI(player, originalRecipeChoice, newRecipeChoice, callback)
        }
    }

    private fun exactChoiceToggle(): ItemButton {
        val name = parseMM(
            when (recipeChoice) {
                is CustomRecipeExactChoice -> "<red>Exact Item Matching"
                is CustomRecipeMaterialChoice -> "<green>Material Item Matching"
            }
        )
        val item = ItemStack(
            when (recipeChoice) {
                is CustomRecipeExactChoice -> Material.REDSTONE_TORCH
                is CustomRecipeMaterialChoice -> Material.TORCH
            }
        ).apply {
            editMeta {
                it.displayName(name)
            }
        }
        return ItemButton.create(item) { _ ->
            val newRecipeChoice = when (recipeChoice) {
                is CustomRecipeExactChoice -> CustomRecipeMaterialChoice(
                    recipeChoice.validItems.map(ItemStack::getType).toSet()
                )

                is CustomRecipeMaterialChoice -> CustomRecipeExactChoice(
                    recipeChoice.validMaterials.map(::ItemStack).toSet()
                )
            }
            RecipeChoiceEditGUI(player, originalRecipeChoice, newRecipeChoice, callback)
        }
    }

    override fun createInventory(): InventoryGUI {
        val inventory =
            InventoryGUI(Bukkit.createInventory(this, SIZE, MM.deserialize("<dark_green>Edit Recipe Choice")))
        inventory.fill(SIZE - 9, SIZE, plugin.config.fillerItem)
        guiComponents.getDisplayItems(recipeChoice).map(this::existingChoiceButton).forEachIndexed(inventory::addButton)
        inventory.addButton(
            SIZE - 7,
            ItemButton.create(plugin.config.cancelButton.item) { _ -> callback(originalRecipeChoice) })
        inventory.addButton(SIZE - 5, exactChoiceToggle())
        inventory.addButton(
            SIZE - 3,
            ItemButton.create(plugin.config.confirmButton.item) { _ ->
                val newChoice = when (recipeChoice) {
                    is CustomRecipeExactChoice -> if (recipeChoice.validItems.isEmpty()) null else recipeChoice
                    is CustomRecipeMaterialChoice -> if (recipeChoice.validMaterials.isEmpty()) null else recipeChoice
                }
                callback(newChoice)
            })
        val listener = EventListener(InventoryClickEvent::class.java) { e ->
            if (e.inventory.holder !is RecipeChoiceEditGUI || e.whoClicked != player || e.clickedInventory !is PlayerInventory) return@EventListener
            val currentItem = e.currentItem
            if (ItemUtils.isEmpty(currentItem)) return@EventListener
            e.isCancelled = true
            val newRecipeChoice = when (recipeChoice) {
                is CustomRecipeExactChoice -> recipeChoice.copy(validItems = recipeChoice.validItems.plus(currentItem!!.asOne()))
                is CustomRecipeMaterialChoice -> recipeChoice.copy(
                    validMaterials = recipeChoice.validMaterials.plus(
                        currentItem!!.type
                    )
                )
            }
            RecipeChoiceEditGUI(player, originalRecipeChoice, newRecipeChoice, callback)
        }
        inventory.setOnDestroy { listener.unregister() }
        return inventory
    }

}
