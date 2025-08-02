package gecko10000.geckorecipes

import com.charleskorn.kaml.Yaml
import gecko10000.geckolib.config.YamlFileManager
import gecko10000.geckorecipes.configs.Config
import gecko10000.geckorecipes.configs.RecipeHolder
import gecko10000.geckorecipes.di.MyKoinComponent
import gecko10000.geckorecipes.di.MyKoinContext
import gecko10000.geckorecipes.model.recipe.CustomBlastingRecipe
import gecko10000.geckorecipes.model.recipe.CustomFurnaceRecipe
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomSmokingRecipe
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

class GeckoRecipes : JavaPlugin(), MyKoinComponent {
    companion object {
        const val NAMESPACE = "geckorecipes"
    }

    private val commandHandler: CommandHandler by inject()

    private val configFile = YamlFileManager(
        configDirectory = dataFolder,
        initialValue = Config(),
        serializer = Config.serializer(),
    )
    val config: Config
        get() = configFile.value

    private val recipesFile = YamlFileManager(
        configDirectory = dataFolder,
        configName = "recipes.yml",
        stringFormat = Yaml(
            configuration = YamlFileManager.defaultConfiguration,
            serializersModule = SerializersModule {
                polymorphic(
                    CustomRecipe::class,
                    CustomSmokingRecipe::class,
                    CustomSmokingRecipe.serializer()
                )
                polymorphic(
                    CustomRecipe::class,
                    CustomFurnaceRecipe::class,
                    CustomFurnaceRecipe.serializer()
                )
                polymorphic(
                    CustomRecipe::class,
                    CustomBlastingRecipe::class,
                    CustomBlastingRecipe.serializer()
                )
            }),
        initialValue = RecipeHolder(),
        serializer = RecipeHolder.serializer(),
    )
    val recipes: MutableMap<String, CustomRecipe>
        get() = recipesFile.value.recipes

    fun reloadConfigs() {
        configFile.reload()
        recipesFile.reload()
    }

    fun saveConfigs() {
        configFile.save()
        recipesFile.save()
    }

    override fun onEnable() {
        MyKoinContext.init(this)
        commandHandler.register()
    }

}
