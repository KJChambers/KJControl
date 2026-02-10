package me.kieran.kjcontrol.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kieran.kjcontrol.KJControl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public final class MessagesUtil {

    /*
        The version of messages.yml that this plugin expects.
        Used to warn server owners if the file is outdated.
     */
    private static final int LATEST_CONFIG_VERSION = 1;

    // The physical messages.yml file on disk.
    private static File file;

    // Bukkit's in-memory representation of messages.yml
    private static FileConfiguration config;

    /*
        Feature toggle for join/quit messages.
        This is read from config.yml, not messages.yml
     */
    private static boolean joinQuitEnabled;

    /*
        Container class for message strings.

        This keeps all message values grouped together
        and avoids polluting MessagesUtil with many fields.
     */
    public static final class Messages {
        public static String join_message;
        public static String quit_message;
    }

    /*
        Loads (or reloads) messages.yml.

        This method should only be called if the messages
        feature is enabled in config.yml
     */
    public static void load() {
        KJControl plugin = KJControl.getInstance();

        /*
            Hard guard: this loader should never be accessed
            when the feature is disabled.

            If this exception ever fires, it means there is
            a logical error elsewhere in the plugin.
         */
        if (!ConfigUtil.messagesEnabled) {
            throw new IllegalStateException(
                    "Messages loader accessed while feature disabled"
            );
        }

        try {
            /*
                Lazily create the File instance.

                We only construct it once and reuse it
                across reloads.
             */
            if (file == null) {
                file = new File(plugin.getDataFolder(), "messages.yml");
            }

            /*
                If messages.yml does not exist, copy it
                from the plugin JAR.
             */
            if (!file.exists()) {
                plugin.saveResource("messages.yml", false);
            }

            // Load the YAML file into memory.
            config = YamlConfiguration.loadConfiguration(file);

            // Version check for messages.yml
            int configVersion = config.getInt("messages-version");
            if (configVersion != LATEST_CONFIG_VERSION) {
                plugin.getComponentLogger().warn(
                        "KJControl/messages.yml is out of date. Please regenerate to avoid unexpected behaviour"
                );
            }

            /*
                Read join/quit feature toggles from config.yml

                This allows server owners to disable join/quit
                messages without deleting them.
             */
            joinQuitEnabled = plugin.getConfig().getBoolean("features.messages.enable-join-quit", true);

            /*
                Only load join/quit messages if the feature
                is enabled.
             */
            if (joinQuitEnabled) {
                Messages.join_message = config.getString("join-message");
                Messages.quit_message = config.getString("quit-message");
            }
        } catch (Exception e) {
            /*
                Catch-all protection to prevent plugin crashes
                due to malformed YAML or IO errors.
             */
            plugin.getComponentLogger().error("Failed to load messages.yml");
            plugin.getComponentLogger().error(defaultErrorMessage(e));
        }

    }

    /*
        Indicates whether messages.yml has been successfully loaded.

        Useful for defensive checks elsewhere in the plugin.
     */
    public static boolean isLoaded() {
        return config != null;
    }

    /*
        Returns true if join/quit messages are disabled.

        The inverted name makes calling code read naturally:
            if (MessagesUtil.isJoinQuitDisabled()) return;
     */
    public static boolean isJoinQuitDisabled() {
        return !joinQuitEnabled;
    }

    /*
        Builds the join message component for a player.

        Returns null if no join message is configured.
     */
    public static Component getJoinMessage(Player player) {
        // No message configured -> nothing to send
        if (Messages.join_message == null) return null;

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
                PlaceholderAPI.setPlaceholders(player, Messages.join_message)
        );
    }

    /*
        Builds the quit message component for a player.

        Returns null if no quit message is configured.
     */
    public static Component getQuitMessage(Player player) {
        // No message configured -> nothing to send
        if (Messages.quit_message == null) return null;

        MiniMessage serializer = ResolveUtil.getSerializer(player);

        return serializer.deserialize(
                PlaceholderAPI.setPlaceholders(player, Messages.quit_message)
        );
    }

    /*
        Builds a default error message component from an exception.

        This is used to:
        - log consistent error messages
        - show useful debugging information

        Example output:
        "Cause: class java.lang.NullPointerException... Message: something was null"
     */
    public static Component defaultErrorMessage(Exception e) {
        return Component.text("Cause: " + e.getClass() + "... Message: " + e.getMessage());
    }

    public static Component noPermissionMessage(String permissionNode) {
        return MiniMessage.miniMessage()
                .deserialize("<red>You do not have permission: <dark_gray>" + permissionNode);
    }

}
