package server.sf.model.api.v2.feature.tpa;

import org.bukkit.entity.Player;

import java.util.UUID;

public final class TpaRequest {

    public enum Type { TPA, TPAHERE }

    public final UUID from;
    public final UUID to;
    public final Type type;
    public final long createdAt;
    public final int timeoutTaskId;

    public TpaRequest(UUID from, UUID to, Type type, long createdAt, int timeoutTaskId) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.createdAt = createdAt;
        this.timeoutTaskId = timeoutTaskId;
    }

    public boolean isTpHere() {
        return type == Type.TPAHERE;
    }

    public boolean expired(long timeoutMs) {
        return System.currentTimeMillis() - createdAt > timeoutMs;
    }

    public String fromName(Player p) {
        Player f = p.getServer().getPlayer(from);
        return f == null ? "未知" : f.getName();
    }

    public String toName(Player p) {
        Player t = p.getServer().getPlayer(to);
        return t == null ? "未知" : t.getName();
    }
}
