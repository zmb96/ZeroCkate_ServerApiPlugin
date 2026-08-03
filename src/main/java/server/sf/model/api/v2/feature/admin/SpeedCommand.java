package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SpeedCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.speed")) {
            SF.sf().msg(sender, "§c你没有权限");
            return true;
        }

        if (args.length == 0) {
            SF.sf().msg(sender, "§c用法: /speed <1-10> [玩家]");
            return true;
        }

        int speed;
        try {
            speed = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            SF.sf().msg(sender, "§c速度必须是 1-10 之间的整数");
            return true;
        }
        if (speed < 1 || speed > 10) {
            SF.sf().msg(sender, "§c速度必须在 1-10 之间");
            return true;
        }

        Player target;
        boolean explicit;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
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

        float f = speed / 10f;
        if (target.isFlying()) {
            target.setFlySpeed(f);
            SF.sf().msg(sender, "§a" + target.getName() + " 飞行速度设为 §e" + speed);
        } else {
            target.setWalkSpeed(f);
            SF.sf().msg(sender, "§a" + target.getName() + " 行走速度设为 §e" + speed);
        }
        if (explicit) SF.sf().msg(target, "§a你的速度已被设为 §e" + speed);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        if (args.length == 2) {
            List<String> out = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
            return out;
        }
        return new ArrayList<>();
    }
}
