# ZeroCkate ServerManagementPlugin

> 一款现代化、模块化的 Minecraft 服务器管理插件，提供完整的管理员工具、传送系统、经济系统，并对外暴露简洁的 API 供其他插件调用。

![Java](https://img.shields.io/badge/Java-21-orange)
![Bukkit](https://img.shields.io/badge/Bukkit-1.21.5-green)
![License](https://img.shields.io/badge/License-MIT-blue)

## 目录

- [✨ 特性](#-特性)
- [📦 安装](#-安装)
- [🚀 快速开始](#-快速开始)
- [🎮 命令参考](#-命令参考)
- [🔐 权限列表](#-权限列表)
- [⚙️ 配置文件](#️-配置文件)
- [💻 开发者 API](#-开发者-api)
    - [API 接口文档](#api-接口文档)
    - [API 接入示例](#api-接入示例)
- [❓ 常见问题](#-常见问题)
- [📝 变更日志](#-变更日志)
- [🤝 贡献指南](#-贡献指南)
- [📄 License](#-license)

---

## ✨ 特性

- 🚀 **极简 API**：一行代码完成日志、经济、传送、调度等操作
- 🎯 **28+ 内置命令**：传送、家、传送点、TPA、管理员工具一应俱全
- 💰 **双后端经济**：自动检测 EssentialsX / Vault，无需手动配置
- 🗄️ **持久化存储**：内置 SQLite / MySQL 切换，零配置开箱即用
- 🔔 **完整事件系统**：120+ Bukkit 事件分类封装，链式调用
- 🛡️ **管理员工具**：无敌、隐身、飞行、治疗、清包等 12 个常用命令
- 🔌 **第三方接入**：通过 Bukkit ServicesManager 暴露 `SFApi` 接口
- ⚡ **异步安全**：经济操作自动回滚、传送防移动取消

---

## 📦 安装

### 环境要求

| 组件 | 版本 |
|------|------|
| Minecraft Server | 1.21.5+ |
| Java | 21+ |
| Bukkit/Paper API | 1.21.5 |

**支持的服务端**：Paper、Purpur、Folia（实验性）、Spigot、CraftBukkit

### 可选依赖

以下插件非必需，但推荐安装以解锁更多功能：

| 插件 | 用途 | 下载 |
|------|------|------|
| EssentialsX | 经济系统后端（优先） | [essentialsx.net](https://essentialsx.net/) |
| Vault | 经济系统后端（回退） | [spigotmc.org](https://www.spigotmc.org/resources/vault.34315/) |

> 💡 如果两个都安装，会优先使用 EssentialsX；若都未安装，经济相关功能会自动禁用，但不影响其他功能。

### 安装步骤

**1. 下载插件**

从 [Releases 页面](https://github.com/zmb96/ZeroCkate_ServerManagementPlugin/releases) 下载最新版本的 `.jar` 文件。

**2. 放入插件目录**

将 jar 文件放入服务器的 `plugins/` 目录：

```
你的服务器/
├── plugins/
│   └── ZeroCkate_ServerManagementPlugin-x.x.x.jar   ← 放这里
├── server.jar
└── ...
```

**3. 启动服务器**

首次启动会自动生成以下文件：

```
plugins/ZeroCkate_SFServerPlugin/
├── config.yml          ← 主配置文件
├── data.db             ← SQLite 数据库（默认）
└── help.txt            ← /sh 命令的帮助文本
```

**4. 验证安装**

在控制台或游戏内执行 `/servermanagement`，看到帮助信息即表示安装成功。

查看经济系统状态（控制台日志）：

```
[INFO] Database ready: true
[INFO] Economy ready: true (Essentials=true, Vault=false)
[INFO] 插件已加载
```

### 升级

1. **停止服务器**
2. 备份 `plugins/ZeroCkate_SFServerPlugin/` 目录（特别是 `data.db`）
3. 替换为新版本的 jar 文件
4. 启动服务器

> ⚠️ 升级前务必备份！数据库结构可能随版本变化。

### 卸载

1. 停止服务器
2. 执行 `/servermanagement` 确认插件状态
3. 删除 `plugins/ZeroCkate_SFServerPlugin.jar`
4. （可选）删除 `plugins/ZeroCkate_SFServerPlugin/` 目录以清除所有数据

### 切换到 MySQL

默认使用 SQLite，若要切换到 MySQL：

1. 编辑 `config.yml`：

```yaml
database:
  mysql:
    enabled: true
    host: localhost
    port: 3306
    database: minecraft
    user: root
    password: "你的密码"
    prefix: "sf_"
```

2. 在 MySQL 中创建数据库：

```sql
CREATE DATABASE minecraft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 重启服务器，表会自动创建。

---

## 🚀 快速开始

本指南将带你在 5 分钟内完成 SF 插件的基础配置。

### 1. 设置出生点

作为 OP，站在你想要的位置执行：

```
/setspawn
```

之后玩家可以用 `/spawn` 回到这个位置。

### 2. 创建公共传送点

站在你想让玩家传送到的位置：

```
/setwarp shop
/setwarp pvp
/setwarp spawn
```

玩家输入 `/warp shop` 即可传送到商店。查看所有传送点：`/warps`

### 3. 设置个人家

玩家可以设置自己的家：

```
/sethome home       # 设置名为 "home" 的家
/sethome farm       # 设置名为 "farm" 的家
/home home          # 传送到 "home"
/homes              # 查看所有家
/delhome farm       # 删除 "farm"
```

### 4. TPA 请求传送

玩家之间互相传送：

```
玩家A: /tpa 玩家B      # 请求传送到 玩家B
玩家B: /tpaccept       # 接受
玩家B: /tpdeny         # 或拒绝

玩家A: /tpahere 玩家B  # 邀请 玩家B 过来
玩家B: /tpaccept       # 接受
```

请求默认 60 秒超时，可在 `config.yml` 修改。

### 5. 使用管理员工具

常用管理员命令：

```
/gm 1                # 切换到创造模式
/gm 0                # 切换到生存模式
/fly                 # 切换飞行
/god                 # 切换无敌
/vanish              # 切换隐身
/heal                # 治疗自己
/feed                # 喂饱自己
/clear 玩家名         # 清空某玩家的背包
/speed 5             # 设置速度为 5
```

所有管理员命令都支持指定其他玩家：

```
/heal 玩家名
/fly 玩家名
/gm 1 玩家名
```

### 6. 返回上次位置

每次传送前都会自动记录位置，可以用 `/back` 返回：

```
/spawn               # 传送到出生点
/back                # 返回刚才的位置
```

### 7. 配置冷却和延迟

编辑 `config.yml`：

```yaml
teleport:
  cooldown:
    spawn: 5         # /spawn 冷却 5 秒
    home: 5
    warp: 5
    back: 10
    tpa: 10
  delay:
    spawn: 3         # /spawn 延迟 3 秒（期间移动会取消）
    home: 3
  tpa:
    timeout: 60      # TPA 请求 60 秒超时
```

重载配置：`/servermanagement reload`

### 8. 权限设置

使用 LuckPerms 或类似插件分配权限：

```
# 给所有玩家基础传送权限（默认就有）
lp group default permission set sf.teleport.bypass false

# 给 VIP 跳过冷却权限
lp group vip permission set sf.teleport.bypass true

# 给管理员所有权限
lp group admin permission set sf.admin.* true
```

---

## 🎮 命令参考

### 传送命令

#### `/spawn` - 传送到出生点

| 用法 | 说明 |
|------|------|
| `/spawn` | 传送到当前世界的出生点 |
| `/spawn set` | 设置当前世界的出生点（需要 `sf.spawn.set`） |

**别名**：`spawnpoint` ｜ **权限**：`sf.spawn.set`（仅设置需要）

#### `/setspawn` - 设置出生点

| 用法 | 说明 |
|------|------|
| `/setspawn` | 将当前位置设为当前世界的出生点 |

**权限**：`sf.spawn.set`

#### `/home` - 传送到家

| 用法 | 说明 |
|------|------|
| `/home` | 传送到名为 `default` 的家 |
| `/home <名称>` | 传送到指定名称的家 |

#### `/sethome` - 设置家

| 用法 | 说明 |
|------|------|
| `/sethome` | 设置名为 `default` 的家 |
| `/sethome <名称>` | 设置指定名称的家 |

#### `/delhome` - 删除家

| 用法 | 说明 |
|------|------|
| `/delhome <名称>` | 删除指定名称的家 |

#### `/homes` - 列出所有家

| 用法 | 说明 |
|------|------|
| `/homes` | 列出你所有的家 |

#### `/warp` - 传送到公共传送点

| 用法 | 说明 |
|------|------|
| `/warp` | 列出所有传送点 |
| `/warp <名称>` | 传送到指定传送点 |

#### `/setwarp` - 设置公共传送点

| 用法 | 说明 |
|------|------|
| `/setwarp <名称>` | 在当前位置创建公共传送点 |

**权限**：`sf.warp.set`

#### `/delwarp` - 删除公共传送点

| 用法 | 说明 |
|------|------|
| `/delwarp <名称>` | 删除指定传送点 |

**权限**：`sf.warp.set`

#### `/warps` - 列出所有传送点

| 用法 | 说明 |
|------|------|
| `/warps` | 列出服务器所有公共传送点 |

#### `/back` - 返回上次位置

| 用法 | 说明 |
|------|------|
| `/back` | 返回上次传送前的位置 |

**别名**：`return`

> 💡 每次传送都会记录前位置。

#### `/tp` - 管理员传送

| 用法 | 说明 |
|------|------|
| `/tp <玩家>` | 传送到指定玩家 |
| `/tp <玩家1> <玩家2>` | 将玩家1传送到玩家2 |
| `/tp <x> <y> <z>` | 传送到指定坐标 |

**权限**：`sf.admin.tp`

#### `/tphere` - 召唤玩家

| 用法 | 说明 |
|------|------|
| `/tphere <玩家>` | 将指定玩家传送到你身边 |

**权限**：`sf.admin.tp`

### TPA 命令

| 命令 | 用法 | 说明 |
|------|------|------|
| `/tpa` | `/tpa <玩家>` | 请求传送到指定玩家身边 |
| `/tpahere` | `/tpahere <玩家>` | 邀请指定玩家传送到你身边 |
| `/tpaccept` | `/tpaccept` | 接受当前待处理的传送请求 |
| `/tpdeny` | `/tpdeny` | 拒绝当前待处理的传送请求 |
| `/tpcancel` | `/tpcancel` | 取消你发起的传送请求 |

### 管理员命令

#### `/gm` - 切换游戏模式

| 用法 | 说明 |
|------|------|
| `/gm <模式>` | 切换自己的游戏模式 |
| `/gm <模式> <玩家>` | 切换指定玩家的游戏模式 |

**别名**：`gamemode` ｜ **权限**：`sf.admin.gamemode`

**模式参数**：
- `0` / `s` / `survival` - 生存
- `1` / `c` / `creative` - 创造
- `2` / `a` / `adventure` - 冒险
- `3` / `sp` / `spectator` - 旁观

#### `/fly` - 切换飞行

| 用法 | 说明 |
|------|------|
| `/fly` | 切换自己的飞行状态 |
| `/fly <玩家>` | 切换指定玩家的飞行状态 |

**权限**：`sf.admin.fly`

#### `/heal` - 治疗

| 用法 | 说明 |
|------|------|
| `/heal` | 治疗自己（恢复生命值、清除药水效果、熄灭） |
| `/heal <玩家>` | 治疗指定玩家 |

**权限**：`sf.admin.heal`

#### `/feed` - 喂饱

| 用法 | 说明 |
|------|------|
| `/feed` | 喂饱自己（饥饿值+饱和度满） |
| `/feed <玩家>` | 喂饱指定玩家 |

**权限**：`sf.admin.feed`

#### `/god` - 无敌模式

| 用法 | 说明 |
|------|------|
| `/god` | 切换自己的无敌状态 |
| `/god <玩家>` | 切换指定玩家的无敌状态 |

**权限**：`sf.admin.god`

> 💡 无敌状态下不会受到任何伤害。状态在重登后保持。

#### `/vanish` - 隐身

| 用法 | 说明 |
|------|------|
| `/vanish` | 切换自己的隐身状态 |
| `/vanish <玩家>` | 切换指定玩家的隐身状态 |

**权限**：`sf.admin.vanish`

> 💡 隐身后其他玩家看不到你，但拥有 `sf.admin.seevanished` 权限的玩家仍可见。状态在重登后保持。

#### `/ec` - 打开末影箱

| 用法 | 说明 |
|------|------|
| `/ec` | 打开自己的末影箱 |
| `/ec <玩家>` | 打开指定玩家的末影箱 |

**别名**：`enderchest` ｜ **权限**：`sf.admin.enderchest`

#### `/wb` - 打开工作台

| 用法 | 说明 |
|------|------|
| `/wb` | 打开一个 3x3 工作台界面 |

**别名**：`workbench`, `craft` ｜ **权限**：`sf.admin.workbench`

#### `/clear` - 清空背包

| 用法 | 说明 |
|------|------|
| `/clear` | 清空自己的背包和末影箱 |
| `/clear <玩家>` | 清空指定玩家的背包和末影箱 |

**别名**：`clearinv` ｜ **权限**：`sf.admin.clear`

#### `/speed` - 设置速度

| 用法 | 说明 |
|------|------|
| `/speed <1-10>` | 设置自己的速度 |
| `/speed <1-10> <玩家>` | 设置指定玩家的速度 |

**权限**：`sf.admin.speed`

> 💡 如果在飞行中，设置飞行速度；否则设置行走速度。

#### `/suicide` - 自杀

| 用法 | 说明 |
|------|------|
| `/suicide` | 立即杀死自己 |

**别名**：`killme`

### 系统命令

| 命令 | 用法 | 说明 | 权限 |
|------|------|------|------|
| `/servermanagement` | `/servermanagement [reload]` | 插件管理 | `servermanagement.use` |
| `/ty` | `/ty <意见内容>` | 提交意见反馈 | - |
| `/ru` | `/ru` | 显示服务器规则 | - |
| `/sh` | `/sh` | 显示帮助信息 | - |
| `/giveit` | `/giveit` | 给予预设物品 | - |

`/servermanagement` **别名**：`sm`, `svm`

---

## 🔐 权限列表

### 默认权限

以下权限默认所有玩家都拥有（无需手动赋予）：

| 权限 | 说明 |
|------|------|
| `servermanagement.use` | 使用 `/servermanagement` 命令 |

### 传送权限

| 权限 | 说明 | 默认 |
|------|------|------|
| `sf.spawn.set` | 设置出生点 `/setspawn` | OP |
| `sf.warp.set` | 设置/删除公共传送点 | OP |
| `sf.teleport.bypass` | 跳过传送冷却 | OP |
| `sf.admin.tp` | 管理员传送 `/tp` `/tphere` | OP |

> 💡 普通的 `/spawn` `/home` `/warp` `/tpa` 等命令**所有玩家默认可用**。

### 管理员权限

| 权限 | 说明 | 默认 |
|------|------|------|
| `sf.admin.gamemode` | 切换游戏模式 `/gm` | OP |
| `sf.admin.fly` | 切换飞行 `/fly` | OP |
| `sf.admin.heal` | 治疗 `/heal` | OP |
| `sf.admin.feed` | 喂饱 `/feed` | OP |
| `sf.admin.god` | 无敌模式 `/god` | OP |
| `sf.admin.vanish` | 隐身 `/vanish` | OP |
| `sf.admin.seevanished` | 看到隐身玩家 | OP |
| `sf.admin.enderchest` | 打开末影箱 `/ec` | OP |
| `sf.admin.workbench` | 打开工作台 `/wb` | OP |
| `sf.admin.clear` | 清空背包 `/clear` | OP |
| `sf.admin.speed` | 设置速度 `/speed` | OP |

### 系统权限

| 权限 | 说明 | 默认 |
|------|------|------|
| `servermanagement.reload` | 重载配置文件 | OP |

### 推荐权限分配（LuckPerms）

**默认组**（所有玩家）：
```bash
lp group default permission set servermanagement.use true
```

**VIP 组**：
```bash
lp group vip permission set sf.teleport.bypass true
```

**管理员组**：
```bash
# 方法 1: 逐个赋予
lp group admin permission set sf.spawn.set true
lp group admin permission set sf.warp.set true
lp group admin permission set sf.admin.tp true
lp group admin permission set sf.admin.gamemode true
lp group admin permission set sf.admin.fly true
lp group admin permission set sf.admin.heal true
lp group admin permission set sf.admin.feed true
lp group admin permission set sf.admin.god true
lp group admin permission set sf.admin.vanish true
lp group admin permission set sf.admin.seevanished true
lp group admin permission set sf.admin.enderchest true
lp group admin permission set sf.admin.workbench true
lp group admin permission set sf.admin.clear true
lp group admin permission set sf.admin.speed true

# 方法 2: 使用通配符（如果你的权限插件支持）
lp group admin permission set sf.admin.* true
lp group admin permission set sf.* true
```

### 通配符权限

SF 支持以下通配符（需权限插件支持，如 LuckPerms）：

| 通配符 | 包含 |
|--------|------|
| `sf.admin.*` | 所有 `sf.admin.xxx` 权限 |
| `sf.*` | 所有 `sf.xxx` 权限 |
| `sf.spawn.*` | 出生点相关权限 |
| `sf.warp.*` | 传送点相关权限 |
| `sf.teleport.*` | 传送相关权限 |

---

## ⚙️ 配置文件

### 完整配置示例

```yaml
# ====== 数据库配置 ======
database:
  # 是否启用 MySQL（false 则使用 SQLite）
  mysql:
    enabled: false
    host: localhost
    port: 3306
    database: minecraft
    user: root
    password: ""
    prefix: "sf_"
  # SQLite 配置（mysql.enabled=false 时使用）
  sqlite:
    file: data.db

# ====== 传送系统配置 ======
teleport:
  # 冷却时间（秒），0 表示无冷却
  cooldown:
    spawn: 5
    home: 5
    warp: 5
    back: 10
    tpa: 10
    tpahere: 10
    tp: 0        # 管理员传送无冷却
    tphere: 0
  # 延迟传送（秒），0 表示立即传送
  delay:
    spawn: 3
    home: 3
    warp: 3
    back: 3
    tpa: 3
    tpahere: 3
  # TPA 请求超时（秒）
  tpa:
    timeout: 60
```

### 配置项详解

#### `database.mysql.enabled`

是否启用 MySQL。设为 `false` 则使用 SQLite。

#### `database.mysql.host` / `port` / `database` / `user` / `password`

MySQL 连接信息。`database` 是数据库名（需要预先创建）。

#### `database.mysql.prefix`

表名前缀。多服务器共用同一个数据库时有用：

```yaml
database:
  mysql:
    prefix: "sf_survival_"   # 表名会变成 sf_survival_homes 等
```

#### `database.sqlite.file`

SQLite 数据库文件名。文件位于 `plugins/ZeroCkate_SFServerPlugin/` 目录下。

#### `teleport.cooldown.*`

传送命令的冷却时间（秒）。同一玩家在冷却时间内无法再次使用该命令。

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `spawn` | 5 | `/spawn` 冷却 |
| `home` | 5 | `/home` 冷却 |
| `warp` | 5 | `/warp` 冷却 |
| `back` | 10 | `/back` 冷却 |
| `tpa` | 10 | `/tpa` 冷却 |
| `tpahere` | 10 | `/tpahere` 冷却 |
| `tp` | 0 | `/tp` 冷却（管理员） |
| `tphere` | 0 | `/tphere` 冷却（管理员） |

> 💡 拥有 `sf.teleport.bypass` 权限的玩家可以跳过冷却。

#### `teleport.delay.*`

传送延迟（秒）。玩家执行命令后不会立即传送，而是等待指定秒数。期间移动会取消传送。

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `spawn` | 3 | `/spawn` 延迟 |
| `home` | 3 | `/home` 延迟 |
| `warp` | 3 | `/warp` 延迟 |
| `back` | 3 | `/back` 延迟 |
| `tpa` | 3 | `/tpa` 接受后延迟 |
| `tpahere` | 3 | `/tpahere` 接受后延迟 |

> 💡 设为 `0` 表示立即传送（无延迟）。

#### `teleport.tpa.timeout`

TPA 请求超时时间（秒）。请求发出后，对方在指定时间内未响应则自动失效。

### 修改配置后

修改 `config.yml` 后，执行以下命令热重载（无需重启服务器）：

```
/servermanagement reload
# 或
/sm reload
```

### 常见配置场景

**场景 1：关闭所有冷却（休闲服）**

```yaml
teleport:
  cooldown:
    spawn: 0
    home: 0
    warp: 0
    back: 0
    tpa: 0
    tpahere: 0
```

**场景 2：长冷却防滥用（生存服）**

```yaml
teleport:
  cooldown:
    spawn: 30
    home: 30
    warp: 30
    back: 60
    tpa: 60
    tpahere: 60
  delay:
    spawn: 5
    home: 5
    warp: 5
    back: 5
    tpa: 5
    tpahere: 5
```

**场景 3：多服务器共享数据库**

```yaml
# 服务器 A（生存服）
database:
  mysql:
    enabled: true
    host: db.example.com
    port: 3306
    database: mc_network
    user: mc_user
    password: "xxx"
    prefix: "sf_survival_"

# 服务器 B（小游戏服）—— 仅 prefix 不同
database:
  mysql:
    enabled: true
    host: db.example.com
    port: 3306
    database: mc_network
    user: mc_user
    password: "xxx"
    prefix: "sf_minigame_"
```

**场景 4：纯立即传送（无延迟）**

```yaml
teleport:
  delay:
    spawn: 0
    home: 0
    warp: 0
    back: 0
    tpa: 0
    tpahere: 0
```

---

## 💻 开发者 API

SF 插件通过 Bukkit `ServicesManager` 对外暴露 `SFApi` 接口，其他插件可以通过它调用 SF 的所有功能。

### Maven 依赖

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.zmb96</groupId>
        <artifactId>ZeroCkate_ServerManagementPlugin</artifactId>
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
    compileOnly("com.github.zmb96:ZeroCkate_ServerManagementPlugin:main-SNAPSHOT")
}
```

> ⚠️ 使用 `provided` / `compileOnly` 作用域，不要把 SF 打包进你的 jar。

### 在 plugin.yml 中声明依赖

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

### API 接口文档

#### 接口概览

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

#### 获取 API 实例

**方法 1：静态方法（推荐）**

```java
if (SFApi.isAvailable()) {
    SFApi api = SFApi.get();
    api.info("成功接入 SF API！");
}
```

**方法 2：通过 ServicesManager**

```java
RegisteredServiceProvider<SFApi> rsp = getServer().getServicesManager().getRegistration(SFApi.class);
if (rsp != null) {
    SFApi api = rsp.getProvider();
}
```

#### 子模块详解

**SFLogger - 日志**

```java
SFLogger logger = api.logger();

logger.info("普通信息");
logger.info("格式化信息: %s 已上线", playerName);  // 支持 String.format
logger.warn("警告信息");
logger.error("错误信息");
logger.error("错误带异常", exception);
```

**SFEconomy - 经济系统**

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

**SFEvents - 事件系统**

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

**SFScheduler - 调度**

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

**SFPlayerOps - 玩家查找**

```java
SFPlayerOps players = api.players();

Player p1 = players.byName("Notch");
Player p2 = players.byId(uuid);
```

**SFServerOps - 服务器操作**

```java
SFServerOps server = api.server();

server.server();                  // 获取 Bukkit Server
server.broadcast("全服广播");
server.broadcast("permission.node", "只有特定权限的玩家能看到");
server.msg(sender, "发送消息给 sender");
```

#### 异常处理

所有 API 方法都会捕获内部异常并通过 logger 输出，不会抛出异常中断调用方代码。

但 `SFApi.get()` 在 API 未注册时会抛出 `IllegalStateException`，建议先检查：

```java
if (!SFApi.isAvailable()) {
    getLogger().warning("SF API 不可用，相关功能已禁用");
    return;
}
SFApi api = SFApi.get();
```

#### 线程安全

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

#### 版本兼容

API 遵循语义化版本：

- **Major**（如 v2 → v3）：破坏性变更
- **Minor**（如 v2.1 → v2.2）：新增功能，向后兼容
- **Patch**（如 v2.1.1 → v2.1.2）：Bug 修复

当前 API 版本：**v2**

包名 `server.sf.model.api.v2` 中的 `v2` 即为 Major 版本号。未来如有破坏性变更会新增 `v3` 包并保留 `v2`。

### API 接入示例

#### 最简单的用法

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

#### 缓存 API 实例

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

#### 示例 1：登录奖励

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
        sf.run(() -> {
            boolean ok = sf.giveMoney(e.getPlayer(), 100);
            if (ok) {
                sf.msg(e.getPlayer(), "§a登录奖励：100 金币");
            }
        });

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

#### 示例 2：自定义商店

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

#### 示例 3：定时全服公告

每 10 分钟广播一次。

```java
@Override
public void onEnable() {
    SFApi sf = SFApi.get();

    sf.runTimer(() -> {
        sf.broadcast("§6===== 服务器公告 =====");
        sf.broadcast("§a欢迎来到我们的服务器！");
        sf.broadcast("§a输入 /sh 查看帮助");
    }, 0L, 12000L);  // 12000 ticks = 10 分钟
}
```

#### 示例 4：玩家死亡惩罚

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

#### 示例 5：使用 SF 的事件系统

不用自己实现 Listener，直接用 SF 的链式 API。

```java
@Override
public void onEnable() {
    SFApi sf = SFApi.get();

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

    sf.events().player().join(e -> {
        sf.giveMoney(e.getPlayer(), 50);
    });
}
```

> 💡 注意：通过 `sf.events().on()` 注册的监听器由 SF 管理，**不需要** 再调用 `getServer().getPluginManager().registerEvents()`。

#### 示例 6：异步数据库查询 + 主线程更新

```java
public void showStats(Player player) {
    SFApi sf = SFApi.get();

    sf.runAsync(() -> {
        String stats = queryFromDatabase(player.getUniqueId());

        sf.run(() -> {
            sf.msg(player, "§6===== 你的统计数据 =====");
            sf.msg(player, stats);
        });
    });
}
```

#### 示例 7：跨插件传送

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

#### 示例 8：完整的工资系统

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

        sf.runTimer(this::paySalary, 0L, 28800L);  // 每 24 分钟
    }

    private void paySalary() {
        for (var player : Bukkit.getOnlinePlayers()) {
            double salary = 100;
            if (player.isOp()) {
                salary *= 1.5;
            }

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

#### 调试技巧

**1. 检查 API 是否就绪**

```java
sf.info("Economy ready: " + sf.economy().ready());
sf.info("Essentials: " + sf.economy().hasEssentials());
sf.info("Vault: " + sf.economy().hasVault());
sf.info("Database: " + server.sf.model.api.v2.database.DatabaseManager.ready());
```

**2. 安全调用**

```java
public void safeGiveMoney(Player p, double amount) {
    sf.run(() -> {
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

**3. 监听 SF 的状态**

```java
sf.events().server().pluginEnable(e -> {
    if (e.getPlugin().getName().equals("Essentials")) {
        sf.info("Essentials 已加载，经济系统可能可用");
    }
});
```

#### 扩展 SF 的传送系统

通过 `api.teleport()` 访问 `TeleportManager`：

```java
SFApi api = SFApi.get();
TeleportManager tp = ((SF) api).teleport();

tp.teleportNow(player, location, "myplugin");
tp.teleportDelayed(player, location, "myplugin", 60);  // 3 秒延迟
tp.back(player);
```

> ⚠️ 注意：`api.teleport()` 是 `SF` 实现类的方法，不在 `SFApi` 接口中。需要强转或直接使用 `SF.sf()`。

---

## ❓ 常见问题

### 安装相关

**Q: 启动后报错 `java.lang.NoClassDefFoundError: server/sf/model/api/v2/SFApi`**

A：你的服务器没有正确加载 SF 插件。检查：

1. jar 文件是否在 `plugins/` 目录下
2. 启动顺序：SF 应该在其他依赖它的插件之前加载（在 `plugin.yml` 中声明 `depend: [ZeroCkate_SFServerPlugin]`）
3. 控制台日志中是否有 SF 启动失败的错误

**Q: 经济系统显示 `Economy ready: false`**

A：SF 没有检测到任何经济后端。检查：

1. 是否安装了 [EssentialsX](https://essentialsx.net/) 或 [Vault](https://www.spigotmc.org/resources/vault.34315/)
2. `plugin.yml` 中 `softdepend` 是否包含 `Essentials, Vault`（默认已配置）
3. 重启服务器，看启动日志中是否显示 `Essentials=true` 或 `Vault=true`

**Q: 数据库报错 `SQLException: database is locked`**

A：SQLite 在并发写入时会锁定。解决方案：

1. 切换到 MySQL（在 `config.yml` 中 `database.mysql.enabled: true`）
2. 或减少异步数据库操作

**Q: 切换到 MySQL 后报错 `Communications link failure`**

A：检查 MySQL 连接：

1. MySQL 服务是否在运行
2. 主机/端口是否正确
3. 用户名/密码是否正确
4. 数据库是否存在（需要手动创建）
5. 防火墙是否放行 3306 端口

```sql
CREATE DATABASE minecraft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 命令相关

**Q: `/home` 提示家不存在**

A：检查家的名称。如果不指定名称，默认使用 `default`：

```
/home           # 访问名为 "default" 的家
/home myhome    # 访问名为 "myhome" 的家
/sethome myhome # 创建名为 "myhome" 的家
```

用 `/homes` 查看所有家。

**Q: `/tpa` 请求没反应**

A：检查：

1. 对方是否在线（`/tpa` 只能发给在线玩家）
2. 对方是否已经有待处理请求（一次只能有一个）
3. 是否被对方用 `/tpdeny` 拒绝
4. 请求是否已超时（默认 60 秒）

**Q: 传送提示 "你移动了，传送已取消"**

A：这是**延迟传送**机制。在传送延迟期间移动会取消传送。

- 修改 `config.yml` 中的 `teleport.delay.*` 为 `0` 可以禁用延迟
- 拥有 `sf.teleport.bypass` 权限可以跳过延迟

**Q: `/vanish` 后 OP 也看不到我**

A：需要给 OP 玩家单独赋予权限：

```bash
lp user 你的名字 permission set sf.admin.seevanished true
```

**Q: `/gm` 命令的参数是什么**

A：支持以下所有写法：

| 数字 | 缩写 | 全名 | 模式 |
|------|------|------|------|
| 0 | s | survival | 生存 |
| 1 | c | creative | 创造 |
| 2 | a | adventure | 冒险 |
| 3 | sp | spectator | 旁观 |

例如 `/gm 1` 和 `/gm creative` 等价。

### API 相关

**Q: `SFApi.get()` 抛出 `IllegalStateException`**

A：SF API 没有被注册。可能原因：

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

**Q: 调用 `giveMoney` 返回 `false`**

A：可能原因：

1. 经济系统未就绪（先检查 `api.economy().ready()`）
2. 玩家没有经济账户（先检查 `api.economy().hasAccount(player)`）
3. 金额为负数（SF 会拒绝负数操作）
4. 操作在异步线程执行但 Essentials 不支持（改用 `api.run(() -> api.giveMoney(p, 100))`）

**Q: 异步线程中调用 API 报错**

A：Bukkit 的大部分 API 都**不是线程安全**的。在异步线程中：

- ✅ 可以调用：`logger.*`, `economy.balance/format`, `scheduler.runAsync`
- ❌ 不可调用：`teleport`, `broadcast`, `msg`, `events.on`, `economy.give/take/set`

正确做法：异步中查询数据，主线程中修改游戏状态：

```java
api.runAsync(() -> {
    double balance = api.balance(player);
    api.run(() -> {
        api.giveMoney(player, 100);
        api.msg(player, "钱到账了");
    });
});
```

**Q: 通过 `sf.events().on()` 注册的监听器不生效**

A：检查：

1. 是否在 `onEnable()` 中注册（不要在 `onLoad()` 中）
2. 事件类是否正确导入（例如 `AsyncPlayerChatEvent` vs `PlayerChatEvent`）
3. 是否被其他插件取消（设置更高优先级 `EventPriority.HIGH`）
4. 控制台是否有异常日志

**Q: 编译报错找不到 `SFApi` 类**

A：Maven/Gradle 依赖配置问题。检查：

1. 是否添加了 JitPack 仓库
2. 依赖 scope 是否正确（`provided` 或 `compileOnly`）
3. 是否执行了 `mvn clean install` 刷新依赖

### 性能相关

**Q: 服务器 TPS 下降**

A：排查步骤：

1. 使用 `/tps` 查看当前 TPS
2. 检查是否有大量异步数据库操作（改为批量操作）
3. 检查 `sf.events().on()` 注册的监听器是否过多或过重
4. 切换到 MySQL 避免 SQLite 锁争用

**Q: 数据库查询慢**

A：

1. SQLite：启用 WAL 模式（默认已启用）
2. MySQL：确保 `homes(uuid, name)` 和 `warps(name)` 有索引（建表时已添加 PRIMARY KEY）
3. 避免在循环中频繁查询，用 `getHomes(uuid)` 一次获取所有

### 其他

**Q: 如何卸载插件而不丢失数据**

A：

1. 停止服务器
2. 备份 `plugins/ZeroCkate_SFServerPlugin/data.db`（SQLite）或导出 MySQL 数据库
3. 删除 jar 文件
4. 数据保留在备份中，下次安装时恢复即可

**Q: 多个服务器能共享家数据吗**

A：可以。所有服务器连同一个 MySQL 数据库，并使用相同的 `prefix`：

```yaml
database:
  mysql:
    enabled: true
    host: shared.db.example.com
    database: mc_network
    prefix: "sf_shared_"
```

如果想让数据相互独立，使用不同的 `prefix`。

**Q: 如何向作者反馈 bug**

A：在 [GitHub Issues](https://github.com/zmb96/ZeroCkate_ServerManagementPlugin/issues) 提交 issue，附上：

- SF 插件版本
- 服务器类型（Paper/Spigot）和版本
- 完整的错误日志（堆栈跟踪）
- 复现步骤

**Q: 可以商用吗**

A：SF 使用 MIT 协议，允许商用、修改、分发，但需保留版权声明。详见 [LICENSE](LICENSE)。

---

## 📝 变更日志

本项目版本变更记录遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

### [1.0.0] - 2026-08-02

首个正式版本发布！

#### ✨ 新增

**核心架构**
- 建立 `server.sf.model.api.v2` 包结构，分离 v1 主入口与 v2 API
- 实现门面模式 `SF` 类，统一对外暴露所有功能
- 通过 Bukkit `ServicesManager` 注册 `SFApi` 接口供第三方插件接入

**子模块（v2/main/）**
- `SFLogger`：分级日志（info/warn/error），支持格式化参数
- `SFScheduler`：同步/异步/延迟/定时任务调度
- `SFPlayerOps`：玩家查找（按名/按 UUID）
- `SFCommandOps`：命令与事件注册，支持链式调用
- `SFServerOps`：广播、消息发送

**经济系统（v2/economy/）**
- 双后端支持：EssentialsX 优先，Vault 回退
- `EconomyBackend` 接口抽象，`EssentialsBackend` / `VaultBackend` 独立实现
- `EconomyOps` 高级操作：余额检查、负数校验、转账自动回滚
- 修复 BigDecimal 精度问题
- 支持 `hasAccount`、`give`、`take`、`set`、`transfer`、`format` 完整 API

**事件系统（v2/event/）**
- 12 个分类文件，覆盖 120+ Bukkit 事件
- 通用 `on()` 方法支持任意自定义事件
- 支持指定优先级和忽略已取消事件
- 单监听器异常隔离，不影响其他监听器

**数据库基础（v2/database/）**
- `Database` 接口 + `SQLiteDatabase` / `MySQLDatabase` 双实现
- SQLite 启用 WAL 模式提升并发性能
- `DatabaseManager` 全局管理，自动建表
- 支持表名前缀，便于多服务器共享数据库

**传送系统（v2/feature/teleport/）**
- `TeleportManager` 核心：冷却 / 延迟 / 防移动取消 / 跨世界
- `/spawn` `/setspawn`：出生点管理
- `/home` `/sethome` `/delhome` `/homes`：个人家（数据库持久化）
- `/warp` `/setwarp` `/delwarp` `/warps`：公共传送点
- `/back`：返回上次位置
- `/tp` `/tphere`：管理员传送

**TPA 系统（v2/feature/tpa/）**
- `/tpa` `/tpahere` `/tpaccept` `/tpdeny` `/tpcancel`
- 请求超时自动清理（默认 60 秒，可配置）
- 互斥机制：同时只能有一个请求

**管理员工具（v2/feature/admin/）**
- `/gm` `/fly` `/heal` `/feed` `/god` `/vanish`
- `/ec` `/wb` `/clear` `/speed` `/suicide`
- `AdminStateManager` 管理状态持久化
- god/vanish 状态在重登后保持

#### 🛠️ 配置
- `plugin.yml`：28+ 命令注册，完整权限节点，别名支持
- `config.yml`：数据库配置 / 传送冷却 / 传送延迟 / TPA 超时
- 支持 SQLite / MySQL 一键切换
- 配置热重载（`/servermanagement reload`）

#### ⚙️ 技术规格
- **Java 版本**：21+
- **API 版本**：Bukkit 1.21.5
- **构建工具**：Maven 3.9+
- **依赖**：Paper API, Vault（可选）, EssentialsX（可选）

---

## 🤝 贡献指南

感谢你对 ZeroCkate ServerManagementPlugin 项目的兴趣！

### 环境要求

- JDK 21+
- Maven 3.9+
- Git
- IDE（推荐 IntelliJ IDEA）

### 本地开发

```bash
# 1. Fork 仓库并克隆
git clone https://github.com/你的用户名/ZeroCkate_ServerManagementPlugin.git
cd ZeroCkate_ServerManagementPlugin

# 2. 添加上游远程
git remote add upstream https://github.com/zmb96/ZeroCkate_ServerManagementPlugin.git

# 3. 构建项目
mvn clean package

# 4. 将 target/ 下的 jar 文件放入测试服务器 plugins/ 目录测试
```

### 报告 Bug

1. 在 [Issues](https://github.com/zmb96/ZeroCkate_ServerManagementPlugin/issues) 搜索是否已有相同问题
2. 如果没有，创建新 Issue，包含以下信息：
   - **环境**：服务器类型（Paper/Spigot）、版本、Java 版本
   - **插件版本**：可在 `/plugins` 中查看
   - **复现步骤**：详细步骤
   - **预期行为**：你期望发生什么
   - **实际行为**：实际发生了什么
   - **完整日志**：相关堆栈跟踪

### 提交代码

1. **Fork** 本仓库
2. 基于最新 `main` 分支创建特性分支：
   ```bash
   git checkout main
   git pull upstream main
   git checkout -b feature/你的特性名
   ```
3. 编写代码，遵循以下规范：
   - 不写注释（项目作者偏好）
   - 一个文件只负责一个小块功能
   - 使用包结构组织代码
   - 命令类实现 `CommandExecutor` 和 `TabCompleter`
   - 监听器类实现 `Listener`
4. 本地测试通过：`mvn clean package`
5. 在测试服务器中验证功能正常
6. 提交修改并推送
7. 在 GitHub 上创建 **Pull Request** 到 `main` 分支

### 代码规范

**包结构**

```
server.sf.model.api.v2/
├── SF.java              # 门面类
├── SFApi.java           # API 接口
├── database/            # 数据库相关
├── economy/             # 经济系统
├── event/               # 事件系统
├── main/                # 核心工具
└── feature/             # 功能模块
    ├── teleport/        # 传送系统
    ├── tpa/             # TPA 系统
    └── admin/           # 管理员工具
```

**命名约定**

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 类 | PascalCase | `TeleportManager` |
| 方法 | camelCase | `teleportNow()` |
| 常量 | UPPER_SNAKE | `DEFAULT_TIMEOUT` |
| 包 | 全小写 | `server.sf.model.api.v2.event` |
| 命令类 | XxxCommand | `HomeCommand` |
| 监听器 | XxxListener | `AdminListener` |
| 管理器 | XxxManager | `TpaManager` |

**Commit 规范**

| 前缀 | 用途 |
|------|------|
| `feat:` | 新功能 |
| `fix:` | Bug 修复 |
| `docs:` | 文档变更 |
| `refactor:` | 重构（不影响功能） |
| `perf:` | 性能优化 |
| `chore:` | 构建/工具变更 |

### 手动测试清单

提交 PR 前，请确保以下功能正常：

- [ ] `mvn clean package` 构建成功
- [ ] 插件能在 Paper 1.21.5 启动
- [ ] 新功能在游戏内测试通过
- [ ] 没有引入新的异常日志
- [ ] `/servermanagement reload` 仍然可用
- [ ] 卸载插件不报错

### 维护者

- **zmb96** - 项目创建者与主要维护者

---

## 📄 License

MIT License - 详见 [LICENSE](LICENSE)
