package me.kieran.kjcontrol.listener;

import me.kieran.kjcontrol.config.ConfigManager;
import me.kieran.kjcontrol.menu.ConfigMenu;
import me.kieran.kjcontrol.menu.KJControlMenu;
import me.kieran.kjcontrol.util.ActionUtil;
import me.kieran.kjcontrol.util.PluginMessagesUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/*
    Listens for inventory click events and handles interactions
    with the KJControl menu.

    This listener:
    - Identifies clicks inside the KJControlMenu
    - Cancels default inventory behaviour
    - Executes actions based on the clicked item
 */
public class InventoryListener implements Listener {

    /*
        Fired whenever a player clicks inside an inventory.

        This method filters events so only clicks inside
        the KJControl menu are handled.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        /*
            Get the inventory that was clicked.

            getClickedInventory() may return null if the player
            clicked outside the inventory window.
         */
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        InventoryHolder holder = inventory.getHolder(false);

        /*
            Exit early if the inventory is not owned
            by KJControlMenu or ConfigMenu

            Using InventoryHolder is the safest way to
            identify custom menus.
         */
        if (!(holder instanceof KJControlMenu)
                && !(holder instanceof ConfigMenu)
        ) {
            return;
        }

        /*
            Cancel the event to prevent players from:
            - Moving items
            - Taking menu items
            - Interacting with the inventory normally
         */
        event.setCancelled(true);

        /*
            Get the entity that clicked the inventory.

            This will usually be a Player, but the type
            is HumanEntity to remain API-safe.
         */
        HumanEntity clicker = event.getWhoClicked();

        /*
            Get the item that was clicked.

            This can be null if the player clicked
            an empty slot.
         */
        ItemStack clicked = event.getCurrentItem();

        // Only continue if an actual item was clicked
        if (clicked != null) {

            if (holder instanceof KJControlMenu) {

                /*
                    Determine which menu item was clicked
                    based on its material type.

                    Since these items are uniquely defined
                    by MenuUtil, this is safe and readable.
                 */
                switch (clicked.getType()) {
                    // Emerald block = Reload config
                    case Material.EMERALD_BLOCK:
                        if (!clicker.hasPermission("kjcontrol.reload")) {
                            clicker.sendMessage(PluginMessagesUtil.noPermissionMessage("kjcontrol.reload"));
                            break;
                        }
                        ActionUtil.reload(clicker);
                        break;
                    // Diamond block = Preview chat format
                    case Material.DIAMOND_BLOCK:
                        if (!clicker.hasPermission("kjcontrol.preview")) {
                            clicker.sendMessage(PluginMessagesUtil.noPermissionMessage("kjcontrol.preview"));
                            break;
                        }
                        ActionUtil.preview(clicker);
                        break;
                    // Iron block = edit config
                    case Material.IRON_BLOCK:
                        if (!clicker.hasPermission("kjcontrol.editconfig")) {
                            clicker.sendMessage(PluginMessagesUtil.noPermissionMessage("kjcontrol.editconfig"));
                            break;
                        }
                        ActionUtil.editConfig(clicker);
                        break;
                }
            } else {
                switch (event.getSlot()) {
                    case 2:
                        ConfigManager.setChatFormatEnabled(ConfigManager.isChatFormatDisabled(), clicker);
                        break;
                    case 6:
                        ConfigManager.setMessagesEnabled(ConfigManager.areMessagesDisabled(), clicker);
                        break;
                }
            }

            /*
                Close the menu after an action is taken.

                This provides immediate feedback and
                prevents repeated clicks.
             */
            clicker.closeInventory();
        }
    }

}
