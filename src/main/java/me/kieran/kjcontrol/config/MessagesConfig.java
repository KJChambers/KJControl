package me.kieran.kjcontrol.config;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.util.PluginMessagesUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class MessagesConfig {

    /*
        The latest supported version of messages.yml

        If the version in the user's file does not match,
        we warn them that their configuration may be outdated.

        This helps prevent silent issues when new options
        are introduced in future plugin updates.
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
    static boolean joinQuitEnabled;

    /*
        Container class for raw message strings.

        This keeps all message values grouped together
        and avoids polluting MessagesConfig with many fields.
     */
    static final class Messages {
        static String join_message;
        static String quit_message;
    }

    /*
        Loads (or reloads) messages.yml.

        Responsibilities:
        1. Ensure file exists
        2. Load YAML into memory
        3. Validate version
        4. Read join/quit messages if enabled

        This method should only be called if the messages
        feature is enabled in config.yml
     */
    public static void load() {
        KJControl plugin = KJControl.getInstance();

        /*
            Developer safeguard.

            If this exception ever fires, it means there is
            a logical error elsewhere in the plugin.
         */
        if (ConfigManager.areMessagesDisabled()) {
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
            if (configVersion != LATEST_CONFIG_VERSION) plugin.getComponentLogger().warn(
                    "KJControl/messages.yml version mismatch. Expected {}, found {}",
                    LATEST_CONFIG_VERSION,
                    configVersion
            );

            /*
                Read join/quit feature toggles from config.yml

                This allows server owners to disable join/quit
                messages without deleting them.
             */
            joinQuitEnabled = plugin.getConfig().getBoolean("features.messages.enable-join-quit", true);

            /*
                Only load message values if the feature is enabled.

                If disabled, clear stored values to prevent
                stale data from being reused.
             */
            if (joinQuitEnabled) {
                Messages.join_message = config.getString("join-message");
                Messages.quit_message = config.getString("quit-message");
            } else {
                Messages.join_message = null;
                Messages.quit_message = null;
            }
        } catch (Exception e) {
            /*
                Catch-all protection to prevent plugin crashes
                due to malformed YAML or IO errors.
             */
            plugin.getComponentLogger().error("Failed to load messages.yml");
            plugin.getComponentLogger().error(PluginMessagesUtil.defaultErrorMessage(e));
        }

    }

    /*
        Returns the underlying FileConfiguration.

        Intended for internal config editing operations only.

        External classes should not manipulate YAML directly -
        they should use ConfigManager instead.
     */
    static FileConfiguration getConfig() {
        return config;
    }

}
