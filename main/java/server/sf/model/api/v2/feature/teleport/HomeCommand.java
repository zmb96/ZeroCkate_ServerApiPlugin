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

public final class HomeCommand implements CommandExecutor, TabCompleter {

    private final TeleportManager tp;

    public HomeCommand(TeleportManager tp) {
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
            case "sethome" -> {
                String home = (args.length == 0) ? "default" : args[0];
                LocationData loc = LocationData.of(p.getLocation());
                LocationStorage.setHome(p.getUniqueId(), home, loc);
                SF.sf().msg(p, "§a已设置家 §e" + home);
                return true;
            }
            case "delhome" -> {
                String home = (args.length == 0) ? "default" : args[0];
                if (LocationStorage.delHome(p.getUniqueId(), home)) {
                    SF.sf().msg(p, "§a已删除家 §e" + home);
                } else {
                    SF.sf().msg(p, "§c家 §e" + home + " §c不存在");
                }
                return true;
            }
            case "homes" -> {
                Map<String, LocationData> homes = LocationStorage.getHomes(p.getUniqueId());
                if (homes.isEmpty()) {
                    SF.sf().msg(p, "§c你还没有设置任何家");
                    return true;
                }
                SF.sf().msg(p, "§6===== 你的家 (§e" + homes.size() + "§6) =====");
                homes.keySet().forEach(h -> SF.sf().msg(p, "§e- " + h));
                return true;
            }
            case "home" -> {
                String home = (args.length == 0) ? "default" : args[0];
                LocationData data = LocationStorage.getHome(p.getUniqueId(), home);
                if (data == null) {
                    SF.sf().msg(p, "§c家 §e" + home + " §c不存在");
                    return true;
                }
                var loc = data.toLocation();
                if (loc == null) {
                    SF.sf().msg(p, "§c家所在世界未加载");
                    return true;
                }
                tp.teleportDelayed(p, loc, "home", tp.delayFor("home"));
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player p)) return new ArrayList<>();
        if (args.length != 1) return new ArrayList<>();
        String name = command.getName();
        if (name.equals("home") || name.equals("delhome")) {
            return new ArrayList<>(LocationStorage.homeNames(p.getUniqueId()));
        }
        return new ArrayList<>();
    }
}
