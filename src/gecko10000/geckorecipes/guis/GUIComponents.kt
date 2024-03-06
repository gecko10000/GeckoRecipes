package gecko10000.geckorecipes.guis

import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.guis.edit.RecipeChoiceEditGUI
import gecko10000.geckorecipes.guis.view.RecipeChoiceViewGUI
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapedRecipe
import gecko10000.geckorecipes.model.recipe.CustomShapelessRecipe
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeExactChoice
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeMaterialChoice
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.misc.ChatPrompt

class GUIComponents : KoinComponent {

    private val plugin: GeckoRecipes by inject()

    fun getDisplayItems(recipeChoice: CustomRecipeChoice) = when (recipeChoice) {
        is CustomRecipeExactChoice -> recipeChoice.validItems
        is CustomRecipeMaterialChoice -> recipeChoice.validMaterials.map(::ItemStack)
    }

    fun viewRecipeChoiceButton(player: Player, recipeChoice: CustomRecipeChoice, callback: () -> Unit): ItemButton {
        val items = getDisplayItems(recipeChoice)
        return ItemButton.create(items.toList().getOrElse(0) { ItemStack(Material.BARRIER) }) { _ ->
            if (items.size > 1) RecipeChoiceViewGUI(
                player,
                recipeChoice,
                callback
            )
        }
    }

    fun editRecipeChoiceButton(
        player: Player,
        givenChoice: CustomRecipeChoice?,
        callback: (CustomRecipeChoice?) -> Unit,
    ): ItemButton {
        val items = givenChoice?.let { getDisplayItems(it) }
        return ItemButton.create(items?.toList()?.getOrNull(0)) { e ->
            val generatedChoice = givenChoice ?: CustomRecipeMaterialChoice()
            val cursor = e.cursor
            val recipeChoice = if (!cursor.isEmpty) {
                when (generatedChoice) {
                    is CustomRecipeExactChoice ->
                        generatedChoice.copy(validItems = generatedChoice.validItems.plus(cursor))

                    is CustomRecipeMaterialChoice ->
                        generatedChoice.copy(validMaterials = generatedChoice.validMaterials.plus(cursor.type))
                }
            } else generatedChoice
            RecipeChoiceEditGUI(
                player,
                givenChoice,
                recipeChoice,
                callback
            )
        }
    }

    fun backButton(callback: () -> Unit): ItemButton =
        ItemButton.create(plugin.config.backButton.item) { _ -> callback() }

    fun nameButton(
        player: Player,
        recipe: CustomRecipe,
        callback: (CustomRecipe) -> Unit,
        cancelCallback: () -> Unit,
    ): ItemButton {
        return ItemButton.create(ItemStack(Material.NAME_TAG).apply {
            editMeta {
                it.displayName(parseMM("<green>Rename recipe"))
                it.lore(
                    listOf(
                        parseMM("<gray>Current value:"),
                        recipe.name.withDefaults()
                    )
                )
            }
        }) { _ ->
            player.closeInventory()
            val deserializedName = MM.serialize(recipe.name)
            ChatPrompt.prompt(player, "Enter a new name for recipe ${recipe.id} (previous: \"$deserializedName\")", {
                val name = MM.deserialize(it)
                val newRecipe = when (recipe) {
                    is CustomShapedRecipe -> recipe.copy(name = name)
                    is CustomShapelessRecipe -> recipe.copy(name = name)
                }
                callback(newRecipe)
            }) {
                if (it == ChatPrompt.CancelReason.PLAYER_CANCELLED) {
                    cancelCallback()
                }
            }
        }
    }

    fun togglePermissionButton(recipe: CustomRecipe, callback: () -> Unit): ItemButton {
        val item = ItemStack(if (recipe.requiresPermission) Material.ENDER_PEARL else Material.ENDER_EYE).apply {
            editMeta {
                it.displayName(parseMM("<green>Toggle permission"))
                it.lore(
                    listOf(
                        parseMM("<gray>Current value:"),
                        parseMM(if (recipe.requiresPermission) "<green>Required" else "<red>Not required"),
                    )
                )
            }
        }
        return ItemButton.create(item) { _ -> callback() }
    }

}
