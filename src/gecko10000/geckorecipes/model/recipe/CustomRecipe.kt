package gecko10000.geckorecipes.model.recipe

import gecko10000.geckorecipes.GeckoRecipes
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
sealed interface CustomRecipe {
    val id: String
    val name: Component
    val result: ItemStack
    val category: CraftingBookCategory
    val requiresPermission: Boolean
    fun getRecipe(): Recipe
    val key: NamespacedKey
        get() = NamespacedKey(GeckoRecipes.NAMESPACE, id)

    fun hasPermission(player: Player) = !requiresPermission || player.hasPermission("geckorecipes.recipe.$id")
}
