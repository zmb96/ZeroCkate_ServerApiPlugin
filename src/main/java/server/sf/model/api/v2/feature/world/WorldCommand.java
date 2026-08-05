package server.sf.model.api.v2.feature.world;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldCommand implements CommandExecutor, TabCompleter {

    private final WorldManager manager;

    public WorldCommand(WorldManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.world")) {
            SF.sf().msg(sender, ChatColor.RED + "你没有权限");
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        String sub = args[0].toLowerCase();
        World world = getWorld(sender, args);
        if (world == null && !Arrays.asList("list", "preset", "help", "info").contains(sub)) {
            SF.sf().msg(sender, ChatColor.RED + "无法确定目标世界");
            return true;
        }

        switch (sub) {
            case "time" -> handleTime(sender, args, world);
            case "day" -> { manager.setDay(world); SF.sf().msg(sender, ChatColor.GREEN + "已设置为白天"); }
            case "night" -> { manager.setNight(world); SF.sf().msg(sender, ChatColor.GREEN + "已设置为夜晚"); }
            case "noon" -> { manager.setNoon(world); SF.sf().msg(sender, ChatColor.GREEN + "已设置为中午"); }
            case "midnight" -> { manager.setMidnight(world); SF.sf().msg(sender, ChatColor.GREEN + "已设置为午夜"); }
            case "locktime" -> { manager.lockTime(world, world.getTime()); SF.sf().msg(sender, ChatColor.GREEN + "时间已锁定"); }
            case "unlocktime" -> { manager.unlockTime(world); SF.sf().msg(sender, ChatColor.GREEN + "时间已解锁"); }
            case "weather" -> handleWeather(sender, args, world);
            case "difficulty" -> handleDifficulty(sender, args, world);
            case "pvp" -> handlePvp(sender, args, world);
            case "border" -> handleBorder(sender, args, world);
            case "mob" -> handleMob(sender, args, world);
            case "fire" -> handleFire(sender, args, world);
            case "preset" -> handlePreset(sender, args, world);
            case "info" -> handleInfo(sender, world);
            case "list" -> handleList(sender);
            case "help" -> sendHelp(sender, label);
            default -> { SF.sf().msg(sender, ChatColor.RED + "未知子命令: " + sub); sendHelp(sender, label); }
        }
        return true;
    }

    private World getWorld(CommandSender sender, String[] args) {
        if (args.length >= 3 && !isNumeric(args[args.length - 1])) {
            World w = SF.sf().bukkit().getWorld(args[args.length - 1]);
            if (w != null) return w;
        }
        if (sender instanceof Player p) return p.getWorld();
        return null;
    }

    private void handleTime(CommandSender s, String[] args, World w) {
        if (args.length < 2) {
            SF.sf().msg(s, ChatColor.RED + "用法: /sfworld time <数值> [世界]");
            return;
        }
        try {
            long time = Long.parseLong(args[1]);
            manager.setTime(w, time);
            SF.sf().msg(s, ChatColor.GREEN + w.getName() + " 时间设为 " + time);
        } catch (NumberFormatException e) {
            SF.sf().msg(s, ChatColor.RED + "时间必须是数字");
        }
    }

    private void handleWeather(CommandSender s, String[] args, World w) {
        if (args.length < 2) {
            SF.sf().msg(s, ChatColor.RED + "用法: /sfworld weather <sun|rain|storm> [世界]");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "sun", "clear" -> { manager.setStorm(w, false); SF.sf().msg(s, ChatColor.GREEN + "天气已设为晴天"); }
            case "rain" -> { manager.setStorm(w, true); SF.sf().msg(s, ChatColor.GREEN + "天气已设为雨天"); }
            case "storm", "thunder" -> { manager.setThunder(w, true); SF.sf().msg(s, ChatColor.GREEN + "天气已设为雷暴"); }
            default -> SF.sf().msg(s, ChatColor.RED + "未知天气: " + args[1]);
        }
    }

    private void handleDifficulty(CommandSender s, String[] args, World w) {
        if (args.length < 2) {
            SF.sf().msg(s, ChatColor.RED + "用法: /sfworld difficulty <peaceful|easy|normal|hard> [世界]");
            return;
        }
        try {
            Difficulty d = Difficulty.valueOf(args[1].toUpperCase());
            manager.setDifficulty(w, d);
            SF.sf().msg(s, ChatColor.GREEN + w.getName() + " 难度设为 " + d.name());
        } catch (IllegalArgumentException e) {
            SF.sf().msg(s, ChatColor.RED + "未知难度: " + args[1]);
        }
    }

    private void handlePvp(CommandSender s, String[] args, World w) {
        boolean pvp = args.length >= 2 ? Boolean.parseBoolean(args[1]) : !w.getPVP();
        manager.setPvp(w, pvp);
        SF.sf().msg(s, ChatColor.GREEN + w.getName() + " PVP: " + (pvp ? "开启" : "关闭"));
    }

    private void handleBorder(CommandSender s, String[] args, World w) {
        if (args.length < 2) {
            SF.sf().msg(s, ChatColor.YELLOW + "当前边界大小: " + w.getWorldBorder().getSize());
            SF.sf().msg(s, ChatColor.RED + "用法: /sfworld border <size|center|reset> ... [世界]");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "size" -> {
                if (args.length < 3) { SF.sf().msg(s, ChatColor.RED + "用法: /sfworld border size <数值> [秒数] [世界]"); return; }
                double size = Double.parseDouble(args[2]);
                if (args.length >= 4 && isNumeric(args[3])) {
                    long sec = Long.parseLong(args[3]);
                    manager.setBorder(w, size, sec);
                    SF.sf().msg(s, ChatColor.GREEN + "边界将在 " + sec + " 秒内变为 " + size);
                } else {
                    manager.setBorder(w, size);
                    SF.sf().msg(s, ChatColor.GREEN + "边界大小设为 " + size);
                }
            }
            case "center" -> {
                if (args.length < 4) { SF.sf().msg(s, ChatColor.RED + "用法: /sfworld border center <x> <z> [世界]"); return; }
                double x = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                manager.setBorderCenter(w, x, z);
                SF.sf().msg(s, ChatColor.GREEN + "边界中心设为 " + x + ", " + z);
            }
            case "reset" -> { manager.resetBorder(w); SF.sf().msg(s, ChatColor.GREEN + "边界已重置"); }
            default -> SF.sf().msg(s, ChatColor.RED + "未知: " + args[1]);
        }
    }

    private void handleMob(CommandSender s, String[] args, World w) {
        boolean spawn = args.length >= 2 ? Boolean.parseBoolean(args[1]) :
                w.getGameRuleValue(org.bukkit.GameRule.DO_MOB_SPAWNING);
        manager.setMobSpawning(w, spawn);
        SF.sf().msg(s, ChatColor.GREEN + w.getName() + " 生物生成: " + (spawn ? "开启" : "关闭"));
    }

    private void handleFire(CommandSender s, String[] args, World w) {
        boolean spread = args.length >= 2 ? Boolean.parseBoolean(args[1]) :
                w.getGameRuleValue(org.bukkit.GameRule.DO_FIRE_TICK);
        manager.setFireSpread(w, spread);
        SF.sf().msg(s, ChatColor.GREEN + w.getName() + " 火焰蔓延: " + (spread ? "开启" : "关闭"));
    }

    private void handlePreset(CommandSender s, String[] args, World w) {
        if (args.length < 2) {
            SF.sf().msg(s, ChatColor.RED + "用法: /sfworld preset <save|apply|list> <名称> [世界]");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "save" -> {
                if (args.length < 3) { SF.sf().msg(s, ChatColor.RED + "用法: /sfworld preset save <名称> [世界]"); return; }
                manager.savePreset(args[2], w);
                SF.sf().msg(s, ChatColor.GREEN + "预设 '" + args[2] + "' 已保存");
            }
            case "apply" -> {
                if (args.length < 3) { SF.sf().msg(s, ChatColor.RED + "用法: /sfworld preset apply <名称> [世界]"); return; }
                if (manager.applyPreset(args[2], w)) {
                    SF.sf().msg(s, ChatColor.GREEN + "预设 '" + args[2] + "' 已应用");
                } else {
                    SF.sf().msg(s, ChatColor.RED + "预设不存在: " + args[2]);
                }
            }
            case "list" -> {
                if (manager.presets().isEmpty()) {
                    SF.sf().msg(s, ChatColor.GRAY + "暂无预设");
                } else {
                    SF.sf().msg(s, ChatColor.GOLD + "===== 世界预设 =====");
                    manager.presets().keySet().forEach(k -> SF.sf().msg(s, ChatColor.YELLOW + " - " + k));
                }
            }
            default -> SF.sf().msg(s, ChatColor.RED + "未知: " + args[1]);
        }
    }

    private void handleInfo(CommandSender s, World w) {
        if (w == null) { SF.sf().msg(s, ChatColor.RED + "请指定世界"); return; }
        SF.sf().msg(s, ChatColor.GOLD + "===== " + w.getName() + " =====");
        SF.sf().msg(s, ChatColor.YELLOW + "时间: " + ChatColor.WHITE + w.getTime());
        SF.sf().msg(s, ChatColor.YELLOW + "天气: " + ChatColor.WHITE + (w.hasStorm() ? (w.isThundering() ? "雷暴" : "雨天") : "晴天"));
        SF.sf().msg(s, ChatColor.YELLOW + "难度: " + ChatColor.WHITE + w.getDifficulty().name());
        SF.sf().msg(s, ChatColor.YELLOW + "PVP: " + (w.getPVP() ? ChatColor.GREEN + "开" : ChatColor.RED + "关"));
        SF.sf().msg(s, ChatColor.YELLOW + "生物生成: " + (w.getGameRuleValue(org.bukkit.GameRule.DO_MOB_SPAWNING) ? ChatColor.GREEN + "开" : ChatColor.RED + "关"));
        SF.sf().msg(s, ChatColor.YELLOW + "火焰蔓延: " + (w.getGameRuleValue(org.bukkit.GameRule.DO_FIRE_TICK) ? ChatColor.GREEN + "开" : ChatColor.RED + "关"));
        SF.sf().msg(s, ChatColor.YELLOW + "边界: " + ChatColor.WHITE + w.getWorldBorder().getSize());
    }

    private void handleList(CommandSender s) {
        SF.sf().msg(s, ChatColor.GOLD + "===== 已加载世界 (" + SF.sf().bukkit().getWorlds().size() + ") =====");
        for (World w : SF.sf().bukkit().getWorlds()) {
            SF.sf().msg(s, ChatColor.YELLOW + w.getName() + ChatColor.GRAY + " [" + w.getEnvironment().name() + "] time=" + w.getTime());
        }
    }

    private void sendHelp(CommandSender s, String label) {
        SF.sf().msg(s, ChatColor.GOLD + "===== SF 世界管理 =====");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " time <数值> [世界]" + ChatColor.GRAY + " - 设置时间");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " day|night|noon|midnight [世界]" + ChatColor.GRAY + " - 快捷时间");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " locktime|unlocktime [世界]" + ChatColor.GRAY + " - 锁定/解锁时间");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " weather <sun|rain|storm> [世界]" + ChatColor.GRAY + " - 设置天气");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " difficulty <peaceful|easy|normal|hard> [世界]" + ChatColor.GRAY + " - 设置难度");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " pvp <true|false> [世界]" + ChatColor.GRAY + " - PVP开关");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " border <size|center|reset> ... [世界]" + ChatColor.GRAY + " - 世界边界");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " mob <true|false> [世界]" + ChatColor.GRAY + " - 生物生成");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " fire <true|false> [世界]" + ChatColor.GRAY + " - 火焰蔓延");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " preset <save|apply|list> <名称> [世界]" + ChatColor.GRAY + " - 预设管理");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " info [世界]" + ChatColor.GRAY + " - 世界信息");
        SF.sf().msg(s, ChatColor.YELLOW + "/" + label + " list" + ChatColor.GRAY + " - 世界列表");
    }

    private boolean isNumeric(String s) {
        try { Double.parseDouble(s); return true; } catch (Exception e) { return false; }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (String s : Arrays.asList("time","day","night","noon","midnight","locktime","unlocktime",
                    "weather","difficulty","pvp","border","mob","fire","preset","info","list","help")) {
                if (s.startsWith(args[0].toLowerCase())) out.add(s);
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "weather" -> out.addAll(Arrays.asList("sun","rain","storm"));
                case "difficulty" -> out.addAll(Arrays.asList("peaceful","easy","normal","hard"));
                case "pvp","mob","fire" -> out.addAll(Arrays.asList("true","false"));
                case "border" -> out.addAll(Arrays.asList("size","center","reset"));
                case "preset" -> out.addAll(Arrays.asList("save","apply","list"));
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("border") && args[1].equalsIgnoreCase("reset")) {
                out.addAll(SF.sf().bukkit().getWorlds().stream().map(World::getName).toList());
            }
        }
        return out;
    }
}
