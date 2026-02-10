package me.kieran.kjcontrol.record;

import me.kieran.kjcontrol.util.ResolveUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;

/*
    This class represents the raw, unresolved chat format
    exactly as it exists in chat-format.yml

    - It stores strings, not Components
    - It is immutable (because it's a record)
    - It does no rendering by itself
    - Rendering is delegated to resolve() which produces
      a ResolvedChatFormat
 */
public record ChatFormat(
        String prefix,
        String name,
        String hoverName,
        String clickName,
        String suffix
) {
    /*
        Checks whether this ChatFormat is invalid.

        A format is considered invalid if any of its required
        fields are null, which usually indicates:
        - A missing key in chat-format.yml
        - A config loading error

        Empty strings ("") are allowed and handled separately.
     */
    public boolean isInvalid() {
        return prefix == null
                || name == null
                || hoverName == null
                || clickName == null
                || suffix == null;
    }

    /*
        Resolves this ChatFormat into a fully-rendered format
        for a specific player and message.

        This method:
        1. Applies PlaceholderAPI placeholders
        2. Deserializes MiniMessage tags
        3. Builds interactive components (hover / click)
        4. Returns a ResolvedChatFormat ready to display
     */
    public ResolvedChatFormat resolve(Player player, Component message) {

        /*
            Combine the suffix and the chat message into a single string.

            This is intentional:
            - MiniMessage styles persist forward
            - Appending the message later would lose the suffix colour

            The message Component is converted to plain text
            so it can be safely re-serializes by MiniMessage.
         */
        String suffixMessage = suffix + PlainTextComponentSerializer.plainText().serialize(message);

        /*
            Apply PlaceholderAPI placeholders to all format parts.

            Order matters here:
            0 - prefix
            1 - name
            2 - hoverName
            3 - clickName
            4 - suffix + message
         */
        List<String> resolvedPlaceholders = ResolveUtil.applyPlaceholders(
                player, prefix, name, hoverName, clickName, suffixMessage
        );

        /*
            Create a MiniMessage serializer configured for this player.

            This serializer understands:
            - Standard MiniMessage tags
            - <username> and <displayname>
         */
        MiniMessage serializer = ResolveUtil.getSerializer(player);

        /*
            Convert each resolved string into an Adventure Component.

            Using an array here allows us to reference components
            by index in a predictable, structured way.
         */
        Component[] components = new Component[resolvedPlaceholders.size()];

        /*
            Deserialize every resolved placeholder string
            into a MiniMessage Component
         */
        for (int i = 0; i < resolvedPlaceholders.size(); i++)
            components[i] = serializer.deserialize(resolvedPlaceholders.get(i));

        /*
            Start building the "name" component.

            This is separated because it may gain hover
            and click interactions.
         */
        Component name = components[1];

        /*
            Only add a hover event if hoverName is not empty.

            This allows server owners to disable hover text
            simply by leaving the config value blank.
         */
        if (!hoverName.isEmpty())
            name = name.hoverEvent(
                    HoverEvent.showText(components[2])
            );

        /*
            Only add a click event if clickName is not empty.

            The click action runs a command, so the component
            must be converted to plan text before use.
         */
        if (!clickName.isEmpty())
            name = name.clickEvent(
                    ClickEvent.runCommand(
                            PlainTextComponentSerializer.plainText().serialize(components[3])
                    )
            );

        /*
            Return a fully resolved chat format:
            - prefix component
            - interactive name component
            - suffix + message component

            This object is now safe to append directly
            in the chat renderer.
         */
        return new ResolvedChatFormat(
                components[0],
                name,
                components[4]
        );
    }
}
