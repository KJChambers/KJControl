package me.kieran.kjcontrol.config;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.record.ChatFormat;
import me.kieran.kjcontrol.util.PluginMessagesUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class ChatFormatConfig {

    /*
        The latest supported version of chat-format.yml

        If the version inside the user's files does not match this,
        we warn them that their configuration may be outdated.

        This helps prevent silent breakage when new options
        are introduced in future updates.
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

        If null:
        - The config failed to load
        - Required fields were missing
        - Chat formatting should be treated as disabled

        Consumers should always check via ConfigManager
        rather than accessing this directly.
     */
    private static ChatFormat loadedFormat;

    /*
        Loads (or reloads) chat-format.yml from disk.

        This method:
        1. Ensures the file exists
        2. Loads YAML into memory
        3. Parses values into a ChatFormat object
        4. Validates required fields

        It is expected to be called:
        - During plugin startup
        - During manual reload via command
     */
    public static void load() {
        KJControl plugin = KJControl.getInstance();

        /*
            Defensive developer check

            If this loader is invoked while the feature
            is disabled in the main config, that indicates
            a logical error in the codebase.

            This is NOT a user configuration problem.
         */
        if (ConfigManager.isChatFormatDisabled()) {
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
            if (configVersion != LATEST_CONFIG_VERSION) plugin.getComponentLogger().warn(
                    "KJControl/chat-format.yml version mismatch. Expected {}, found {}",
                    LATEST_CONFIG_VERSION,
                    configVersion
            );

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
            plugin.getComponentLogger().error(PluginMessagesUtil.defaultErrorMessage(e));
        }
    }

    /*
        Returns the currently loaded ChatFormat instance.

        Package-private on purpose:
        Only ConfigManager (or classes in the same package)
        should access raw configuration state.

        Consumers should NOT cache this reference.
     */
    static ChatFormat getRawFormat() {
        return loadedFormat;
    }

}
