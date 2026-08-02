package server.sf.model.api.v2.feature.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.feature.teleport.TeleportManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TpaManager {

    private final JavaPlugin plugin;
    private final TeleportManager tp;
    private final Map<UUID, TpaRequest> toPlayer = new ConcurrentHashMap<>();
    private final Map<UUID, TpaRequest> fromPlayer = new ConcurrentHashMap<>();

    public TpaManager(JavaPlugin plugin, TeleportManager tp) {
        this.plugin = plugin;
        this.tp = tp;
    }

    public boolean request(Player from, Player to, TpaRequest.Type type) {
        if (from.getUniqueId().equals(to.getUniqueId())) {
            SF.sf().msg(from, "§c不能向自己发起请求");
            return false;
        }
        if (hasPendingTo(to.getUniqueId())) {
            SF.sf().msg(from, "§e" + to.getName() + " §c已有一个待处理请求，请稍后再试");
            return false;
        }
        clearAll(from.getUniqueId());

        int timeoutSec = plugin.getConfig().getInt("teleport.tpa.timeout", 60);
        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            TpaRequest r = toPlayer.remove(to.getUniqueId());
            if (r != null) {
                fromPlayer.remove(r.from);
                Player f = Bukkit.getPlayer(r.from);
                Player t = Bukkit.getPlayer(r.to);
                if (f != null) SF.sf().msg(f, "§c请求已超时");
                if (t != null) SF.sf().msg(t, "§c来自 §e" + f.getName() + " §c的请求已超时");
            }
        }, timeoutSec * 20L).getTaskId();

        TpaRequest req = new TpaRequest(from.getUniqueId(), to.getUniqueId(), type, System.currentTimeMillis(), taskId);
        toPlayer.put(to.getUniqueId(), req);
        fromPlayer.put(from.getUniqueId(), req);

        if (type == TpaRequest.Type.TPA) {
            SF.sf().msg(from, "§a已向 §e" + to.getName() + " §a发起传送请求，超时 §e" + timeoutSec + " §a秒");
            SF.sf().msg(to, "§e" + from.getName() + " §a请求传送到你身边");
        } else {
            SF.sf().msg(from, "§a已邀请 §e" + to.getName() + " §a传送到你身边，超时 §e" + timeoutSec + " §a秒");
            SF.sf().msg(to, "§e" + from.getName() + " §a邀请你传送到他身边");
        }
        SF.sf().msg(to, "§a输入 §e/tpaccept §a接受 或 §e/tpdeny §a拒绝");
        return true;
    }

    public boolean accept(Player to) {
        TpaRequest r = toPlayer.remove(to.getUniqueId());
        if (r == null) {
            SF.sf().msg(to, "§c你没有待处理的请求");
            return false;
        }
        fromPlayer.remove(r.from);
        Bukkit.getScheduler().cancelTask(r.timeoutTaskId);

        Player from = Bukkit.getPlayer(r.from);
        if (from == null) {
            SF.sf().msg(to, "§c请求者已离线");
            return false;
        }
        if (r.isTpHere()) {
            tp.teleportDelayed(to, from.getLocation(), "tpahere", tp.delayFor("tpahere"));
            SF.sf().msg(to, "§a正在传送到 §e" + from.getName());
            SF.sf().msg(from, "§e" + to.getName() + " §a接受了你的邀请");
        } else {
            tp.teleportDelayed(from, to.getLocation(), "tpa", tp.delayFor("tpa"));
            SF.sf().msg(from, "§e" + to.getName() + " §a接受了你的请求");
            SF.sf().msg(to, "§a已接受 §e" + from.getName() + " §a的请求");
        }
        return true;
    }

    public boolean deny(Player to) {
        TpaRequest r = toPlayer.remove(to.getUniqueId());
        if (r == null) {
            SF.sf().msg(to, "§c你没有待处理的请求");
            return false;
        }
        fromPlayer.remove(r.from);
        Bukkit.getScheduler().cancelTask(r.timeoutTaskId);

        Player from = Bukkit.getPlayer(r.from);
        SF.sf().msg(to, "§a已拒绝请求");
        if (from != null) SF.sf().msg(from, "§e" + to.getName() + " §c拒绝了你的请求");
        return true;
    }

    public boolean cancel(Player from) {
        TpaRequest r = fromPlayer.remove(from.getUniqueId());
        if (r == null) {
            SF.sf().msg(from, "§c你没有发起中的请求");
            return false;
        }
        toPlayer.remove(r.to);
        Bukkit.getScheduler().cancelTask(r.timeoutTaskId);

        Player to = Bukkit.getPlayer(r.to);
        SF.sf().msg(from, "§a已取消请求");
        if (to != null) SF.sf().msg(to, "§e" + from.getName() + " §c取消了请求");
        return true;
    }

    public boolean hasPendingTo(UUID to) {
        return toPlayer.containsKey(to);
    }

    public boolean hasPendingFrom(UUID from) {
        return fromPlayer.containsKey(from);
    }

    public TpaRequest pendingTo(UUID to) {
        return toPlayer.get(to);
    }

    public void clearAll(UUID player) {
        TpaRequest r1 = toPlayer.remove(player);
        TpaRequest r2 = fromPlayer.remove(player);
        if (r1 != null) {
            fromPlayer.remove(r1.from);
            Bukkit.getScheduler().cancelTask(r1.timeoutTaskId);
        }
        if (r2 != null) {
            toPlayer.remove(r2.to);
            Bukkit.getScheduler().cancelTask(r2.timeoutTaskId);
        }
    }
}
