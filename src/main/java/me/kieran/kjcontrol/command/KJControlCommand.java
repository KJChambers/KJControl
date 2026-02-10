package me.kieran.kjcontrol.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.kieran.kjcontrol.util.CommandUtil;

/*
    Defines and builds the root "/kjcontrol" command and all of its subcommands.

    This class acts purely as a command structure definition:
    - No execution logic lives here
    - All behaviour is delegated to CommandUtil

    Using Brigadier allows us to declaratively define permissions,
    subcommands, and execution paths in a clear, hierarchical way
 */
public class KJControlCommand {

    /*
        Pre-built command tree for "/kjcontrol"

        This is registered during plugin startup and includes:
        - Root command permission checks
        - Subcommand definitions
        - Execution handlers
     */
    public static LiteralCommandNode<CommandSourceStack> buildKJControlCommand = Commands.literal("kjcontrol")

            /*
                Base permission required to access the root command.
                If this fails, none of the subcommands are accessible
             */
            .requires(sender -> sender.getSender().hasPermission("kjcontrol.admin"))

            // Executed when "/kjcontrol" is run with no arguments
            .executes(CommandUtil::executeMain)

            /*
                "/kjcontrol preview"

                Shows the executing player a preview of their
                current chat format.
             */
            .then(Commands.literal("preview")
                    .requires(sender -> sender.getSender().hasPermission("kjcontrol.preview"))
                    .executes(CommandUtil::executePreview)
            )

            /*
                "/kjcontrol reload"

                Reloads configuration and cached data without
                restarting the server.
             */
            .then(Commands.literal("reload")
                    .requires(sender -> sender.getSender().hasPermission("kjcontrol.reload"))
                    .executes(CommandUtil::executeReload)
            )

            /*
                "/kjcontrol help"

                Displays available subcommands and descriptions.
                Inherits the root permission requirement.
             */
            .then(Commands.literal("help")
                    .executes(CommandUtil::executeHelp)
            )
            .build();
}
