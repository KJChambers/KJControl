package me.kieran.kjcontrol.listener;

import me.kieran.kjcontrol.menu.KJControlMenu;
import me.kieran.kjcontrol.util.ChatFormatUtil;
import me.kieran.kjcontrol.util.ConfigUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !(inventory.getHolder(false) instanceof KJControlMenu)) {
            return;
        }

        event.setCancelled(true);

        HumanEntity clicker = event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked != null) {

            switch (clicked.getType()) {
                case Material.EMERALD_BLOCK:
                    ConfigUtil.load(clicker);
                    break;
                case Material.DIAMOND_BLOCK:
                    clicker.sendMessage(
                            ChatFormatUtil.getFormat(clicker).append(
                                    Component.text("Only you can see this preview!")
                            )
                    );
                    break;
            }

            clicker.closeInventory();
        }
    }

}
