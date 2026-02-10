package me.kieran.kjcontrol.util;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.record.ChatFormat;
import me.kieran.kjcontrol.record.ResolvedChatFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public final class ChatFormatUtil {

    /*
        The latest version number of the chat-format.yml file
        that this plugin expects.

        This allows us to warn users if they are using an old
        configuration file that may be missing new options
     */
    private static final int LATEST_CONFIG_VERSION = 1;

    /*
        Reference to the chat-format.yml file on disk.

        This is stored statically so:
        - it can be reused across reloads
        - the file path only needs to be resolved once
     */
    private static File file;

    /*
        The currently loaded chat format configuration.

        If this is null, chat formatting is considered disabled
        or failed to load correctly.
     */
    private static ChatFormat loadedFormat;

    /*
        Loads (or reloads) the chat-format.yml file and parses it
        into a ChatFormat object.

        This method is called:
        - when the plugin enables
        - when the plugin is reloaded via command
     */
    public static void load() {
        KJControl plugin = KJControl.getInstance();

        /*
            Defensive check to ensure this class is not used
            when the chat format feature is disabled in config.yml.

            If this happens, it indicates a developer error,
            not a user misconfiguration.
         */
        if (!ConfigUtil.chatFormatEnabled) {
            throw new IllegalStateException(
                    "Chat Format loader accessed while feature disabled"
            );
        }

        try {
            /*
                If we haven't already created the File reference,
                do so now, using the plugin's data folder.
             */
            if (file == null) {
                file = new File(plugin.getDataFolder(), "chat-format.yml");
            }

            /*
                If the file does not exist yet, copy the default
                version from the plugin JAR into the data folder.
             */
            if (!file.exists()) {
                plugin.saveResource("chat-format.yml", false);
            }

            /*
                Load the YAML file into a FileConfiguration object
                so we can read values from it.
             */
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            /*
                Read the configuration version number from the file.
                This allows us to warn users if their config is outdated.
             */
            int configVersion = config.getInt("chat-format-version");
            if (configVersion != LATEST_CONFIG_VERSION) {
                plugin.getComponentLogger().warn(
                        "KJControl/chat-format.yml is out of date. Please regenerate to avoid unexpected behaviour"
                );
            }

            /*
                Create a ChatFormat object from the configuration values.

                ChatFormat is a simple data container that holds the raw
                MiniMessage strings for each part of the chat format.
             */
            loadedFormat = new ChatFormat(
                config.getString("format.prefix"),
                config.getString("format.name"),
                config.getString("format.name_hover"),
                config.getString("format.name_click"),
                config.getString("format.suffix")
            );

            /*
                Validate the loaded format.

                If any required fields are missing (null),
                the format is considered invalid and chat formatting
                will be disabled.
             */
            if (loadedFormat.isInvalid()) {
                plugin.getComponentLogger().error(
                        "chat-format.yml is invalid. Chat formatting has been disabled."
                );
                loadedFormat = null;
                return;
            }

            /*
                Warn the server owner if the name field is empty.

                This is not an error, but it will result in players'
                names not appearing in chat, which is usually unintended
             */
            if (loadedFormat.name().isEmpty())
                plugin.getComponentLogger().warn("chat-format.yml: format.name is empty - chat names will be blank");
        } catch (Exception e) {
            /*
                Catch any unexpected exceptions that occur while loading.

                This prevents the plugin from crashing the server
                and provides useful debugging information.
             */
            plugin.getComponentLogger().error("Failed to load chat-format.yml");
            plugin.getComponentLogger().error(MessagesUtil.defaultErrorMessage(e));
        }
    }

    /*
        Returns whether a valid chat format is currently loaded.

        Other parts of the plugin can use this to decide whether
        chat formatting should be applied or skipped.
     */
    public static boolean isLoaded() {
        return loadedFormat != null;
    }

    /*
        Builds the full chat format for a specific player and message.

        This method:
        - resolves placeholders for the player
        - deserializes MiniMessage strings into Components
        - applies hover and click events
        - attaches the message to the suffix so colours inherit correctly.
     */
    public static Component getFormat(Player player, Component message) {
        /*
            Resolve the raw ChatFormat into a ResolvedChatFormat
            that contains fully built Adventure Components.
         */
        ResolvedChatFormat format = loadedFormat.resolve(player, message);

        /*
            Combine the prefix, name, and suffix+message
            into the final chat message component.
         */
        return format.prefix()
                .append(format.name())
                .append(format.suffixMessage());
    }

    /*
        Convenience overload for CommandSender.

        This allows commands or other systems to call GetFormat
        without manually casting to Player.

        NOTE: This method assumes the sender is a Player
     */
    public static Component getFormat(CommandSender player, Component message) {
        return getFormat((Player) player, message);
    }

}
