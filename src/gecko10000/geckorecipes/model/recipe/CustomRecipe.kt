package gecko10000.geckorecipes.model.recipe

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
sealed interface CustomRecipe {
    val id: String
    val name: Component
    val result: ItemStack
    val category: CraftingBookCategory
    fun getRecipe(): Recipe
    val key: NamespacedKey
        get() = NamespacedKey("geckorecipes", id)
}
