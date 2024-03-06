package gecko10000.geckorecipes

import gecko10000.geckoconfig.YamlFileManager
import gecko10000.geckorecipes.configs.Config
import gecko10000.geckorecipes.configs.RecipeHolder
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
    val recipeHolder: RecipeHolder
        get() = recipesFile.value

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
            initialValue = RecipeHolder(),
            serializer = RecipeHolder.serializer()
        )
        startKoin {
            modules(pluginModules(this@GeckoRecipes))
        }
    }

}
