package me.kieran.kjcontrol.config;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.util.PluginMessagesUtil;

public final class Config {

    /*
        The latest version of config.yml that this plugin expects.

        Used to warn server owners if they are running an outdated
        configuration file that may be missing new options.
     */
    private static final int LATEST_CONFIG_VERSION = 1;

    /*
        Feature toggle flags read from config.yml.

        These values control whether optional systems
        (like chat formatting or messages) are enabled.

        They are static so they can be accessed
        without passing a Config instance around.
     */
    static boolean chatFormatEnabled;
    static boolean messagesEnabled;

    /*
        Loads (or reloads) the main config.yml file.

        This method is typically called:
        - when the plugin enables
        - when the plugin is reloaded via command

        It is responsible for:
        - creating the default config if it doesn't exist
        - reading feature flags
        - loading dependent systems based on those flags
     */
    static void load() {

        // Get the main plugin instance so we can access config and logging.
        KJControl plugin = KJControl.getInstance();

        try {
            /*
                Ensure the default config.yml exists.
                If it doesn't, it will be copied from the plugin JAR.
             */
            plugin.saveDefaultConfig();

            /*
                Reload the config.yml from disk.
                This ensures any changes made by the server owner
                are applied immediately.
             */
            plugin.reloadConfig();

            /*
                Read the configuration version number.
                This allows us to warn if the config is not
                the latest version.
             */
            int configVersion = plugin.getConfig().getInt("config-version");
            if (configVersion != LATEST_CONFIG_VERSION) plugin.getComponentLogger().warn(
                    "KJControl/config.yml version mismatch. Expected {}, found {}",
                    LATEST_CONFIG_VERSION,
                    configVersion
            );

            /*
                Read feature toggles from the configuration.

                The second argument (false) is the default value
                used if the path is missing from the config.
             */
            chatFormatEnabled = plugin.getConfig().getBoolean("features.enable-chat-format", false);
            messagesEnabled = plugin.getConfig().getBoolean("features.messages.enabled", false);

        } catch (Exception e) {
            /*
                Catch any unexpected errors during config loading.

                This prevents the plugin from crashing and provides
                useful debugging information in the console.
             */
            plugin.getComponentLogger().error("config.yml failed to load.");
            plugin.getComponentLogger().error(PluginMessagesUtil.defaultErrorMessage(e));
        }

    }

}
