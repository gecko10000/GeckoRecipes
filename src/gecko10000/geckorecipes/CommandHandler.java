package gecko10000.geckorecipes;

import gecko10000.geckorecipes.guis.edit.RecipesEditGUI;
import gecko10000.geckorecipes.guis.view.RecipesViewGUI;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.strokkur.commands.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Command("geckorecipes")
@Aliases({"customcrafting", "cc"})
@Permission("geckorecipes.command")
public class CommandHandler {

    private final GeckoRecipes plugin = JavaPlugin.getPlugin(GeckoRecipes.class);

    void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            CommandHandlerBrigadier.register(event.registrar());
        }));
    }

    @Executes("view")
    @Permission("geckorecipes.command.view")
    void view(CommandSender sender, @Executor Player player) {
        new RecipesViewGUI(player);
    }

    @Executes("edit")
    @Permission("geckorecipes.command.edit")
    void edit(CommandSender sender, @Executor Player player) {
        new RecipesEditGUI(player);
    }

    @Executes("reload")
    @Permission("geckorecipes.command.reload")
    void reload(CommandSender sender) {
        plugin.reloadConfigs();
        sender.sendRichMessage("<green>Configs reloaded.");
    }

}
