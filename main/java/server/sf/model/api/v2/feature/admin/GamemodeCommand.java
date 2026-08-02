package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GamemodeCommand implements CommandExecutor, TabCompleter {

    private static final List<String> MODES = Arrays.asList("survival", "creative", "adventure", "spectator", "0", "1", "2", "3", "s", "c", "a", "sp");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.gamemode")) {
            SF.sf().msg(sender, "§c你没有权限");
            return true;
        }

        if (args.length == 0) {
            SF.sf().msg(sender, "§c用法: /gm <模式> [玩家]");
            return true;
        }

        GameMode mode = parseMode(args[0]);
        if (mode == null) {
            SF.sf().msg(sender, "§c未知模式: §e" + args[0]);
            return true;
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
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

        target.setGameMode(mode);
        SF.sf().msg(sender, "§a已将 §e" + target.getName() + " §a的游戏模式设为 §e" + mode.name());
        if (!sender.equals(target)) {
            SF.sf().msg(target, "§a你的游戏模式已被设为 §e" + mode.name());
        }
        return true;
    }

    private GameMode parseMode(String s) {
        return switch (s.toLowerCase()) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return new ArrayList<>(MODES);
        if (args.length == 2) {
            List<String> out = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
            return out;
        }
        return new ArrayList<>();
    }
}
