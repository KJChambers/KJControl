package me.kieran.kjcontrol.util;

import me.kieran.kjcontrol.KJControl;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ConfigUtil {

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

        They are static so they can be accessed from anywhere
        without passing a ConfigUtil instance around.
     */
    public static boolean chatFormatEnabled;
    public static boolean messagesEnabled;

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
    public static void load() {

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
                This allows us to warn if the config is outdated.
             */
            int configVersion = plugin.getConfig().getInt("config-version");
            if (configVersion != LATEST_CONFIG_VERSION) {
                plugin.getComponentLogger().warn(
                        "KJControl/config.yml is out of date. Please regenerate to avoid unexpected behaviour"
                );
            }

            /*
                Read feature toggles from the configuration.

                The second argument (true) is the default value
                used if the path is missing from the config.
             */
            chatFormatEnabled = plugin.getConfig().getBoolean("features.enable-chat-format", true);
            messagesEnabled = plugin.getConfig().getBoolean("features.messages.enabled", true);

            /*
                Conditionally load feature-specific systems
                based on the configuration values.

                This avoids unnecessary file access and logic
                when features are disabled.
             */
            if (chatFormatEnabled) ChatFormatUtil.load();
            if (messagesEnabled) MessagesUtil.load();

        } catch (Exception e) {
            /*
                Catch any unexpected errors during config loading.

                This prevents the plugin from crashing and provides
                useful debugging information in the console.
             */
            plugin.getComponentLogger().error("config.yml failed to load.");
            plugin.getComponentLogger().error(MessagesUtil.defaultErrorMessage(e));
        }

    }

    /*
        Overloaded load method that reloads the config and
        provides feedback to the command sender.

        This is typically called by a reload command.
     */
    public static void load(CommandSender player) {

        // Get the plugin instance for logging and messaging.
        KJControl plugin = KJControl.getInstance();

        try {
            /*
                Call the main load() method to actually reload
                the configuration and dependent systems.
             */
            load();

            /*
                If the reload was triggered by a player,
                send them a confirmation message.
             */
            if (player instanceof Player) {
                player.sendMessage("Config reloaded successfully!");
                // Log who reloaded the config for auditing purposes.
                plugin.getComponentLogger().info("{} reloaded the config!", player.getName());
            } else {
                /*
                    If the reload was triggered by console,
                    just log a generic success message.
                 */
                plugin.getComponentLogger().info("Config reloaded successfully!");
            }
        } catch (Exception e) {
            /*
                If something goes wrong during reload,
                inform the player (if applicable) and log the error.
             */
            if (player instanceof Player) player.sendMessage("Failed to reload config! See console.");
            plugin.getComponentLogger().error(MessagesUtil.defaultErrorMessage(e));
        }
    }

}
