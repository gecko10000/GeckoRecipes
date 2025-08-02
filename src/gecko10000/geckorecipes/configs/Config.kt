@file:UseSerializers(MMComponentSerializer::class)

package gecko10000.geckorecipes.configs

import com.charleskorn.kaml.YamlComment
import gecko10000.geckolib.config.objects.DisplayItem
import gecko10000.geckolib.config.serializers.MMComponentSerializer
import gecko10000.geckolib.extensions.MM
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
data class Config(
    val showRecipeIconsWithoutPermission: Boolean = false,
    @YamlComment("Only used when the above setting is true")
    val shownPlaceholderMaterial: Material = Material.BARRIER,
    val viewGuiName: Component = MM.deserialize("<gradient:#7300e6:#af00cc>Custom Recipes"),
    private val viewRecipeName: Component = MM.deserialize("<#8b00cc>Recipe: <name>"),
    private val editRecipeName: Component = MM.deserialize("<dark_green>Editing recipe <id>"),
    val viewRecipeChoiceName: Component = MM.deserialize("<dark_green>Ingredient Options"),
    val prevButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<red>Previous"),
        material = Material.RED_STAINED_GLASS_PANE,
    ),
    val nextButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<green>Next"),
        material = Material.LIME_STAINED_GLASS_PANE,
    ),
    val cancelButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<red>Cancel"),
        material = Material.RED_STAINED_GLASS_PANE,
    ),
    val confirmButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<green>Confirm"),
        material = Material.LIME_STAINED_GLASS_PANE,
    ),
    val backButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<red>Back"),
        material = Material.RED_STAINED_GLASS_PANE,
    ),
    val createButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<white>(<green><bold>+</bold></green>)"),
        material = Material.GREEN_STAINED_GLASS_PANE,
    ),
    private val filler: Material = Material.BLACK_STAINED_GLASS_PANE,
) {
    @Transient
    private val _fillerItem = ItemStack(filler).apply { editMeta { it.displayName(Component.empty()) } }

    val fillerItem
        get() = _fillerItem.clone()

    fun viewRecipeName(recipe: CustomRecipe) =
        viewRecipeName.replaceText { it.matchLiteral("<name>").replacement(recipe.name) }

    fun editRecipeName(recipe: CustomRecipe) =
        editRecipeName.replaceText { it.matchLiteral("<id>").replacement(recipe.id) }
}
