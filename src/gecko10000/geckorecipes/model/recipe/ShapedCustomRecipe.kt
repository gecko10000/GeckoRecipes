@file:UseSerializers(ItemStackSerializer::class, MMComponentSerializer::class)

package gecko10000.geckorecipes.model.recipe

import gecko10000.geckoconfig.serializers.ItemStackSerializer
import gecko10000.geckoconfig.serializers.MMComponentSerializer
import gecko10000.geckorecipes.model.recipechoice.CustomRecipeChoice
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
data class ShapedCustomRecipe(
    override val id: String,
    override val name: Component,
    override val result: ItemStack,
    override val category: CraftingBookCategory = CraftingBookCategory.MISC,
    val ingredients: List<CustomRecipeChoice?>,
) : CustomRecipe {
    override fun getRecipe(): ShapedRecipe {
        val charMap = mutableMapOf<Char, CustomRecipeChoice>()
        val shapeStrings = mutableListOf<String>()
        var currentChar = 'a'
        for (i in 0..<3) {
            val shapeBuilder = StringBuilder()
            for (j in 0..<3) {
                val ingredient = ingredients[i * 3 + j]
                if (ingredient == null) {
                    shapeBuilder.append(' ')
                } else {
                    shapeBuilder.append(currentChar)
                    charMap[currentChar] = ingredient
                    currentChar++
                }
            }
            shapeStrings.add(shapeBuilder.toString())
        }
        return ShapedRecipe(key, result).apply {
            this.shape(*shapeStrings.shaveOutsides().toTypedArray())
            charMap.forEach { (c, choice) -> this.setIngredient(c, choice.getRecipeChoice()) }
        }
    }

    private fun List<String>.shaveOutsides(): List<String> {
        return this.shaveSides().transpose().shaveSides().transpose()
    }

    private fun List<String>.shaveSides(): List<String> {
        val excludedIndices = mutableSetOf<Int>()
        var i = 0
        do {
            val isEmpty = this[i].trim().isEmpty()
            if (isEmpty) excludedIndices.add(i)
            i++
        } while (isEmpty)
        i = this.size - 1
        do {
            val isEmpty = this[i].trim().isEmpty()
            if (isEmpty) excludedIndices.add(i)
            i--
        } while (isEmpty)
        return this.filterIndexed { index, _ -> index !in excludedIndices }
    }

    // ["ab", "de", "gh"] -> ["adg", "beh"]
    private fun List<String>.transpose(): List<String> {
        val result = mutableListOf<String>()
        for (i in 0..<this[0].length) {
            val builder = StringBuilder()
            for (j in this.indices) {
                builder.append(this[j][i])
            }
            result.add(builder.toString())
        }
        return result
    }

}
