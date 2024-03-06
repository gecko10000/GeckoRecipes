package gecko10000.geckorecipes

import gecko10000.geckorecipes.guis.GUIComponents
import org.koin.dsl.module

fun pluginModules(plugin: GeckoRecipes) = module {
    single { plugin }
    single(createdAtStart = true) { RecipeManager() }
    single(createdAtStart = true) { CommandHandler() }
    single { GUIComponents() }
}
