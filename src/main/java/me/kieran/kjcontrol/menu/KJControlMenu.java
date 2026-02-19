package me.kieran.kjcontrol.menu;

import me.kieran.kjcontrol.KJControl;
import me.kieran.kjcontrol.util.MenuUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.jetbrains.annotations.NotNull;

/*
    Represents the main graphical menu (GUI) for the KJControl plugin.

    This class:
    - Creates a custom inventory
    - Defines which items appear in specific slots
    - Acts as the InventoryHolder so interactions can be identified

    The menu itself contains no click-handling logic;
    it only defines layout and contents.
 */
public class KJControlMenu implements InventoryHolder {

    /*
        The Inventory backing this menu.

        Stored as a field so it can be returned later
        when the menu is opened for a player.
     */
    private final Inventory menu;

    /*
        Constructs the KJControl menu.

        @param plugin The main plugin instance, used to
                      access the server and create inventories.
     */
    public KJControlMenu(KJControl plugin) {

        /*
            Create a new inventory with:
            - This class as the InventoryHolder
            - 9 slots (a single row)
            - A custom title shown at the top of the GUI
         */
        menu = plugin.getServer().createInventory(this, 9, Component.text("KJControl Menu"));

        /*
            Slot items are zero-based:
            - Slot 0 is the far-left
            - Slot 8 is the far-right
         */
        menu.setItem(1, MenuUtil.getReloadBlock());
        menu.setItem(3, MenuUtil.getPreviewFormatBlock());
        menu.setItem(5, MenuUtil.getConfigBlock());
    }

    /*
        Required method from InventoryHolder interface.

        This allows Bukkit/Paper to associate this Inventory
        with this specific menu instance, which is useful
        for identifying it in InventoryClickEvent handlers.
     */
    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }
}
