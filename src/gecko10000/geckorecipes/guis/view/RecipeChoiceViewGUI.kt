package gecko10000.geckorecipes.guis.view

import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.misc.ItemUtils
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.guis.GUIComponents
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeExactChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeMaterialChoice
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject

class RecipeChoiceViewGUI(
    player: Player,
    private val choice: CustomRecipeChoice,
    private val backCallback: () -> Unit,
) : GUI(player), MyKoinComponent {

    private val plugin: GeckoRecipes by inject()
    private val guiComponents: GUIComponents by inject()

    override fun createInventory(): InventoryGUI {
        val items = when (choice) {
            is CustomRecipeExactChoice -> choice.validItems
            is CustomRecipeMaterialChoice -> choice.validMaterials.map(::ItemStack)
        }
        val inventorySize = ItemUtils.minimumChestSize(items.size) + 9
        val inventory = InventoryGUI(Bukkit.createInventory(this, inventorySize, plugin.config.viewRecipeChoiceName))
        inventory.fill(0, inventorySize, plugin.config.fillerItem)
        items.forEachIndexed(inventory.inventory::setItem)
        inventory.addButton(inventorySize - 5, guiComponents.backButton(backCallback))
        return inventory
    }

}
