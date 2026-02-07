package me.kieran.kjcontrol.util;

import me.kieran.kjcontrol.KJControl;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public final class ConfigUtil {

    public static boolean CHAT_FORMAT_ENABLED;

    public static void load() {
        KJControl plugin = KJControl.getInstance();

        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            CHAT_FORMAT_ENABLED = plugin.getConfig().getBoolean("features.enable-chat-format", true);
            if (CHAT_FORMAT_ENABLED) {
                ChatFormatUtil.load();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error Message: " + e.getMessage() + "... " + Arrays.toString(e.getStackTrace()));
        }

    }

    public static void load(CommandSender player) {
        KJControl plugin = KJControl.getInstance();

        try {
            load();
            player.sendMessage("Config reloaded successfully!");
            plugin.getLogger().info(player.getName() + " reloaded the config!");
        } catch (Exception e) {
            player.sendMessage("Failed to reload config! See console.");
            plugin.getLogger().severe("Error Message: " + e.getMessage() + "... " + Arrays.toString(e.getStackTrace()));
        }
    }

}
