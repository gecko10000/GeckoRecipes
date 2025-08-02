package gecko10000.geckorecipes.di

import gecko10000.geckorecipes.CommandHandler
import gecko10000.geckorecipes.GeckoRecipes
import gecko10000.geckorecipes.RecipeManager
import gecko10000.geckorecipes.guis.GUIComponents
import org.koin.dsl.module

fun pluginModules(plugin: GeckoRecipes) = module {
    single { plugin }
    single(createdAtStart = true) { RecipeManager() }
    single(createdAtStart = true) { CommandHandler() }
    single { GUIComponents() }
}
