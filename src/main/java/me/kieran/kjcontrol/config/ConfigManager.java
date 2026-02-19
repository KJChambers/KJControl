package me.kieran.kjcontrol.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.record.ResolvedChatFormat;
import me.kieran.kjcontrol.util.PluginMessagesUtil;
import me.kieran.kjcontrol.util.ResolveUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigManager {

    /*
        Central orchestration class for all configuration-related logic.

        This class:
        - Coordinates loading of all config files
        - Exposes safe accessors for runtime systems
        - Builds Adventure Components from raw config values

        Other parts of the plugin (listeners, commands, menus)
        should interact ONLY with this class - never directly
        with YAML or raw config classes.
     */

    /*
        We intentionally fetch the plugin instance lazily
        instead of storing it in a static field.

        This avoids potential initialisation order issues
        during plugin startup.
     */
    private static KJControl plugin() {
        return KJControl.getInstance();
    }

    /*
        Loads all configuration files.

        This method:
        - Loads main config
        - Loads chat format if enabled
        - Loads messages if enabled

        Called during:
        - Plugin startup
        - Manual reload command
     */
    public static void loadConfigs() {
        try {

            // Load main config first (feature toggles live here)
            Config.load();

            // Only load chat format if feature enabled.
            if (!isChatFormatDisabled()) ChatFormatConfig.load();

            // Only load messages if feature enabled.
            if (!areMessagesDisabled()) MessagesConfig.load();

        } catch (Exception e) {
            plugin().getComponentLogger().error("Failed to load plugin configs.");
            plugin().getComponentLogger().error(PluginMessagesUtil.defaultErrorMessage(e));
        }
    }

    /*
        Reload wrapper that provides user feedback.

        Called when reload is triggered via command
     */
    public static void loadConfigs(CommandSender player) {
        try {
            /*
                Call the main loadConfigs() method to actually reload
                the configuration and dependent systems.
             */
            loadConfigs();

            /*
                If the reload was triggered by a player,
                send them a confirmation message.
             */
            if (player instanceof Player) {
                player.sendMessage("Config reloaded successfully!");
                // Log who reloaded the config for auditing purposes.
                plugin().getComponentLogger().info("{} reloaded the config!", player.getName());
            } else {
                /*
                    If the reload was triggered by console,
                    just log a generic success message.
                 */
                plugin().getComponentLogger().info("Config reloaded successfully!");
            }
        } catch (Exception e) {
            /*
                If something goes wrong during reload,
                inform the player (if applicable) and log the error.
             */
            if (player instanceof Player) player.sendMessage("Failed to reload config! See console.");
            plugin().getComponentLogger().error(PluginMessagesUtil.defaultErrorMessage(e));
        }
    }

    /*
        Feature state helpers
     */

    public static void setMessagesEnabled(boolean state, CommandSender sender) {
        plugin().getConfig().set("features.messages.enabled", state);
        plugin().saveConfig();
        Config.messagesEnabled = state;

        if (sender instanceof Player player) {
            player.closeInventory();
            player.sendMessage(
                    "Messages " + (state ? "Enabled" : "Disabled")
            );
        } else {
            plugin().getComponentLogger().info(Component.text(
                    "Messages " + (state ? "Enabled" : "Disabled")
            ));
        }
    }

    public static boolean areMessagesDisabled() {
        return !Config.messagesEnabled;
    }

    public static boolean areMessagesLoaded() {
        return MessagesConfig.getConfig() != null;
    }

    public static boolean isJoinQuitDisabled() {
        return !MessagesConfig.joinQuitEnabled;
    }

    public static void setChatFormatEnabled(boolean state, CommandSender sender) {
        plugin().getConfig().set("features.enable-chat-format", state);
        plugin().saveConfig();
        Config.chatFormatEnabled = state;

        if (sender instanceof Player player) {
            player.closeInventory();
            player.sendMessage(
                    "Chat Format " + (state ? "Enabled" : "Disabled")
            );
        } else {
            plugin().getComponentLogger().info(Component.text(
                    "Chat Format " + (state ? "Enabled" : "Disabled")
            ));
        }
    }

    public static boolean isChatFormatDisabled() {
        return !Config.chatFormatEnabled;
    }

    public static boolean isChatFormatLoaded() {
        return ChatFormatConfig.getRawFormat() != null;
    }

    /*
        Builds the fully formatted chat message component.

        This method:
        1. Retrieves raw ChatFormat data
        2. Resolves placeholders
        3. Combines prefix + name + suffix+message

        Returns null if chat format is unavailable.
     */
    public static Component getChatFormat(Player player, Component message) {
        /*
            Resolve the raw ChatFormat into a ResolvedChatFormat
            that contains fully built Adventure Components.
         */
        ResolvedChatFormat format = ChatFormatConfig.getRawFormat().resolve(player, message);

        /*
            Combine the prefix, name, and suffix+message
            into the final chat message component.
         */
        return format.prefix()
                .append(format.name())
                .append(format.suffixMessage());
    }

    /*
        Builds the join message component for a player.

        Returns null if no join message is configured.
     */
    public static Component getJoinMessage(Player player) {
        // No message configured -> nothing to send
        if (MessagesConfig.Messages.join_message == null) return null;

        /*
            Get a MiniMessage serializer configured for
            this specific player (placeholders, resolvers, etc.)
         */
        MiniMessage serializer = ResolveUtil.getSerializer(player);

        /*
            Apply PlaceholderAPI placeholders to the raw string and
            deserialize the string into an Adventure Component
        */
        return serializer.deserialize(
                PlaceholderAPI.setPlaceholders(player, MessagesConfig.Messages.join_message)
        );
    }

    /*
        Builds the quit message component for a player.

        Returns null if no quit message is configured.
     */
    public static Component getQuitMessage(Player player) {
        // No message configured -> nothing to send
        if (MessagesConfig.Messages.quit_message == null) return null;

        MiniMessage serializer = ResolveUtil.getSerializer(player);

        return serializer.deserialize(
                PlaceholderAPI.setPlaceholders(player, MessagesConfig.Messages.quit_message)
        );
    }
}
