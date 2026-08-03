package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.List;

public final class EnderchestCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.enderchest")) {
            SF.sf().msg(sender, "§c你没有权限");
            return true;
        }

        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                SF.sf().msg(sender, "§c玩家不在线");
                return true;
            }
        } else {
            if (!(sender instanceof Player p)) {
                SF.sf().msg(sender, "§c控制台请指定玩家");
                return true;
            }
            target = p;
        }

        if (!(sender instanceof Player viewer)) {
            SF.sf().msg(sender, "§c只有玩家才能打开背包");
            return true;
        }
        viewer.openInventory(target.getEnderChest());
        SF.sf().msg(viewer, "§a正在查看 §e" + target.getName() + " §a的末影箱");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> out = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
            return out;
        }
        return new ArrayList<>();
    }
}
