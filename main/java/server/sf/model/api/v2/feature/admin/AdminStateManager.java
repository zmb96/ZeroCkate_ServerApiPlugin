package server.sf.model.api.v2.feature.admin;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AdminStateManager {

    private final Set<UUID> god = ConcurrentHashMap.newKeySet();
    private final Set<UUID> vanish = ConcurrentHashMap.newKeySet();
    private final Set<UUID> fly = ConcurrentHashMap.newKeySet();

    public boolean toggleGod(Player p) {
        UUID id = p.getUniqueId();
        if (god.contains(id)) {
            god.remove(id);
            return false;
        }
        god.add(id);
        return true;
    }

    public boolean isGod(UUID id) {
        return god.contains(id);
    }

    public void setGod(Player p, boolean on) {
        if (on) god.add(p.getUniqueId());
        else god.remove(p.getUniqueId());
    }

    public boolean toggleVanish(Player p) {
        UUID id = p.getUniqueId();
        if (vanish.contains(id)) {
            vanish.remove(id);
            return false;
        }
        vanish.add(id);
        return true;
    }

    public boolean isVanish(UUID id) {
        return vanish.contains(id);
    }

    public void setVanish(Player p, boolean on) {
        if (on) vanish.add(p.getUniqueId());
        else vanish.remove(p.getUniqueId());
    }

    public Set<UUID> vanished() {
        return vanish;
    }

    public boolean toggleFly(Player p) {
        UUID id = p.getUniqueId();
        if (fly.contains(id)) {
            fly.remove(id);
            return false;
        }
        fly.add(id);
        return true;
    }

    public boolean isFlyToggled(UUID id) {
        return fly.contains(id);
    }
}
