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

    /*
        Handles the base /kjcontrol command.

        This command opens the main KJControl menu
        for players, or shows an error for console senders.
     */
    public static int executeMain(CommandContext<CommandSourceStack> ctx) {

        /*
            Create a new instance of the plugin's main menu.

            This builds an Inventory UI that the player
            can interact with.
         */
        KJControlMenu menu = new KJControlMenu(KJControl.getInstance());

        /*
            Get the Bukkit CommandSender from Brigadier's
            CommandSourceStack.

            This allows the same command to be executed
            by players or the console.
         */
        CommandSender sender = ctx.getSource().getSender();

        /*
            Only players can open inventories.

            The console has no inventory UI, so we guard against that here.
         */
        if (sender instanceof Player) {

            /*
                Cast is safe because of the instanceof check.
                Open the menu inventory for the player.
             */
            ((Player) sender).openInventory(menu.getInventory());

        } else {

            /*
                If the command is run from console,
                explain why it doesn't work.
             */
            sender.sendMessage("Only players can do this!");
        }

        /*
            Brigadier requires a command result.
            SINGLE_SUCCESS indicates the command ran successfully.
         */
        return Command.SINGLE_SUCCESS;
    }

    /*
        Handles /kjcontrol preview.

        This sends the player a preview of the current
        chat format using their own context.
     */
    public static int executePreview(CommandContext<CommandSourceStack> ctx) {

        // Extract the sender from the command context.
        CommandSender sender = ctx.getSource().getSender();

        /*
            Only players can preview chat formats,
            since formatting depends on player data
            like name, display name, and placeholders.
         */
        if (sender instanceof Player)

            /*
                Generate a formatted chat message using
                the current chat format configuration.

                The message text here is only visible
                to the command sender.
             */
            sender.sendMessage(
                    ChatFormatUtil.getFormat(sender, Component.text("Only you can see this preview!"))
            );

        return Command.SINGLE_SUCCESS;
    }

    /*
        Handles /kjcontrol reload

        This reloads all plugin configuration files
        and provides feedback to the sender.
     */
    public static int executeReload(CommandContext<CommandSourceStack> ctx) {
        // Get the command sender
        CommandSender sender = ctx.getSource().getSender();

        /*
            Reload the configuration.

            ConfigUtil handles:
            - reloading config.yml
            - reloading feature configs
            - sending feedback to the sender
         */
        ConfigUtil.load(sender);
        return Command.SINGLE_SUCCESS;
    }

    /*
        Handles /kjcontrol help

        Displays a clickable, permission-aware help menu
        listing all available subcommands.
     */
    public static int executeHelp(CommandContext<CommandSourceStack> ctx) {
        // Get the sender of the command
        CommandSender sender = ctx.getSource().getSender();

        /*
            Use a basic MiniMessage instance for rendering
            colours and formatting in the help menu.
         */
        MiniMessage mm = MiniMessage.miniMessage();

        /*
            Start building the help message.

            Component.empty() gives us a clean base
            to append lines to.
         */
        Component message = Component.empty()
                .append(mm.deserialize("<gray>-------- <green>KJControl Help</green> --------</gray>\n"));

        /*
            Define the help entries for each subcommand.

            Each entry includes:
            - The command
            - A short description
            - An optional permission
         */
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

        /*
            Loop through each help entry and render it
            only if the sender has permission.
         */
        for (HelpEntry entry : entries) {

            // Skips commands the sender does not have permission for.
            if (entry.permission() != null && !sender.hasPermission(entry.permission())) {
                continue;
            }

            /*
                Build a clickable help line.

                Clicking runs the command.
                Hovering shows extra information.
             */
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

            // Append the line and a new line to the message.
            message = message.append(line).append(Component.newline());
        }

        // Add a footer line to close the help menu.
        message = message.append(
                mm.deserialize("<gray>-----------------------------------</gray>")
        );

        // Send the fully-built help message to the sender.
        sender.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

}
