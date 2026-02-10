package me.kieran.kjcontrol.listener;

import me.kieran.kjcontrol.util.ConfigUtil;
import me.kieran.kjcontrol.util.MessagesUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*
    Handles player join and quit events for KJControl.

    This listener is responsible only for deciding *when*
    join/quit messages should be applied.

    The actual message content and formatting are delegated
    to MessagesUtil to keep event logic minimal and consistent.
 */
public class PlayerListener implements Listener {

    /*
        Called when a player joins the server.

        If join/quit messages are disabled (either globally
        or via MessagesUtil), the default join message is preserved.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        /*
            Exit early if:
            - Messages are disabled in the config
            - Join/Quit messages are disabled
         */
        if (!ConfigUtil.messagesEnabled || MessagesUtil.isJoinQuitDisabled()) return;
        Player player = event.getPlayer();

        /*
            Replace the default join message with the
            custom formatted join message.
         */
        event.joinMessage(MessagesUtil.getJoinMessage(player));
    }

    /*
        Called when a player leaves the server.

        Mirrors the join logic, applying a custom quit message
        when join/quit messages are enabled.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!ConfigUtil.messagesEnabled || MessagesUtil.isJoinQuitDisabled()) return;
        Player player = event.getPlayer();
        event.quitMessage(MessagesUtil.getQuitMessage(player));
    }

}
