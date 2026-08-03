package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class AdminListener implements Listener {

    private final AdminStateManager st;

    public AdminListener(AdminStateManager st) {
        this.st = st;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (st.isGod(p.getUniqueId())) {
            e.setCancelled(true);
            p.setFireTicks(0);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        if (st.isVanish(id)) {
            applyVanish(p, true);
        }
        if (st.isGod(id)) {
            p.setFireTicks(0);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (st.isVanish(p.getUniqueId())) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(p)) {
                    other.showPlayer(p);
                }
            }
        }
    }

    public static void applyVanish(Player p, boolean vanish) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(p)) continue;
            if (vanish) {
                if (!other.hasPermission("sf.admin.seevanished")) {
                    other.hidePlayer(p);
                }
            } else {
                other.showPlayer(p);
            }
        }
    }
}
