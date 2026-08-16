package server.sf.model.api.v1;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.database.DatabaseManager;
import server.sf.model.api.v2.feature.enchant.LifestealEnchant;
import server.sf.model.api.v2.feature.enchant.AncestralMightEnchant;
import server.sf.model.api.v2.feature.enchant.EnchantManager;
import server.sf.model.api.v2.feature.enchant.SFEnchantCommand;
import server.sf.model.api.v2.feature.item.ItemManager;
import server.sf.model.api.v2.feature.item.MagicScepterItem;
import server.sf.model.api.v2.feature.item.SFItemCommand;
import server.sf.model.api.v2.feature.chat.ChatCommand;
import server.sf.model.api.v2.feature.chat.ChatManager;
import server.sf.model.api.v2.feature.main.ReachCommand;
import server.sf.model.api.v2.feature.perf.PerformanceCommand;
import server.sf.model.api.v2.feature.permission.PermissionCommand;
import server.sf.model.api.v2.feature.permission.PermissionManager;
import server.sf.model.api.v2.feature.world.WorldCommand;
import server.sf.model.api.v2.feature.world.WorldManager;
import server.sf.model.api.v2.feature.teleport.TeleportManager;

@Deprecated
public final class main extends JavaPlugin {

    private static Economy eco;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        SF.init(this);
        SF sf = SF.sf();
        sf.info("Starting server.sf.model.api.v1 (v2 bridge enabled)");
        saveDefaultConfig();

        boolean dbOk = DatabaseManager.init(this);
        sf.info("Database ready: " + dbOk);

        teleportManager = new TeleportManager(this);
        sf.setTeleportManager(teleportManager);

        sf
                .regCommand("servermanagement", new servermanagement(this))
                .regCommand("ru", new rulescom())
                .regCommand("sh", new helpcom())
                .regCommand("ty", new tycon(this))
                .regCommand("giveit", new giveit(this));

        sf.regEvent(new PlayerJoinOrQuitEvent(this));

        EnchantManager em = sf.enchant();
        em.register(new LifestealEnchant());
        em.register(new AncestralMightEnchant());
        sf.regCommand("sfenchant", new SFEnchantCommand(em));

        ItemManager itemManager = sf.item();
        itemManager.register(new MagicScepterItem());
        sf.regCommand("sfitem", new SFItemCommand(itemManager));

        ChatManager chatManager = sf.chat();
        sf.regCommand("sfchat", new ChatCommand(chatManager));

        WorldManager worldManager = sf.world();
        sf.regCommand("sfworld", new WorldCommand(worldManager));

        PermissionManager permManager = sf.permission();
        sf.regCommand("sfperm", new PermissionCommand(permManager));

        sf.reach();
        sf.regCommand("sfreach", new ReachCommand(sf.reach()));

        sf.perf();
        sf.regCommand("sfperf", new PerformanceCommand(sf.perf()));

        sf.info("插件已加载");
        sf.info("Economy ready: " + sf.eco().ready() + " (Essentials=" + sf.eco().hasEssentials() + ", Vault=" + sf.eco().hasVault() + ")");
    }

    @Override
    public void onDisable() {
        SF sf = SF.sf();
        sf.info("Unload server.sf.model.api.v1.main");
        DatabaseManager.shutdown();
        SF.shutdown();
    }

    private boolean setupEconomy() {
        try {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                return false;
            }
            RegisteredServiceProvider<Economy> rsp =
                    getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            eco = rsp.getProvider();
            return eco != null;
        } catch(NullPointerException e) {

        }
        return eco != null;
    }

    public static Economy eco() {
        return eco;
    }

    public static SF sf() {
        return SF.sf();
    }
}
