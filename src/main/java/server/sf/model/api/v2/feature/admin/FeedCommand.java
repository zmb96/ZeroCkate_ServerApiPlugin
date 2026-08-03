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

public final class FeedCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.feed")) {
            SF.sf().msg(sender, "§c你没有权限");
            return true;
        }

        Player target;
        boolean explicit;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                SF.sf().msg(sender, "§c玩家不在线");
                return true;
            }
            explicit = true;
        } else {
            if (!(sender instanceof Player p)) {
                SF.sf().msg(sender, "§c控制台请指定玩家");
                return true;
            }
            target = p;
            explicit = false;
        }

        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.setExhaustion(0f);
        SF.sf().msg(sender, "§a已喂饱 §e" + target.getName());
        if (explicit) SF.sf().msg(target, "§a你已被喂饱");
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
