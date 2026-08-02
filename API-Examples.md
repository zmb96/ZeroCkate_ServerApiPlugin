# API 接入示例

本指南演示如何在自己的插件中接入 SF API。

## 前置要求

- 一个 Bukkit/Paper 插件项目
- JDK 21+
- Maven 或 Gradle

## 第一步：添加依赖

### Maven

在你的 `pom.xml` 中：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- SF 插件 API -->
    <dependency>
        <groupId>com.github.yourname</groupId>
        <artifactId>ZeroCkate_SFServerPlugin</artifactId>
        <version>main-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.yourname:ZeroCkate_SFServerPlugin:main-SNAPSHOT")
}
```

> ⚠️ 使用 `provided` / `compileOnly` 作用域，不要把 SF 打包进你的 jar。

## 第二步：声明依赖

在你的 `plugin.yml` 中：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.myplugin.MyPlugin
api-version: '1.21.5'

# 声明依赖（让 SF 先加载）
depend: [ZeroCkate_SFServerPlugin]
# 或者软依赖（SF 不存在也能加载）
softdepend: [ZeroCkate_SFServerPlugin]
```

- 用 `depend`：你的插件**强依赖** SF，SF 必须存在才能启用
- 用 `softdepend`：你的插件**软依赖** SF，SF 不存在时降级运行

## 第三步：获取 API

### 最简单的用法

```java
package com.example.myplugin;

import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.SFApi;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!SFApi.isAvailable()) {
            getLogger().warning("SF API 不可用，相关功能已禁用");
            return;
        }

        SFApi api = SFApi.get();
        api.info("MyPlugin 已接入 SF API！");
    }
}
```

### 缓存 API 实例

```java
public class MyPlugin extends JavaPlugin {

    private SFApi sf;

    @Override
    public void onEnable() {
        if (SFApi.isAvailable()) {
            sf = SFApi.get();
            sf.info("MyPlugin 已接入 SF API");
        } else {
            getLogger().warning("SF API 不可用");
        }
    }

    public SFApi sf() {
        return sf;
    }
}
```

## 实战示例

### 示例 1：登录奖励

玩家登录时给予 100 金币并广播欢迎消息。

```java
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import server.sf.model.api.v2.SFApi;

public class LoginBonusListener implements Listener {

    private final SFApi sf;

    public LoginBonusListener(SFApi sf) {
        this.sf = sf;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // 异步给钱（注意：经济写入操作应在主线程，这里只是演示）
        sf.run(() -> {
            boolean ok = sf.giveMoney(e.getPlayer(), 100);
            if (ok) {
                sf.msg(e.getPlayer(), "§a登录奖励：100 金币");
            }
        });

        // 广播
        sf.broadcast("§e" + e.getPlayer().getName() + " §a加入了服务器！");
    }
}
```

注册监听器：

```java
@Override
public void onEnable() {
    SFApi sf = SFApi.get();
    getServer().getPluginManager().registerEvents(new LoginBonusListener(sf), this);
}
```

### 示例 2：自定义商店

玩家右键牌子时扣钱给物品。

```java
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import server.sf.model.api.v2.SFApi;

public class SignShopListener implements Listener {

    private final SFApi sf;

    public SignShopListener(SFApi sf) {
        this.sf = sf;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;

        // 假设第一行 [Shop] 第二行价格 第三行物品 ID
        var sign = (org.bukkit.block.Sign) e.getClickedBlock().getState();
        if (!sign.getLine(0).equals("[Shop]")) return;

        double price = Double.parseDouble(sign.getLine(1));
        var player = e.getPlayer();

        if (sf.balance(player) < price) {
            sf.msg(player, "§c金币不足，需要 " + sf.formatMoney(price));
            return;
        }

        if (sf.takeMoney(player, price)) {
            player.getInventory().addItem(new ItemStack(org.bukkit.Material.DIAMOND, 1));
            sf.msg(player, "§a购买成功！剩余余额：" + sf.formatMoney(sf.balance(player)));
        }
    }
}
```

### 示例 3：定时全服公告

每 10 分钟广播一次。

```java
@Override
public void onEnable() {
    SFApi sf = SFApi.get();

    // 12000 ticks = 10 分钟
    sf.runTimer(() -> {
        sf.broadcast("§6===== 服务器公告 =====");
        sf.broadcast("§a欢迎来到我们的服务器！");
        sf.broadcast("§a输入 /sh 查看帮助");
    }, 0L, 12000L);
}
```

### 示例 4：玩家死亡惩罚

死亡时扣除 10% 金币。

```java
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import server.sf.model.api.v2.SFApi;

public class DeathPenaltyListener implements Listener {

    private final SFApi sf;

    public DeathPenaltyListener(SFApi sf) {
        this.sf = sf;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var player = e.getEntity();
        double balance = sf.balance(player);
        if (balance <= 0) return;

        double penalty = balance * 0.10;
        sf.takeMoney(player, penalty);
        sf.msg(player, "§c死亡惩罚：扣除 " + sf.formatMoney(penalty) + " 金币");
    }
}
```

### 示例 5：使用 SF 的事件系统

不用自己实现 Listener，直接用 SF 的链式 API。

```java
@Override
public void onEnable() {
    SFApi sf = SFApi.get();

    // 链式注册多个事件
    sf.events()
        .on(org.bukkit.event.player.PlayerJoinEvent.class, e -> {
            sf.info("玩家加入：" + e.getPlayer().getName());
        })
        .on(org.bukkit.event.player.PlayerQuitEvent.class, e -> {
            sf.info("玩家退出：" + e.getPlayer().getName());
        })
        .on(org.bukkit.event.entity.EntityDeathEvent.class, e -> {
            if (e.getEntity() instanceof org.bukkit.entity.Player p) {
                sf.broadcast("§c" + p.getName() + " 死了！");
            }
        });

    // 使用分类快捷方法
    sf.events().player().join(e -> {
        sf.giveMoney(e.getPlayer(), 50);
    });
}
```

> 💡 注意：通过 `sf.events().on()` 注册的监听器由 SF 管理，**不需要** 再调用 `getServer().getPluginManager().registerEvents()`。

### 示例 6：异步数据库查询 + 主线程更新

```java
public void showStats(Player player) {
    SFApi sf = SFApi.get();

    // 异步查询（不要在异步中调用 Bukkit API）
    sf.runAsync(() -> {
        String stats = queryFromDatabase(player.getUniqueId());

        // 切回主线程更新 UI
        sf.run(() -> {
            sf.msg(player, "§6===== 你的统计数据 =====");
            sf.msg(player, stats);
        });
    });
}
```

### 示例 7：跨插件传送

集成 SF 的传送系统，享受冷却/延迟/防移动等特性。

```java
public void teleportToLobby(Player player) {
    SFApi sf = SFApi.get();

    // 方法 1：直接传送（绕过冷却/延迟）
    var lobbyLoc = new Location(Bukkit.getWorld("world"), 0, 64, 0);
    sf.teleport(player, lobbyLoc);

    // 方法 2：通过 TeleportManager 享受完整特性（冷却、延迟、防移动）
    // 注意：这需要 SF 实现，且 teleportManager 已注册
    // sf.teleport().teleportDelayed(player, lobbyLoc, "custom", 60);  // 3 秒延迟
}
```

### 示例 8：完整示例 - 简单的工资系统

```java
package com.example.myplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import server.sf.model.api.v2.SFApi;

public class SalaryPlugin extends JavaPlugin {

    private SFApi sf;

    @Override
    public void onEnable() {
        if (!SFApi.isAvailable()) {
            getLogger().severe("需要 SF 插件！");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        sf = SFApi.get();
        sf.info("工资系统已启动");

        // 每天 12:00 发工资（这里简化为每 24 分钟演示一次）
        sf.runTimer(this::paySalary, 0L, 28800L);  // 1440 * 20 ticks
    }

    private void paySalary() {
        for (var player : Bukkit.getOnlinePlayers()) {
            // 基础工资
            double salary = 100;

            // OP 多发 50%
            if (player.isOp()) {
                salary *= 1.5;
            }

            // 发工资
            if (sf.giveMoney(player, salary)) {
                sf.msg(player, "§a=========================");
                sf.msg(player, "§a  日工资到账：" + sf.formatMoney(salary));
                sf.msg(player, "§a  当前余额：" + sf.formatMoney(sf.balance(player)));
                sf.msg(player, "§a=========================");
            }
        }
        sf.info("日工资发放完成");
    }
}
```

## 调试技巧

### 1. 检查 API 是否就绪

```java
sf.info("Economy ready: " + sf.economy().ready());
sf.info("Essentials: " + sf.economy().hasEssentials());
sf.info("Vault: " + sf.economy().hasVault());
sf.info("Database: " + server.sf.model.api.v2.database.DatabaseManager.ready());
```

### 2. 安全调用

```java
public void safeGiveMoney(Player p, double amount) {
    sf.run(() -> {  // 确保主线程
        try {
            if (sf.economy().ready()) {
                sf.giveMoney(p, amount);
            } else {
                sf.warn("经济系统未就绪，无法给 " + p.getName() + " 发钱");
            }
        } catch (Throwable t) {
            sf.error("给钱失败", t);
        }
    });
}
```

### 3. 监听 SF 的状态

```java
sf.events().server().pluginEnable(e -> {
    if (e.getPlugin().getName().equals("Essentials")) {
        sf.info("Essentials 已加载，经济系统可能可用");
    }
});
```

## 下一步

- [API 接口文档](API-Reference) - 完整方法列表
- [命令参考](Commands) - SF 提供的命令
- [常见问题](FAQ) - 解决接入问题
