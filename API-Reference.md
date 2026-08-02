# API 接口文档

SF 插件通过 Bukkit `ServicesManager` 对外暴露 `SFApi` 接口，其他插件可以通过它调用 SF 的所有功能。

## 接口概览

```java
package server.sf.model.api.v2;

public interface SFApi {
    // 子模块访问器
    SFLogger logger();
    SFEconomy economy();
    SFEvents events();
    SFScheduler scheduler();
    SFPlayerOps players();
    SFServerOps server();

    // 日志快捷方法
    void info(String msg);
    void info(String fmt, Object... args);
    void warn(String msg);
    void warn(String fmt, Object... args);
    void error(String msg);
    void error(String msg, Throwable t);
    void error(String fmt, Object... args);

    // 聊天/广播
    void broadcast(String msg);
    void broadcast(String perm, String msg);
    void msg(CommandSender sender, String msg);

    // 玩家查找
    Player player(String name);
    Player player(UUID id);

    // 经济系统（便捷方法）
    boolean giveMoney(OfflinePlayer p, double amount);
    boolean takeMoney(OfflinePlayer p, double amount);
    boolean setMoney(OfflinePlayer p, double amount);
    double balance(OfflinePlayer p);
    boolean transferMoney(OfflinePlayer from, OfflinePlayer to, double amount);
    String formatMoney(double amount);

    // 传送
    boolean teleport(Player p, Location loc);

    // 调度
    void run(Runnable r);                  // 主线程同步
    void runAsync(Runnable r);             // 异步
    void runLater(Runnable r, long ticks); // 延迟
    void runTimer(Runnable r, long delay, long period);  // 定时

    // 控制台
    void console(String cmd);

    // 获取 API 实例
    static SFApi get();
    static boolean isAvailable();
}
```

## 获取 API 实例

### 方法 1：静态方法（推荐）

```java
if (SFApi.isAvailable()) {
    SFApi api = SFApi.get();
    api.info("成功接入 SF API！");
}
```

### 方法 2：通过 ServicesManager

```java
RegisteredServiceProvider<SFApi> rsp = getServer().getServicesManager().getRegistration(SFApi.class);
if (rsp != null) {
    SFApi api = rsp.getProvider();
}
```

## 子模块详解

### SFLogger - 日志

```java
SFLogger logger = api.logger();

logger.info("普通信息");
logger.info("格式化信息: %s 已上线", playerName);  // 支持 String.format
logger.warn("警告信息");
logger.error("错误信息");
logger.error("错误带异常", exception);
```

### SFEconomy - 经济系统

```java
SFEconomy eco = api.economy();

// 状态查询
eco.ready();              // 经济系统是否就绪
eco.hasEssentials();      // 是否使用 EssentialsX 后端
eco.hasVault();           // 是否使用 Vault 后端

// 账户操作（OfflinePlayer 也支持）
eco.hasAccount(player);
eco.balance(player);
eco.give(player, 100);
eco.take(player, 50);
eco.set(player, 1000);
eco.transfer(playerA, playerB, 100);
eco.format(100.5);        // 格式化为字符串

// 直接访问后端
eco.essentials();         // EssentialsBackend 实例
eco.vault();              // VaultBackend 实例
eco.ops();                // EconomyOps 高级操作
```

### SFEvents - 事件系统

```java
SFEvents events = api.events();

// 通用方法（任意 Bukkit 事件）
events.on(PlayerJoinEvent.class, e -> {
    api.broadcast("欢迎 " + e.getPlayer().getName());
});

// 分类快捷方法
events.player().join(e -> { ... });
events.player().quit(e -> { ... });
events.player().chat(e -> { ... });
events.player().death(e -> { ... });
events.player().move(e -> { ... });

events.block().break_(e -> { ... });
events.block().place(e -> { ... });

events.entity().damage(e -> { ... });
events.entity().death(e -> { ... });

events.inventory().click(e -> { ... });

events.server().command(e -> { ... });
events.world().load(e -> { ... });

// 支持指定优先级
events.on(PlayerChatEvent.class, EventPriority.HIGH, true, e -> {
    // HIGH 优先级，忽略已取消的事件
});

// 卸载所有监听器
events.unregisterAll();
```

### SFScheduler - 调度

```java
SFScheduler scheduler = api.scheduler();

// 主线程同步执行
scheduler.run(() -> {
    player.sendMessage("在主线程执行");
});

// 异步执行（不要在异步中调用 Bukkit API！）
scheduler.runAsync(() -> {
    // 数据库查询、HTTP 请求等
});

// 延迟执行（20 ticks = 1 秒）
scheduler.runLater(() -> {
    player.sendMessage("1 秒后执行");
}, 20L);

// 定时执行
scheduler.runTimer(() -> {
    api.broadcast("每 5 秒广播一次");
}, 0L, 100L);  // delay=0, period=100 ticks
```

### SFPlayerOps - 玩家查找

```java
SFPlayerOps players = api.players();

Player p1 = players.byName("Notch");
Player p2 = players.byId(uuid);
```

### SFServerOps - 服务器操作

```java
SFServerOps server = api.server();

server.server();                  // 获取 Bukkit Server
server.broadcast("全服广播");
server.broadcast("permission.node", "只有特定权限的玩家能看到");
server.msg(sender, "发送消息给 sender");
```

## 异常处理

所有 API 方法都会捕获内部异常并通过 logger 输出，不会抛出异常中断调用方代码。

但 `SFApi.get()` 在 API 未注册时会抛出 `IllegalStateException`，建议先检查：

```java
if (!SFApi.isAvailable()) {
    getLogger().warning("SF API 不可用，相关功能已禁用");
    return;
}
SFApi api = SFApi.get();
```

## 线程安全

| 方法 | 线程安全 | 说明 |
|------|----------|------|
| `logger.*` | ✅ | 完全线程安全 |
| `economy.balance/give/take/set/transfer` | ⚠️ | 读取可异步，写入建议主线程 |
| `economy.format` | ✅ | 纯计算 |
| `events.on/register` | ⚠️ | 必须主线程调用 |
| `scheduler.runAsync` | ✅ | 任何线程可调用 |
| `scheduler.run/runLater/runTimer` | ⚠️ | 必须主线程调用 |
| `teleport` | ⚠️ | 必须主线程调用 |
| `broadcast/msg` | ⚠️ | 必须主线程调用 |

> 💡 不确定时，用 `api.run(() -> { ... })` 包裹代码确保主线程执行。

## 版本兼容

API 遵循语义化版本：

- **Major**（如 v2 → v3）：破坏性变更
- **Minor**（如 v2.1 → v2.2）：新增功能，向后兼容
- **Patch**（如 v2.1.1 → v2.1.2）：Bug 修复

当前 API 版本：**v2**

包名 `server.sf.model.api.v2` 中的 `v2` 即为 Major 版本号。未来如有破坏性变更会新增 `v3` 包并保留 `v2`。
