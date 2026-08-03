package server.sf.model.api.v2.feature.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

public final class SuicideCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            SF.sf().msg(sender, "§c只有玩家才能使用此命令");
            return true;
        }
        p.setHealth(0);
        SF.sf().msg(p, "§c你已自杀");
        return true;
    }
}
