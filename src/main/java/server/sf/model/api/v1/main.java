package server.sf.model.api.v1;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.SF;
import server.sf.model.api.v2.database.DatabaseManager;
import server.sf.model.api.v2.feature.admin.AdminListener;
import server.sf.model.api.v2.feature.admin.AdminStateManager;
import server.sf.model.api.v2.feature.admin.*;
import server.sf.model.api.v2.feature.enchant.LifestealEnchant;
import server.sf.model.api.v2.feature.enchant.AncestralMightEnchant;
import server.sf.model.api.v2.feature.enchant.EnchantManager;
import server.sf.model.api.v2.feature.enchant.SFEnchantCommand;
import server.sf.model.api.v2.feature.item.ItemManager;
import server.sf.model.api.v2.feature.item.MagicScepterItem;
import server.sf.model.api.v2.feature.item.SFItemCommand;
import server.sf.model.api.v2.feature.chat.ChatCommand;
import server.sf.model.api.v2.feature.chat.ChatManager;
import server.sf.model.api.v2.feature.permission.PermissionCommand;
import server.sf.model.api.v2.feature.permission.PermissionManager;
import server.sf.model.api.v2.feature.world.WorldCommand;
import server.sf.model.api.v2.feature.world.WorldManager;
import server.sf.model.api.v2.feature.teleport.*;
import server.sf.model.api.v2.feature.tpa.TpaCommands;
import server.sf.model.api.v2.feature.tpa.TpaManager;

@Deprecated
public final class main extends JavaPlugin {

    private static Economy eco;
    private TeleportManager teleportManager;
    private TpaManager tpaManager;
    private AdminStateManager adminState;

    @Override
    public void onEnable() {
        SF.init(this);
        SF.sf().info("Starting server.sf.model.api.v1 (v2 bridge enabled)");
        saveDefaultConfig();

        boolean dbOk = DatabaseManager.init(this);
        SF.sf().info("Database ready: " + dbOk);

        teleportManager = new TeleportManager(this);
        SF.sf().setTeleportManager(teleportManager);
        tpaManager = new TpaManager(this, teleportManager);
        adminState = new AdminStateManager();

        SF.sf().regEvent(new PlayerJoinOrQuitEvent(this), this);
        SF.sf().regEvent(new AdminListener(adminState), this);

        SF.sf()
                .regCommand("servermanagement", new servermanagement(this))
                .regCommand("ru", new rulescom())
                .regCommand("sh", new helpcom())
                .regCommand("ty", new tycon(this))
                .regCommand("giveit", new giveit(this))

                .regCommand("spawn", new SpawnCommand(teleportManager))
                .regCommand("setspawn", new SpawnCommand(teleportManager))
                .regCommand("home", new HomeCommand(teleportManager))
                .regCommand("sethome", new HomeCommand(teleportManager))
                .regCommand("delhome", new HomeCommand(teleportManager))
                .regCommand("homes", new HomeCommand(teleportManager))
                .regCommand("warp", new WarpCommand(teleportManager))
                .regCommand("setwarp", new WarpCommand(teleportManager))
                .regCommand("delwarp", new WarpCommand(teleportManager))
                .regCommand("warps", new WarpCommand(teleportManager))
                .regCommand("back", new BackCommand(teleportManager))
                .regCommand("tp", new TpCommand(teleportManager))
                .regCommand("tphere", new TpCommand(teleportManager))
                .regCommand("tpa", new TpaCommands(tpaManager))
                .regCommand("tpahere", new TpaCommands(tpaManager))
                .regCommand("tpaccept", new TpaCommands(tpaManager))
                .regCommand("tpdeny", new TpaCommands(tpaManager))
                .regCommand("tpcancel", new TpaCommands(tpaManager))

                .regCommand("gm", new GamemodeCommand())
                .regCommand("fly", new FlyCommand(adminState))
                .regCommand("heal", new HealCommand())
                .regCommand("feed", new FeedCommand())
                .regCommand("god", new GodCommand(adminState))
                .regCommand("vanish", new VanishCommand(adminState))
                .regCommand("ec", new EnderchestCommand())
                .regCommand("wb", new WorkbenchCommand())
                .regCommand("clear", new ClearCommand())
                .regCommand("speed", new SpeedCommand())
                .regCommand("suicide", new SuicideCommand());

        EnchantManager em = SF.sf().enchant();
        em.register(new LifestealEnchant());
        em.register(new AncestralMightEnchant());
        SF.sf().regCommand("sfenchant", new SFEnchantCommand(em));

        ItemManager itemManager = SF.sf().item();
        itemManager.register(new MagicScepterItem());
        SF.sf().regCommand("sfitem", new SFItemCommand(itemManager));

        ChatManager chatManager = SF.sf().chat();
        SF.sf().regCommand("sfchat", new ChatCommand(chatManager));

        WorldManager worldManager = SF.sf().world();
        SF.sf().regCommand("sfworld", new WorldCommand(worldManager));

        PermissionManager permManager = SF.sf().permission();
        SF.sf().regCommand("sfperm", new PermissionCommand(permManager));

        SF.sf().reach();
        SF.sf().regCommand("sfreach", new server.sf.model.api.v2.feature.main.ReachCommand(SF.sf().reach()));

        SF.sf().info("插件已加载");
        SF.sf().info("Economy ready: " + SF.sf().eco().ready() + " (Essentials=" + SF.sf().eco().hasEssentials() + ", Vault=" + SF.sf().eco().hasVault() + ")");
    }

    @Override
    public void onDisable() {
        SF.sf().info("Unload server.sf.model.api.v1.main");
        if (tpaManager != null) {
            getServer().getOnlinePlayers().forEach(p -> tpaManager.clearAll(p.getUniqueId()));
        }
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
