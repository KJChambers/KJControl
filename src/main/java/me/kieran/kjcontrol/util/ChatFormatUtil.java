package me.kieran.kjcontrol.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kieran.kjcontrol.KJControl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public final class ChatFormatUtil {

    private static File file;
    private static FileConfiguration config;

    public static final class Format {
        public static String prefix;
        public static String name;
        public static String hoverName;
        public static String clickName;
        public static String suffix;
    }

    public static void load() {
        KJControl plugin = KJControl.getInstance();

        if (!ConfigUtil.CHAT_FORMAT_ENABLED) {
            throw new IllegalStateException(
                    "ChatFormatConfig accessed while feature disabled"
            );
        }
        try {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "chat-format.yml");
            }

            if (!file.exists()) {
                plugin.saveResource("chat-format.yml", false);
            }

            config = YamlConfiguration.loadConfiguration(file);

            Format.prefix = config.getString("format.prefix");
            Format.name = config.getString("format.name");
            Format.hoverName = config.getString("format.name_hover");
            Format.clickName = config.getString("format.name_click");
            Format.suffix = config.getString("format.suffix");

            if (Format.name.isEmpty())
                plugin.getLogger().warning("chat-format.yml: format.name is empty - chat names will be blank");
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("chat-format.yml is malformed (invalid YAML)");
            plugin.getLogger().severe(e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load chat-format.yml");
            plugin.getLogger().severe("Cause: " + e.getCause() + "... Message: " + e.getMessage());
        }
    }

    public static boolean isLoaded() {
        return config != null;
    }

    public static Component getFormat(Player player) {
        MiniMessage serializer = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(Placeholder.component(
                                "username",
                                player.name()
                        ))
                        .resolver(Placeholder.component(
                                "displayname",
                                player.displayName()
                        ))
                        .build()
                )
                .build();

        String prefix = PlaceholderAPI.setPlaceholders(player, Format.prefix);
        String name = PlaceholderAPI.setPlaceholders(player, Format.name);
        String hoverName = PlaceholderAPI.setPlaceholders(player, Format.hoverName);
        String clickName = PlaceholderAPI.setPlaceholders(player, Format.clickName);
        String suffix = PlaceholderAPI.setPlaceholders(player, Format.suffix);

        Component prefixComp = serializer.deserialize(prefix);
        Component nameComp = serializer.deserialize(name)
                .hoverEvent(HoverEvent.showText(serializer.deserialize(hoverName)))
                .clickEvent(ClickEvent.runCommand(clickName));
        Component suffixComp = serializer.deserialize(suffix);

        return prefixComp.append(nameComp).append(suffixComp);
    }

    public static Component getFormat(CommandSender player) {
        return getFormat((Player) player);
    }

}
