package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.List;

public final class ClearCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.clear")) {
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

        int count = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            if (item != null) count += item.getAmount();
        }
        target.getInventory().clear();
        target.getEnderChest().clear();
        target.setItemOnCursor(null);
        SF.sf().msg(sender, "§a已清空 §e" + target.getName() + " §a的背包 §7(" + count + " 件物品)");
        if (explicit) SF.sf().msg(target, "§c你的背包已被清空");
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
