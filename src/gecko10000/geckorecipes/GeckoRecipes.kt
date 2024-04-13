package gecko10000.geckorecipes

import com.charleskorn.kaml.Yaml
import gecko10000.geckoconfig.YamlFileManager
import gecko10000.geckorecipes.configs.Config
import gecko10000.geckorecipes.configs.RecipeHolder
import gecko10000.geckorecipes.model.recipe.CustomBlastingRecipe
import gecko10000.geckorecipes.model.recipe.CustomFurnaceRecipe
import gecko10000.geckorecipes.model.recipe.CustomRecipe
import gecko10000.geckorecipes.model.recipe.CustomSmokingRecipe
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin

class GeckoRecipes : JavaPlugin() {
    companion object {
        const val NAMESPACE = "geckorecipes"
    }

    private lateinit var configFile: YamlFileManager<Config>
    val config: Config
        get() = configFile.value

    private lateinit var recipesFile: YamlFileManager<RecipeHolder>
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
        configFile =
            YamlFileManager(configDirectory = dataFolder, initialValue = Config(), serializer = Config.serializer())
        recipesFile = YamlFileManager(
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
            serializer = RecipeHolder.serializer()
        )
        startKoin {
            modules(pluginModules(this@GeckoRecipes))
        }
    }

}
