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

    public static ItemStack getReloadBlock() {

        ItemStack reloadBlock = ItemStack.of(Material.EMERALD_BLOCK);
        ItemMeta meta = reloadBlock.getItemMeta();

        if (meta != null) {
            meta.displayName(
                    Component.text("Reload KJControl", NamedTextColor.GREEN)
            );

            meta.lore(List.of(
                    Component.text("Reloads the plugin!", NamedTextColor.GRAY)
            ));

            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            reloadBlock.setItemMeta(meta);
        }

        return reloadBlock;

    }

    public static ItemStack getPreviewFormatBlock() {

        ItemStack previewFormatBlock = ItemStack.of(Material.DIAMOND_BLOCK);
        ItemMeta meta = previewFormatBlock.getItemMeta();

        if (meta != null) {
            meta.displayName(
                    Component.text("Preview Chat Format", NamedTextColor.AQUA)
            );
            meta.lore(List.of(
                    Component.text("Shows you what your chat will look like!", NamedTextColor.GRAY)
            ));

            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            previewFormatBlock.setItemMeta(meta);
        }

        return previewFormatBlock;

    }

}
