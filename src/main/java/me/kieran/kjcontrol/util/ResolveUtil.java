package me.kieran.kjcontrol.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ResolveUtil {

    /*
        Creates a MiniMessage serializer configured for a specific player.

        This serializer:
        - Supports all standard MiniMessage tags
        - Adds custom component placeholders like <username>
        - Can safely deserialize strings from config files
     */
    public static MiniMessage getSerializer(Player player) {

        /*
            Start building a custom MiniMessage instance.

            Using a builder instead of MiniMessage.miniMessage()
            allows us to inject our own tag resolvers.
         */
        return MiniMessage.builder()
                // Define which tags MiniMessage should understand.
                .tags(TagResolver.builder()

                        /*
                            Register all default MiniMessage tags:
                            <color>, <bold>, <hover>, <click>, <gradient>, etc.
                         */
                        .resolver(StandardTags.defaults())

                        /*
                            Register a custom placeholder: <username>

                            This resolves to the player's name
                            as an Adventure Component.
                         */
                        .resolver(Placeholder.component(
                                "username",
                                player.name()
                        ))

                        /*
                            Register another custom placeholder:
                            <displayname>

                            This resolves to the player's display name,
                            preserving any colours or formatting.
                         */
                        .resolver(Placeholder.component(
                                "displayname",
                                player.displayName()
                        ))

                        // Finish building the TagResolver
                        .build()
                )
                // Finish building the MiniMessage serializer
                .build();
    }

    /*
        Applies PlaceholderAPI placeholders to a list of strings.

        This method:
        - Accepts any number of input strings
        - Replaces %placeholders% using PlaceholderAPI
        - Returns the resolved strings in the same order
     */
    public static List<String> applyPlaceholders(
            Player player,
            String... inputs
    ) {

        /*
            Convert the varargs array into a stream,
            process each string, and collect the results.
         */
        return Arrays.stream(inputs)
                /*
                    For each string, apply PlaceholderAPI placeholders
                    using the given player as context.
                 */
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                // Collect the resolved strings into an immutable list.
                .toList();
    }

}
