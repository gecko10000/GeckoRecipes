package gecko10000.geckorecipes.di

import gecko10000.geckorecipes.GeckoRecipes
import org.koin.core.Koin
import org.koin.dsl.koinApplication

object MyKoinContext {
    internal lateinit var koin: Koin
    fun init(plugin: GeckoRecipes) {
        koin = koinApplication(createEagerInstances = false) {
            modules(pluginModules(plugin))
        }.koin
        koin.createEagerInstances()
    }
}
