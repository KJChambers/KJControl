package me.kieran.kjcontrol.menu;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.config.ConfigManager;
import me.kieran.kjcontrol.util.MenuUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ConfigMenu implements InventoryHolder {

    private final Inventory menu;

    public ConfigMenu(KJControl plugin) {
        menu = plugin.getServer().createInventory(this, 27, Component.text("Config Editor"));

        if (ConfigManager.isChatFormatLoaded() && !ConfigManager.isChatFormatDisabled()) {
            menu.setItem(2, MenuUtil.getFormatEnabledBlock());
        } else menu.setItem(2, MenuUtil.getFormatDisabledBlock());

        if (ConfigManager.areMessagesLoaded() && !ConfigManager.areMessagesDisabled()) {
            menu.setItem(6, MenuUtil.getMessagesEnabledBlock());
        } else menu.setItem(6, MenuUtil.getMessagesDisabledBlock());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }
}
