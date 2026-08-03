package server.sf.model.api.v2.feature.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.List;

public final class TpCommand implements CommandExecutor, TabCompleter {

    private final TeleportManager tp;

    public TpCommand(TeleportManager tp) {
        this.tp = tp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName();

        if (name.equals("tp")) {
            if (!sender.hasPermission("sf.admin.tp")) {
                SF.sf().msg(sender, "§c你没有权限");
                return true;
            }
            if (args.length == 0) {
                SF.sf().msg(sender, "§c用法: /tp <玩家> [目标玩家] 或 /tp <x> <y> <z>");
                return true;
            }
            if (args.length == 1) {
                if (!(sender instanceof Player p)) {
                    SF.sf().msg(sender, "§c控制台请指定两个玩家");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    SF.sf().msg(p, "§c玩家不在线");
                    return true;
                }
                tp.teleportNow(p, target.getLocation(), "tp");
                SF.sf().msg(p, "§a已传送到 §e" + target.getName());
                return true;
            }
            if (args.length == 2) {
                Player a = Bukkit.getPlayerExact(args[0]);
                Player b = Bukkit.getPlayerExact(args[1]);
                if (a == null || b == null) {
                    SF.sf().msg(sender, "§c玩家不在线");
                    return true;
                }
                tp.teleportNow(a, b.getLocation(), "tp");
                SF.sf().msg(sender, "§a已将 §e" + a.getName() + " §a传送到 §e" + b.getName());
                return true;
            }
            if (args.length == 3 && sender instanceof Player p) {
                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    tp.teleportNow(p, p.getLocation().set(x, y, z), "tp");
                    SF.sf().msg(p, "§a已传送到 §e" + x + ", " + y + ", " + z);
                } catch (NumberFormatException e) {
                    SF.sf().msg(p, "§c坐标格式错误");
                }
                return true;
            }
            return true;
        }

        if (name.equals("tphere")) {
            if (!sender.hasPermission("sf.admin.tp")) {
                SF.sf().msg(sender, "§c你没有权限");
                return true;
            }
            if (args.length == 0) {
                SF.sf().msg(sender, "§c用法: /tphere <玩家>");
                return true;
            }
            if (!(sender instanceof Player p)) {
                SF.sf().msg(sender, "§c只有玩家才能使用此命令");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                SF.sf().msg(p, "§c玩家不在线");
                return true;
            }
            tp.teleportNow(target, p.getLocation(), "tphere");
            SF.sf().msg(p, "§a已将 §e" + target.getName() + " §a召唤到你身边");
            SF.sf().msg(target, "§a你被传送到 §e" + p.getName());
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            out.add(p.getName());
        }
        return out;
    }
}
