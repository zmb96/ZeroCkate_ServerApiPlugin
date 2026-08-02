package server.sf.model.api.v2.feature.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.database.LocationData;
import server.sf.model.api.v2.database.LocationStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> pendingTasks = new ConcurrentHashMap<>();

    public TeleportManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean teleportNow(Player p, Location dest, String action) {
        if (dest == null || dest.getWorld() == null) {
            SF.sf().msg(p, "§c目标位置无效或世界未加载");
            return false;
        }
        saveBack(p);
        if (!p.teleport(dest)) {
            SF.sf().msg(p, "§c传送被取消（可能被其他插件拦截）");
            return false;
        }
        p.playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        return true;
    }

    public void teleportDelayed(Player p, Location dest, String action, int delayTicks) {
        if (delayTicks <= 0) {
            teleportNow(p, dest, action);
            return;
        }
        UUID id = p.getUniqueId();
        if (isPending(id)) {
            SF.sf().msg(p, "§c你已有一个传送正在进行");
            return;
        }
        long cooldown = cooldownMs(action);
        if (cooldown > 0 && !p.hasPermission("sf.teleport.bypass")) {
            long last = cooldowns.getOrDefault(id, 0L);
            long now = System.currentTimeMillis();
            if (now - last < cooldown) {
                long remain = (cooldown - (now - last)) / 1000;
                SF.sf().msg(p, "§c请等待 " + remain + " 秒后再使用传送");
                return;
            }
        }

        SF.sf().msg(p, "§a将在 " + (delayTicks / 20) + " 秒后传送，请勿移动");
        cooldowns.put(id, System.currentTimeMillis());

        Location origin = p.getLocation();
        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            pendingTasks.remove(id);
            Location cur = p.getLocation();
            if (movedSignificantly(origin, cur)) {
                SF.sf().msg(p, "§c你移动了，传送已取消");
                return;
            }
            teleportNow(p, dest, action);
            SF.sf().msg(p, "§a传送完成");
        }, delayTicks).getTaskId();
        pendingTasks.put(id, taskId);
    }

    public boolean isPending(UUID id) {
        return pendingTasks.containsKey(id);
    }

    public void cancelPending(UUID id) {
        Integer tid = pendingTasks.remove(id);
        if (tid != null) {
            Bukkit.getScheduler().cancelTask(tid);
        }
    }

    private boolean movedSignificantly(Location a, Location b) {
        if (a.getWorld() == null || b.getWorld() == null) return true;
        if (!a.getWorld().equals(b.getWorld())) return true;
        return a.distanceSquared(b) > 1.0;
    }

    private long cooldownMs(String action) {
        try {
            int sec = plugin.getConfig().getInt("teleport.cooldown." + action, 0);
            return sec * 1000L;
        } catch (Throwable t) {
            return 0L;
        }
    }

    private int delayTicks(String action) {
        try {
            int sec = plugin.getConfig().getInt("teleport.delay." + action, 0);
            return sec * 20;
        } catch (Throwable t) {
            return 0;
        }
    }

    public int delayFor(String action) {
        return delayTicks(action);
    }

    public void saveBack(Player p) {
        LocationStorage.setLastLocation(p.getUniqueId(), LocationData.of(p.getLocation()));
    }

    public boolean back(Player p) {
        LocationData data = LocationStorage.getLastLocation(p.getUniqueId());
        if (data == null) {
            SF.sf().msg(p, "§c没有可返回的位置");
            return false;
        }
        Location loc = data.toLocation();
        if (loc == null) {
            SF.sf().msg(p, "§c返回位置所在世界未加载");
            return false;
        }
        teleportDelayed(p, loc, "back", delayTicks("back"));
        return true;
    }

    public void clearCooldown(UUID id) {
        cooldowns.remove(id);
    }

    public Map<UUID, Long> snapshotCooldowns() {
        return new HashMap<>(cooldowns);
    }
}
