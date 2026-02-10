package me.kieran.kjcontrol.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class MenuUtil {

    /*
        Generic factory method for creating styled menu blocks.

        This method centralises all ItemStack display configuration,
        allowing menu items to be defined declaratively rather than
        duplicating ItemMeta setup logic in multiple places.

        Parameters:
        - material    -> The base material of the item
        - displayName -> The name shown at the top of the tooltip
        - lore        -> The descriptive lines shown below the name
        - isGlowing   -> Whether the item should have a visual glow

        This keeps menu creation clean, reusable, and easy to extend.
     */
    public static ItemStack createBlock(
            Material material, Component displayName,
            List<Component> lore, boolean isGlowing
    ) {
        /*
            Create a new ItemStack using the material param.

            ItemStack.of(...) is a modern, clean way to
            create items in recent Bukkit/Paper versions.
         */
        ItemStack block = ItemStack.of(material);

        /*
            Retrieve the ItemMeta from the ItemStack.

            ItemMeta contains all display-related data:
            - Name
            - Lore
            - Enchantments
            - Flags
         */
        ItemMeta meta = block.getItemMeta();

        /*
            ItemMeta can technically be null for some items,
            so we guard against that to avoid NullPointerExceptions.
         */
        if (meta != null) {

            /*
                Set the display name shown to the player
                when they hover over the item.

                Uses Adventure Components instead of legacy strings.
             */
            meta.displayName(displayName);

            /*
                Sets the lore (description text) under the item name.

                Lore is a list of Components, each one representing
                a line of text.
             */
            meta.lore(lore);

            /*
                Apply a glowing visual effect if requested.

                This does not change gameplay behaviour.
                It simply adds a hidden enchantment so the item
                appears enchanted (glowing) in the inventory.
             */
            if (isGlowing) makeGlow(meta);

            /*
                Apply the modified ItemMeta back onto the ItemStack.

                Changes to ItemMeta are NOT applied automatically.
             */
            block.setItemMeta(meta);
        }

        // Return the fully-built ItemStack
        return block;
    }

    /*
        Creates the predefined "Reload KJControl" menu item.

        This method delegates to createBlock(...) to keep
        styling consistent and avoid repeating meta logic.
     */
    public static ItemStack getReloadBlock() {
        return createBlock(
                Material.EMERALD_BLOCK,
                Component.text("Reload KJControl", NamedTextColor.GREEN),
                List.of(
                        Component.text("Reloads the plugin!", NamedTextColor.GRAY)
                ),
                true
        );
    }

    /*
        Creates the predefined "Preview Chat Format" menu item.

        Like getReloadBlock(), this method defines only
        the visual data while createBlock(...) handles
        the actual ItemStack configuration.
     */
    public static ItemStack getPreviewFormatBlock() {
        return createBlock(
                Material.DIAMOND_BLOCK,
                Component.text("Preview Chat Format", NamedTextColor.AQUA),
                List.of(
                        Component.text(
                                "Shows you what your chat will look like!",
                                NamedTextColor.GRAY
                        )
                ),
                true
        );
    }

    /*
        Applies a hidden enchantment to an ItemMeta
        to produce the enchanted "glow" effect.

        The enchantment is intentionally hidden using
        ItemFlag.HIDE_ENCHANTS so players do not see
        irrelevant enchantment details in the tooltip.
     */
    private static void makeGlow(ItemMeta meta) {
        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

}
