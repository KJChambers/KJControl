package me.kieran.kjcontrol.record;

import net.kyori.adventure.text.Component;

/*
    Represents a fully-resolved, player-specific chat format.

    This record exists to separate:
    - Raw configuration data (ChatFormat)
    - From rendered, interactive Adventure Components

    By the time a ResolvedChatFormat is created:
    - All PlaceholderAPI placeholders have been applied
    - All MiniMessage tags have been deserialized
    - Hover and click events have been conditionally attached
    - The chat message has been correctly merged with the suffix

    This allows chat listeners to work with a simple, safe
    and immutable object that is ready to be appended directly
    to the chat output without any further processing.
 */
public record ResolvedChatFormat(
        Component prefix,
        Component name,
        Component suffixMessage
) {}
