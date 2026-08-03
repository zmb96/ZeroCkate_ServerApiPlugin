package server.sf.model.api.v2.feature.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.database.LocationData;

import java.util.ArrayList;
import java.util.List;

public final class SpawnCommand implements CommandExecutor, TabCompleter {

    private final TeleportManager tp;

    public SpawnCommand(TeleportManager tp) {
        this.tp = tp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            SF.sf().msg(sender, "§c只有玩家才能使用此命令");
            return true;
        }

        if (command.getName().equals("setspawn")) {
            if (!p.hasPermission("sf.spawn.set")) {
                SF.sf().msg(p, "§c你没有权限");
                return true;
            }
            Location loc = p.getLocation();
            p.getWorld().setSpawnLocation(loc);
            SF.sf().msg(p, "§a已设置 " + loc.getWorld().getName() + " 的出生点");
            SF.sf().info("Spawn set by " + p.getName() + " at " + LocationData.of(loc).sqlValues());
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("set") && p.hasPermission("sf.spawn.set")) {
            Location loc = p.getLocation();
            p.getWorld().setSpawnLocation(loc);
            SF.sf().msg(p, "§a已设置 " + loc.getWorld().getName() + " 的出生点");
            return true;
        }

        World w = p.getWorld();
        Location spawn = w.getSpawnLocation();
        tp.teleportDelayed(p, spawn, "spawn", tp.delayFor("spawn"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (command.getName().equals("spawn") && args.length == 1 && sender.hasPermission("sf.spawn.set")) {
            out.add("set");
        }
        return out;
    }
}
