package gecko10000.geckorecipes

import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckorecipes.guis.edit.RecipesEditGUI
import gecko10000.geckorecipes.guis.view.RecipesViewGUI
import org.bukkit.command.CommandSender
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
    fun editCommand(player: Player) = RecipesEditGUI(player)

    @CommandHook("reload")
    fun reloadCommand(sender: CommandSender) {
        plugin.reloadConfigs()
        sender.sendMessage(parseMM("<green>Configs reloaded."))
    }

}
