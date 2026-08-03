package server.sf.model.api.v2;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.economy.SFEconomy;
import server.sf.model.api.v2.event.SFEvents;
import server.sf.model.api.v2.feature.enchant.EnchantAttributeListener;
import server.sf.model.api.v2.feature.enchant.EnchantManager;
import server.sf.model.api.v2.feature.enchant.SEnchantment;
import server.sf.model.api.v2.feature.item.ItemListener;
import server.sf.model.api.v2.feature.item.ItemManager;
import server.sf.model.api.v2.feature.item.SItem;
import server.sf.model.api.v2.feature.teleport.TeleportManager;
import server.sf.model.api.v2.main.SFCommandOps;
import server.sf.model.api.v2.main.SFLogger;
import server.sf.model.api.v2.main.SFPlayerOps;
import server.sf.model.api.v2.main.SFScheduler;
import server.sf.model.api.v2.main.SFServerOps;

import java.util.UUID;

public final class SF implements SFApi {

    private static SF instance;

    private final JavaPlugin plugin;
    private final SFEconomy economy;
    private final SFEvents events;
    private final SFLogger logger;
    private final SFScheduler scheduler;
    private final SFPlayerOps players;
    private final SFCommandOps commands;
    private final SFServerOps serverOps;
    private TeleportManager teleportManager;
    private EnchantManager enchantManager;
    private EnchantAttributeListener enchantAttrListener;
    private ItemManager itemManager;
    private ItemListener itemListener;

    private SF(JavaPlugin plugin) {
        this.plugin = plugin;
        this.economy = new SFEconomy(plugin);
        this.events = new SFEvents(plugin);
        this.logger = new SFLogger(plugin);
        this.scheduler = new SFScheduler(plugin);
        this.players = new SFPlayerOps();
        this.commands = new SFCommandOps(plugin);
        this.serverOps = new SFServerOps();
    }

    public static void init(JavaPlugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("SF already initialized");
        }
        instance = new SF(plugin);
        plugin.getServer().getServicesManager().register(SFApi.class, instance, plugin, org.bukkit.plugin.ServicePriority.Normal);
    }

    public static void shutdown() {
        if (instance != null) {
            if (instance.enchantAttrListener != null) instance.enchantAttrListener.shutdown();
            if (instance.enchantManager != null) instance.enchantManager.unregisterAll();
            if (instance.itemListener != null) instance.itemListener.shutdown();
            if (instance.itemManager != null) instance.itemManager.unregisterAll();
            instance.events.unregisterAll();
            instance.plugin.getServer().getServicesManager().unregister(instance);
        }
        instance = null;
    }

    public static SF sf() {
        if (instance == null) {
            throw new IllegalStateException("SF not initialized. Call SF.init(plugin) in onEnable().");
        }
        return instance;
    }

    public void setTeleportManager(TeleportManager tp) {
        this.teleportManager = tp;
    }

    public TeleportManager teleport() {
        return teleportManager;
    }

    public EnchantManager enchant() {
        if (enchantManager == null) {
            SEnchantment.init(plugin);
            enchantManager = new EnchantManager();
            enchantAttrListener = new EnchantAttributeListener(enchantManager);
            regEvent(new server.sf.model.api.v2.feature.enchant.EnchantAnvilListener(enchantManager), plugin);
            regEvent(enchantAttrListener, plugin);
            enchantAttrListener.startTick(this, 40L);
            SF.sf().info("[Enchant] System initialized");
        }
        return enchantManager;
    }

    public ItemManager item() {
        if (itemManager == null) {
            SItem.init(plugin);
            itemManager = new ItemManager();
            itemListener = new ItemListener(itemManager);
            regEvent(itemListener, plugin);
            SF.sf().info("[Item] System initialized");
        }
        return itemManager;
    }

    @Override
    public SFLogger logger() {
        return logger;
    }

    @Override
    public SFEconomy economy() {
        return economy;
    }

    public SFEconomy eco() {
        return economy;
    }

    @Override
    public SFEvents events() {
        return events;
    }

    @Override
    public SFScheduler scheduler() {
        return scheduler;
    }

    @Override
    public SFPlayerOps players() {
        return players;
    }

    @Override
    public SFServerOps server() {
        return serverOps;
    }

    public SFCommandOps commands() {
        return commands;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public Server bukkit() {
        return plugin.getServer();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String fmt, Object... args) {
        logger.info(fmt, args);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String fmt, Object... args) {
        logger.warn(fmt, args);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    @Override
    public void error(String fmt, Object... args) {
        logger.error(fmt, args);
    }

    @Override
    public void broadcast(String msg) {
        serverOps.broadcast(msg);
    }

    @Override
    public void broadcast(String perm, String msg) {
        serverOps.broadcast(perm, msg);
    }

    @Override
    public void msg(CommandSender sender, String msg) {
        serverOps.msg(sender, msg);
    }

    @Override
    public Player player(String name) {
        return players.byName(name);
    }

    @Override
    public Player player(UUID id) {
        return players.byId(id);
    }

    @Override
    public boolean giveMoney(OfflinePlayer p, double amount) {
        return economy.give(p, amount);
    }

    @Override
    public boolean takeMoney(OfflinePlayer p, double amount) {
        return economy.take(p, amount);
    }

    @Override
    public boolean setMoney(OfflinePlayer p, double amount) {
        return economy.set(p, amount);
    }

    @Override
    public double balance(OfflinePlayer p) {
        return economy.balance(p);
    }

    @Override
    public boolean transferMoney(OfflinePlayer from, OfflinePlayer to, double amount) {
        return economy.transfer(from, to, amount);
    }

    @Override
    public String formatMoney(double amount) {
        return economy.format(amount);
    }

    @Override
    public boolean teleport(Player p, Location loc) {
        if (teleportManager != null) {
            return teleportManager.teleportNow(p, loc, "api");
        }
        return p.teleport(loc);
    }

    @Override
    public void run(Runnable r) {
        scheduler.run(r);
    }

    @Override
    public void runAsync(Runnable r) {
        scheduler.runAsync(r);
    }

    @Override
    public void runLater(Runnable r, long ticks) {
        scheduler.runLater(r, ticks);
    }

    @Override
    public void runTimer(Runnable r, long delay, long period) {
        scheduler.runTimer(r, delay, period);
    }

    @Override
    public void console(String cmd) {
        commands.console(cmd);
    }

    public SF regEvent(Listener listener, JavaPlugin p) {
        commands.regEvent(listener, p);
        return this;
    }

    public SF regEvent(Listener listener) {
        commands.regEvent(listener);
        return this;
    }

    public SF regCommand(String name, CommandExecutor executor) {
        commands.regCommand(name, executor);
        return this;
    }
}
