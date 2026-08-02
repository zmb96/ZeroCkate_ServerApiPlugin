package server.sf.model.api.v2.feature.teleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

public final class BackCommand implements CommandExecutor {

    private final TeleportManager tp;

    public BackCommand(TeleportManager tp) {
        this.tp = tp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            SF.sf().msg(sender, "§c只有玩家才能使用此命令");
            return true;
        }
        tp.back(p);
        return true;
    }
}
