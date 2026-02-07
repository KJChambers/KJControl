package me.kieran.kjcontrol.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.kieran.kjcontrol.util.CommandUtil;

public class KJControlCommand {

    public static LiteralCommandNode<CommandSourceStack> buildKJControlCommand = Commands.literal("kjcontrol")
            .executes(CommandUtil::executeMain)
            .then(Commands.literal("preview")
                    .executes(CommandUtil::executePreview)
            )
            .then(Commands.literal("reload")
                    .executes(CommandUtil::executeReload)
            )
            .then(Commands.literal("help")
                    .executes(CommandUtil::executeHelp)
            )
            .build();
}
