package me.kieran.kjcontrol;

import me.kieran.kjcontrol.listener.ChatListener;
import me.kieran.kjcontrol.listener.InventoryListener;
import me.kieran.kjcontrol.util.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class KJControl extends JavaPlugin {

    private static KJControl instance;

    @Override
    public void onEnable() {
        instance = this;

        ConfigUtil.load();
        getLogger().info("Chat Format Enabled: " + ConfigUtil.CHAT_FORMAT_ENABLED);

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    public static KJControl getInstance() { return instance; }

}