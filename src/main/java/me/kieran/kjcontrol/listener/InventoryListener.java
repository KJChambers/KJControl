package me.kieran.kjcontrol.listener;

import me.kieran.kjcontrol.menu.KJControlMenu;
import me.kieran.kjcontrol.util.ChatFormatUtil;
import me.kieran.kjcontrol.util.ConfigUtil;
import me.kieran.kjcontrol.util.MessagesUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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

        /*
            Exit early if:
            - No inventory was clicked
            - The inventory is not owned by KJControlMenu

            Using InventoryHolder is the safest way to
            identify custom menus.
         */
        if (inventory == null || !(inventory.getHolder(false) instanceof KJControlMenu)) {
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
                        clicker.sendMessage(MessagesUtil.noPermissionMessage("kjcontrol.reload"));
                        break;
                    }
                    ConfigUtil.load(clicker);
                    break;
                // Diamond block = Preview chat format
                case Material.DIAMOND_BLOCK:
                    if (!clicker.hasPermission("kjcontrol.preview")) {
                        clicker.sendMessage(MessagesUtil.noPermissionMessage("kjcontrol.preview"));
                        break;
                    }
                    clicker.sendMessage(
                            ChatFormatUtil.getFormat(
                                    clicker,
                                    Component.text("Only you can see this preview!")
                            )
                    );
                    break;
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
