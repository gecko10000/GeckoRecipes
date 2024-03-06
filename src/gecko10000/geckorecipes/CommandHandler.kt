package gecko10000.geckorecipes

import gecko10000.geckorecipes.guis.view.RecipesViewGUI
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.commandmanager.CommandHook
import redempt.redlib.commandmanager.CommandParser

class CommandHandler : KoinComponent {

    private val plugin: GeckoRecipes by inject()

    init {
        CommandParser(plugin.getResource("command.rdcml")).parse().register("epicrecipez", this)
    }

    @CommandHook("view")
    fun viewCommand(player: Player) = RecipesViewGUI(player)

    @CommandHook("edit")
    fun editCommand(player: Player) = {}

}
