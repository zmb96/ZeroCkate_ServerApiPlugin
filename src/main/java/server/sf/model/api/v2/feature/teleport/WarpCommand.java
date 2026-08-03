package server.sf.model.api.v2.feature.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.database.LocationData;
import server.sf.model.api.v2.database.LocationStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class WarpCommand implements CommandExecutor, TabCompleter {

    private final TeleportManager tp;

    public WarpCommand(TeleportManager tp) {
        this.tp = tp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            SF.sf().msg(sender, "§c只有玩家才能使用此命令");
            return true;
        }

        String name = command.getName();
        switch (name) {
            case "setwarp" -> {
                if (!p.hasPermission("sf.warp.set")) {
                    SF.sf().msg(p, "§c你没有权限");
                    return true;
                }
                if (args.length == 0) {
                    SF.sf().msg(p, "§c用法: /setwarp <名称>");
                    return true;
                }
                LocationData loc = LocationData.of(p.getLocation());
                LocationStorage.setWarp(args[0], loc, p.getUniqueId());
                SF.sf().msg(p, "§a已设置传送点 §e" + args[0]);
                return true;
            }
            case "delwarp" -> {
                if (!p.hasPermission("sf.warp.set")) {
                    SF.sf().msg(p, "§c你没有权限");
                    return true;
                }
                if (args.length == 0) {
                    SF.sf().msg(p, "§c用法: /delwarp <名称>");
                    return true;
                }
                if (LocationStorage.delWarp(args[0])) {
                    SF.sf().msg(p, "§a已删除传送点 §e" + args[0]);
                } else {
                    SF.sf().msg(p, "§c传送点 §e" + args[0] + " §c不存在");
                }
                return true;
            }
            case "warps" -> {
                Map<String, LocationData> warps = LocationStorage.getWarps();
                if (warps.isEmpty()) {
                    SF.sf().msg(p, "§c服务器还没有任何传送点");
                    return true;
                }
                SF.sf().msg(p, "§6===== 传送点列表 (§e" + warps.size() + "§6) =====");
                warps.keySet().forEach(w -> SF.sf().msg(p, "§e- " + w));
                return true;
            }
            case "warp" -> {
                if (args.length == 0) {
                    Map<String, LocationData> warps = LocationStorage.getWarps();
                    if (warps.isEmpty()) {
                        SF.sf().msg(p, "§c服务器还没有任何传送点");
                    } else {
                        SF.sf().msg(p, "§6===== 传送点列表 =====");
                        warps.keySet().forEach(w -> SF.sf().msg(p, "§e- " + w));
                    }
                    return true;
                }
                LocationData data = LocationStorage.getWarp(args[0]);
                if (data == null) {
                    SF.sf().msg(p, "§c传送点 §e" + args[0] + " §c不存在");
                    return true;
                }
                var loc = data.toLocation();
                if (loc == null) {
                    SF.sf().msg(p, "§c传送点所在世界未加载");
                    return true;
                }
                tp.teleportDelayed(p, loc, "warp", tp.delayFor("warp"));
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) return new ArrayList<>();
        String name = command.getName();
        if (name.equals("warp") || name.equals("delwarp")) {
            return new ArrayList<>(LocationStorage.warpNames());
        }
        return new ArrayList<>();
    }
}
