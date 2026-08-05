package server.sf.model.api.v2.feature.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import server.sf.model.api.v2.SF;

public class ChatListener implements Listener {

    private final ChatManager manager;

    public ChatListener(ChatManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        Player p = e.getPlayer();
        if (manager.isMuted(p)) {
            SF.sf().msg(p, "§c你已被禁言" + (manager.muteReason(p) != null ? ": " + manager.muteReason(p) : "")
                    + (manager.muteRemaining(p) > 0 ? " §7(剩余 " + manager.muteRemaining(p) + "秒)" : ""));
            e.setCancelled(true);
            return;
        }

        String raw = SF.sf().bukkit().getConsoleSender().getName();
        String message = e.message().toString();
        message = message.replaceAll("^\"|\"$", "");
        message = manager.filterMessage(message);

        ChatManager.ChatChannel channel = manager.getChannel(p);
        String formatted = channel.prefix + manager.format(p, message);

        e.setCancelled(true);

        for (Player recipient : manager.getRecipients(p, channel)) {
            SF.sf().msg(recipient, formatted);
        }
        SF.sf().msg(SF.sf().bukkit().getConsoleSender(), formatted);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        manager.setChannel(e.getPlayer(), "global");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    }
}
