package me.kieran.kjcontrol;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.kieran.kjcontrol.command.KJControlCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.List;

/*
    Paper bootstrap entrypoint for KJControl.

    This class is used with Paper's experimental PluginBootstrap API,
    which allows certain setup tasks (like command registration)
    to occur earlier in the server lifecycle than a normal JavaPluign.

    Keeping bootstrap logic separate helps ensure:
    - Commands are registered at the correct lifecycle phase
    - The main plugin class remains focused on runtime behaviour
 */
public class KJControlBootstrap implements PluginBootstrap {

    /*
        Called during the bootstrap phase, before the plugin is fully created.

        This is the safest place to register Brigadier commands when using
        Paper's lifecycle system, as it guarantees commands are available
        before the server finishes enabling plugins.
     */
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(
                    KJControlCommand.buildKJControlCommand,
                    "Main plugin command",
                    List.of("kjc")
            );
        });
    }

    /*
        Creates and returns the main plugin instance.

        Paper calls this after bootstrap has completed, handing control
        over to the standard JavaPlugin lifecycle (onEnable, onDisable, etc).
     */
    @Override
    public @NonNull JavaPlugin createPlugin(@NonNull PluginProviderContext context) {
        return new KJControl();
    }
}
