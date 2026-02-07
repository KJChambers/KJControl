package me.kieran.kjcontrol.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.kieran.kjcontrol.util.ChatFormatUtil;
import me.kieran.kjcontrol.util.ConfigUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        if (!ConfigUtil.CHAT_FORMAT_ENABLED || !ChatFormatUtil.isLoaded()) return;

        event.renderer((source, _, message, _) -> ChatFormatUtil.getFormat(source).append(message));
    }

}
