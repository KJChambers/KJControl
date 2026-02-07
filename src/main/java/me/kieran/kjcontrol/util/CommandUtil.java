package me.kieran.kjcontrol.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.menu.KJControlMenu;
import me.kieran.kjcontrol.record.HelpEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandUtil {

    public static int executeMain(CommandContext<CommandSourceStack> ctx) {
        KJControlMenu menu = new KJControlMenu(KJControl.getInstance());
        CommandSender sender = ctx.getSource().getSender();
        if (sender instanceof Player) {
            ((Player) sender).openInventory(menu.getInventory());
        } else {
            sender.sendMessage("Only players can do this!");
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int executePreview(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        sender.sendMessage(
                ChatFormatUtil.getFormat(sender).append(
                        Component.text("Only you can see this preview!")
                )
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int executeReload(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        ConfigUtil.load(sender);
        return Command.SINGLE_SUCCESS;
    }

    public static int executeHelp(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        MiniMessage mm = MiniMessage.miniMessage();

        Component message = Component.empty()
                .append(mm.deserialize("<gray>-------- <green>KJControl Help</green> --------</gray>\n"));

        List<HelpEntry> entries = List.of(
                new HelpEntry(
                        "/kjcontrol",
                        "opens the plugin menu",
                        "kjcontrol.admin"
                ),
                new HelpEntry(
                        "/kjcontrol preview",
                        "shows you a preview of the chat format",
                        "kjcontrol.preview"
                ),
                new HelpEntry(
                        "/kjcontrol reload",
                        "reloads the plugin files",
                        "kjcontrol.reload"
                ),
                new HelpEntry(
                        "/kjcontrol help",
                        "shows this menu",
                        null
                )
        );

        for (HelpEntry entry : entries) {
            if (entry.permission() != null && !sender.hasPermission(entry.permission())) {
                continue;
            }

            Component line = mm.deserialize(
                    "<green>" + entry.command() + "</green> <gray>- " + entry.description() + "</gray>"
            ).clickEvent(ClickEvent.runCommand(entry.command()))
                    .hoverEvent(HoverEvent.showText(
                            mm.deserialize("<yellow>Click to run!</yellow>"
                                    + (entry.permission() != null
                                    ? "\n<gray>Permission: " + entry.permission() + "</gray>"
                                    : "")
                            )
                    ));

            message = message.append(line).append(Component.newline());
        }

        message = message.append(
                mm.deserialize("<gray>-----------------------------------</gray>")
        );

        sender.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

}
