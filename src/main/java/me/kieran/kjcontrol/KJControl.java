package me.kieran.kjcontrol;

import me.kieran.kjcontrol.listener.ChatListener;
import me.kieran.kjcontrol.listener.InventoryListener;
import me.kieran.kjcontrol.listener.PlayerListener;
import me.kieran.kjcontrol.util.ChatFormatUtil;
import me.kieran.kjcontrol.util.ConfigUtil;
import me.kieran.kjcontrol.util.MessagesUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/*
    This is the main class of the plugin.

    - It MUST extend JavaPlugin
    - The class name must match the one defined in plugin.yml and paper-plugin.yml
    - This class is created and managed by the server, not by us
 */
public class KJControl extends JavaPlugin {

    /*
        A static reference to the plugin instance.

        This allows other classes (like util classes) to access the plugin
        without passing it through constructors everywhere.

        This is a very common pattern in Bukkit/Spigot/Paper plugins.
     */
    private static KJControl instance;

    /*
        This method is automatically called by the server
        when the plugin is enabled (server start or /reload)
     */
    @Override
    public void onEnable() {

        // Store the plugin instance so it can be access globally
        instance = this;

        /*
            Load the main configuration files.

            ConfigUtil is responsible for:
            - creating config files if they don't exist
            - reading values from config.yml
            - enabling/disabling features based on config
         */
        ConfigUtil.load();

        /*
            Log whether optional systems where successfully loaded.

            getComponentLogger() is Paper's Adventure-based Logger,
            which allows formatted components instead of plain strings.
         */
        getComponentLogger().info("Chat Format Enabled: {}", ChatFormatUtil.isLoaded());
        getComponentLogger().info("Messages Enabled: {}", MessagesUtil.isLoaded());

        /*
            Register event listeners.

            - ChatListener handles chat formatting
            - InventoryListener handles menu interactions
            - PlayerListener handles player-related events (join, quit, etc.)

            Registering them here tells the server:
            "Call these classes when relevant events happen."
         */
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    /*
        This method is automatically called by the server
        when the plugin is disabled (server stop or reload)
     */
    @Override
    public void onDisable() {
        /*
            Unregister all event listeners belonging to this plugin.

            This is good practice to ensure:
            - no memory leaks
            - no events firing after the plugin is disabled.
         */
        HandlerList.unregisterAll(this);
    }

    /*
        Returns the stored plugin instance.

        This allows other classes to call:
        KJControl.getInstance()

        instead of passing the plugin object around manually.
     */
    public static KJControl getInstance() { return instance; }

}