package me.kieran.kjcontrol.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PluginMessagesUtil {

    /*
        Utility class for building commonly used plugin messages.

        This centralises:
        - Error output formatting
        - Permission denial messages

        Keeping message construction here ensures:
        - Consistency across the plugin
        - Easier future formatting improvements
     */

    /*
        Builds a standardised error message component
        from an exception.

        This is used to:
        - log consistent error messages
        - show useful debugging information

        Example output:
        "Cause: NullPointerException... Message: something was null"
     */
    public static Component defaultErrorMessage(Exception e) {

        /*
            Use getSimpleName() to avoid printing the full
            package path of the exception class.
         */
        String cause = e.getClass().getSimpleName();

        /*
            Exception#getMessage() can return null.
            Guard against that to avoid ugly output.
         */
        String message = (e.getMessage() != null)
                ? e.getMessage()
                : "No additional details provided";

        return Component.text("Cause: " + cause + " | Message: " + message);
    }

    /*
        Builds a formatted "no permission" message.

        Uses MiniMessage for colour formatting.


        @param permissionNode The permission the player lacks.
     */
    public static Component noPermissionMessage(String permissionNode) {

        /*
            We explicitly close colour tags to avoid
            accidental colour bleeding into appended components.
         */
        return MiniMessage.miniMessage()
                .deserialize(
                        "<red>You do not have permission: <dark_gray>"
                                + permissionNode
                                + "</dark_gray></red>"
                );
    }

}
