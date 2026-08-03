package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

public final class WorkbenchCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.workbench")) {
            SF.sf().msg(sender, "§c你没有权限");
            return true;
        }
        if (!(sender instanceof Player p)) {
            SF.sf().msg(sender, "§c只有玩家才能使用此命令");
            return true;
        }
        p.openWorkbench(p.getLocation(), true);
        SF.sf().msg(p, "§a已打开工作台");
        return true;
    }
}
