# 配置文件

本文档详细说明 `config.yml` 的所有配置项。

## 完整配置示例

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

## 配置项详解

### `database` - 数据库配置

#### `database.mysql.enabled`

是否启用 MySQL。设为 `false` 则使用 SQLite。

```yaml
database:
  mysql:
    enabled: true   # 切换到 MySQL
```

#### `database.mysql.host` / `port` / `database` / `user` / `password`

MySQL 连接信息。`database` 是数据库名（需要预先创建）。

```yaml
database:
  mysql:
    host: 127.0.0.1
    port: 3306
    database: minecraft
    user: mc_admin
    password: "strong_password"
```

#### `database.mysql.prefix`

表名前缀。多服务器共用同一个数据库时有用：

```yaml
database:
  mysql:
    prefix: "sf_survival_"   # 表名会变成 sf_survival_homes 等
```

#### `database.sqlite.file`

SQLite 数据库文件名。文件位于 `plugins/ZeroCkate_SFServerPlugin/` 目录下。

```yaml
database:
  sqlite:
    file: data.db
```

---

### `teleport` - 传送系统

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

```yaml
teleport:
  tpa:
    timeout: 60   # 60 秒未响应则失效
```

---

## 修改配置后

修改 `config.yml` 后，执行以下命令热重载（无需重启服务器）：

```
/servermanagement reload
```

或简写：

```
/sm reload
```

## 常见配置场景

### 场景 1：关闭所有冷却（休闲服）

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

### 场景 2：长冷却防滥用（生存服）

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

### 场景 3：多服务器共享数据库

服务器 A（生存服）：

```yaml
database:
  mysql:
    enabled: true
    host: db.example.com
    port: 3306
    database: mc_network
    user: mc_user
    password: "xxx"
    prefix: "sf_survival_"
```

服务器 B（小游戏服）：

```yaml
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

这样两个服务器的家/传送点数据相互独立，但共用同一个数据库。

### 场景 4：纯立即传送（无延迟）

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
