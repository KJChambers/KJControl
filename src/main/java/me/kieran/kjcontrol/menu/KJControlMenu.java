package me.kieran.kjcontrol.menu;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.util.MenuUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.jetbrains.annotations.NotNull;

public class KJControlMenu implements InventoryHolder {

    private final Inventory menu;

    public KJControlMenu(KJControl plugin) {
        menu = plugin.getServer().createInventory(this, 9, Component.text("KJControl Menu"));
        menu.setItem(1, MenuUtil.getReloadBlock());
        menu.setItem(3, MenuUtil.getPreviewFormatBlock());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }
}
