package me.kieran.kjcontrol.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.config.ConfigManager;
import me.kieran.kjcontrol.menu.ConfigMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ActionUtil {

    public static void reload(CommandSender player) {
        ConfigManager.loadConfigs(player);
    }

    public static int reload(CommandContext<CommandSourceStack> ctx) {
        reload(ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }

    public static void preview(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can preview chat format!");
            return;
        }

        player.sendMessage(
                ConfigManager.getChatFormat(player, Component.text("Only you can see this preview!"))
        );
    }

    public static int preview(CommandContext<CommandSourceStack> ctx) {
        preview(ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }

    public static void editConfig(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can edit the config");
            return;
        }

        ConfigMenu menu = new ConfigMenu(KJControl.getInstance());
        player.openInventory(menu.getInventory());
    }

    public static int editConfig(CommandContext<CommandSourceStack> ctx) {
        editConfig(ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }

}
