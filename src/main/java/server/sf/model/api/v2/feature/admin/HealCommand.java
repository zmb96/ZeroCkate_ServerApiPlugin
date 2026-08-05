package server.sf.model.api.v2.feature.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.feature.enchant.SEnchantment;

import java.util.ArrayList;
import java.util.List;

public final class HealCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sf.admin.heal")) {
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

        target.setHealth(target.getAttribute(SEnchantment.findAttribute("GENERIC_MAX_HEALTH")).getValue());
        target.setFireTicks(0);
        target.setRemainingAir(target.getMaximumAir());
        for (org.bukkit.potion.PotionEffect eff : target.getActivePotionEffects()) {
            target.removePotionEffect(eff.getType());
        }
        SF.sf().msg(sender, "§a已治疗 §e" + target.getName());
        if (explicit) SF.sf().msg(target, "§a你已被治疗");
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
