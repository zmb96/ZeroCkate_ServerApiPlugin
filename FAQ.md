# 常见问题

## 安装相关

### Q: 启动后报错 `java.lang.NoClassDefFoundError: server/sf/model/api/v2/SFApi`

**A**：你的服务器没有正确加载 SF 插件。检查：

1. jar 文件是否在 `plugins/` 目录下
2. 启动顺序：SF 应该在其他依赖它的插件之前加载（在 `plugin.yml` 中声明 `depend: [ZeroCkate_SFServerPlugin]`）
3. 控制台日志中是否有 SF 启动失败的错误

### Q: 经济系统显示 `Economy ready: false`

**A**：SF 没有检测到任何经济后端。检查：

1. 是否安装了 [EssentialsX](https://essentialsx.net/) 或 [Vault](https://www.spigotmc.org/resources/vault.34315/)
2. `plugin.yml` 中 `softdepend` 是否包含 `Essentials, Vault`（默认已配置）
3. 重启服务器，看启动日志中是否显示 `Essentials=true` 或 `Vault=true`

> 💡 如果只想用 Vault，建议同时安装一个经济提供者插件（如 EssentialsX）。Vault 本身只是桥接，不提供经济数据。

### Q: 数据库报错 `SQLException: database is locked`

**A**：SQLite 在并发写入时会锁定。解决方案：

1. 切换到 MySQL（在 `config.yml` 中 `database.mysql.enabled: true`）
2. 或减少异步数据库操作

### Q: 切换到 MySQL 后报错 `Communications link failure`

**A**：检查 MySQL 连接：

1. MySQL 服务是否在运行
2. 主机/端口是否正确
3. 用户名/密码是否正确
4. 数据库是否存在（需要手动创建）
5. 防火墙是否放行 3306 端口

```sql
CREATE DATABASE minecraft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 命令相关

### Q: `/home` 提示家不存在

**A**：检查家的名称。如果不指定名称，默认使用 `default`：

```
/home           # 访问名为 "default" 的家
/home myhome    # 访问名为 "myhome" 的家
/sethome myhome # 创建名为 "myhome" 的家
```

用 `/homes` 查看所有家。

### Q: `/tpa` 请求没反应

**A**：检查：

1. 对方是否在线（`/tpa` 只能发给在线玩家）
2. 对方是否已经有待处理请求（一次只能有一个）
3. 是否被对方用 `/tpdeny` 拒绝
4. 请求是否已超时（默认 60 秒）

### Q: 传送提示 "你移动了，传送已取消"

**A**：这是**延迟传送**机制。在传送延迟期间移动会取消传送。

- 修改 `config.yml` 中的 `teleport.delay.*` 为 `0` 可以禁用延迟
- 拥有 `sf.teleport.bypass` 权限可以跳过延迟（注：当前实现是冷却跳过，延迟未跳过）

### Q: `/vanish` 后 OP 也看不到我

**A**：需要给 OP 玩家单独赋予权限：

```bash
lp user 你的名字 permission set sf.admin.seevanished true
```

### Q: `/gm` 命令的参数是什么

**A**：支持以下所有写法：

| 数字 | 缩写 | 全名 | 模式 |
|------|------|------|------|
| 0 | s | survival | 生存 |
| 1 | c | creative | 创造 |
| 2 | a | adventure | 冒险 |
| 3 | sp | spectator | 旁观 |

例如 `/gm 1` 和 `/gm creative` 等价。

---

## API 相关

### Q: `SFApi.get()` 抛出 `IllegalStateException`

**A**：SF API 没有被注册。可能原因：

1. SF 插件未启用（检查 `/plugins` 命令）
2. 你的插件先于 SF 加载（在 `plugin.yml` 中添加 `depend: [ZeroCkate_SFServerPlugin]`）
3. SF 启动失败（检查控制台日志）

正确做法：

```java
if (!SFApi.isAvailable()) {
    getLogger().warning("SF API 不可用");
    return;
}
SFApi api = SFApi.get();
```

### Q: 调用 `giveMoney` 返回 `false`

**A**：可能原因：

1. 经济系统未就绪（先检查 `api.economy().ready()`）
2. 玩家没有经济账户（先检查 `api.economy().hasAccount(player)`）
3. 金额为负数（SF 会拒绝负数操作）
4. 操作在异步线程执行但 Essentials 不支持（改用 `api.run(() -> api.giveMoney(p, 100))`）

### Q: 异步线程中调用 API 报错

**A**：Bukkit 的大部分 API 都**不是线程安全**的。在异步线程中：

- ✅ 可以调用：`logger.*`, `economy.balance/format`, `scheduler.runAsync`
- ❌ 不可调用：`teleport`, `broadcast`, `msg`, `events.on`, `economy.give/take/set`

正确做法：异步中查询数据，主线程中修改游戏状态：

```java
api.runAsync(() -> {
    double balance = api.balance(player);  // 异步查询
    api.run(() -> {                         // 切回主线程
        api.giveMoney(player, 100);
        api.msg(player, "钱到账了");
    });
});
```

### Q: 通过 `sf.events().on()` 注册的监听器不生效

**A**：检查：

1. 是否在 `onEnable()` 中注册（不要在 `onLoad()` 中）
2. 事件类是否正确导入（例如 `AsyncPlayerChatEvent` vs `PlayerChatEvent`）
3. 是否被其他插件取消（设置更高优先级 `EventPriority.HIGH`）
4. 控制台是否有异常日志

### Q: 编译报错找不到 `SFApi` 类

**A**：Maven/Gradle 依赖配置问题。检查：

1. 是否添加了 JitPack 仓库
2. 依赖 scope 是否正确（`provided` 或 `compileOnly`）
3. 是否执行了 `mvn clean install` 刷新依赖

```xml
<dependency>
    <groupId>com.github.yourname</groupId>
    <artifactId>ZeroCkate_SFServerPlugin</artifactId>
    <version>main-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Q: 如何在自己的插件中扩展 SF 的传送系统

**A**：通过 `api.teleport()` 访问 `TeleportManager`：

```java
SFApi api = SFApi.get();
TeleportManager tp = ((SF) api).teleport();

// 立即传送（带音效、记录返回点）
tp.teleportNow(player, location, "myplugin");

// 延迟传送（带冷却、防移动）
tp.teleportDelayed(player, location, "myplugin", 60);  // 3 秒延迟

// 让玩家返回上次位置
tp.back(player);
```

> ⚠️ 注意：`api.teleport()` 是 `SF` 实现类的方法，不在 `SFApi` 接口中。需要强转或直接使用 `SF.sf()`。

---

## 性能相关

### Q: 服务器 TPS 下降

**A**：排查步骤：

1. 使用 `/tps` 查看当前 TPS
2. 检查是否有大量异步数据库操作（改为批量操作）
3. 检查 `sf.events().on()` 注册的监听器是否过多或过重
4. 切换到 MySQL 避免 SQLite 锁争用

### Q: 数据库查询慢

**A**：

1. SQLite：启用 WAL 模式（默认已启用）
2. MySQL：确保 `homes(uuid, name)` 和 `warps(name)` 有索引（建表时已添加 PRIMARY KEY）
3. 避免在循环中频繁查询，用 `getHomes(uuid)` 一次获取所有

### Q: 内存占用高

**A**：SF 本身内存占用很小（< 10MB）。可能原因：

1. `events.unregisterAll()` 未在卸载时调用（导致监听器泄漏）
2. 自定义监听器中持有大对象引用
3. 缓存未清理

---

## 其他

### Q: 如何卸载插件而不丢失数据

**A**：

1. 停止服务器
2. 备份 `plugins/ZeroCkate_SFServerPlugin/data.db`（SQLite）或导出 MySQL 数据库
3. 删除 jar 文件
4. 数据保留在备份中，下次安装时恢复即可

### Q: 多个服务器能共享家数据吗

**A**：可以。所有服务器连同一个 MySQL 数据库，并使用相同的 `prefix`：

```yaml
# 所有服务器
database:
  mysql:
    enabled: true
    host: shared.db.example.com
    database: mc_network
    prefix: "sf_shared_"
```

如果想让数据相互独立，使用不同的 `prefix`。

### Q: 如何向作者反馈 bug

**A**：

1. 在 [GitHub Issues](https://github.com/yourname/ZeroCkate_SFServerPlugin/issues) 提交 issue
2. 附上以下信息：
   - SF 插件版本
   - 服务器类型（Paper/Spigot）和版本
   - 完整的错误日志（堆栈跟踪）
   - 复现步骤

### Q: 可以商用吗

**A**：SF 使用 MIT 协议，允许商用、修改、分发，但需保留版权声明。详见 [LICENSE](https://github.com/yourname/ZeroCkate_SFServerPlugin/blob/main/LICENSE)。
