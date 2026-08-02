package server.sf.model.api.v2.feature.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.List;

public final class TpaCommands implements CommandExecutor, TabCompleter {

    private final TpaManager tpa;

    public TpaCommands(TpaManager tpa) {
        this.tpa = tpa;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            SF.sf().msg(sender, "§c只有玩家才能使用此命令");
            return true;
        }
        String name = command.getName();
        switch (name) {
            case "tpa" -> {
                if (args.length == 0) {
                    SF.sf().msg(p, "§c用法: /tpa <玩家>");
                    return true;
                }
                Player t = Bukkit.getPlayerExact(args[0]);
                if (t == null) {
                    SF.sf().msg(p, "§c玩家不在线");
                    return true;
                }
                tpa.request(p, t, TpaRequest.Type.TPA);
                return true;
            }
            case "tpahere" -> {
                if (args.length == 0) {
                    SF.sf().msg(p, "§c用法: /tpahere <玩家>");
                    return true;
                }
                Player t = Bukkit.getPlayerExact(args[0]);
                if (t == null) {
                    SF.sf().msg(p, "§c玩家不在线");
                    return true;
                }
                tpa.request(p, t, TpaRequest.Type.TPAHERE);
                return true;
            }
            case "tpaccept" -> {
                tpa.accept(p);
                return true;
            }
            case "tpdeny" -> {
                tpa.deny(p);
                return true;
            }
            case "tpcancel" -> {
                tpa.cancel(p);
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        String name = command.getName();
        if ((name.equals("tpa") || name.equals("tpahere")) && args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                out.add(p.getName());
            }
        }
        return out;
    }
}
