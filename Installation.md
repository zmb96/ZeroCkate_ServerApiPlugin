# 安装指南

## 环境要求

| 组件 | 版本 |
|------|------|
| Minecraft Server | 1.21.5+ |
| Java | 21+ |
| Bukkit/Paper API | 1.21.5 |

**支持的服务端**：Paper、Purpur、Folia（实验性）、Spigot、CraftBukkit

## 可选依赖

以下插件非必需，但推荐安装以解锁更多功能：

| 插件 | 用途 | 下载 |
|------|------|------|
| EssentialsX | 经济系统后端（优先） | [essentialsx.net](https://essentialsx.net/) |
| Vault | 经济系统后端（回退） | [spigotmc.org](https://www.spigotmc.org/resources/vault.34315/) |

> 💡 如果两个都安装，会优先使用 EssentialsX；若都未安装，经济相关功能会自动禁用，但不影响其他功能。

## 安装步骤

### 1. 下载插件

从 [Releases 页面](https://github.com/yourname/ZeroCkate_SFServerPlugin/releases) 下载最新版本的 `ZeroCkate_SFServerPlugin-x.x.x.jar`。

### 2. 放入插件目录

将 jar 文件放入服务器的 `plugins/` 目录：

```
你的服务器/
├── plugins/
│   └── ZeroCkate_SFServerPlugin-x.x.x.jar   ← 放这里
├── server.jar
└── ...
```

### 3. 启动服务器

首次启动会自动生成以下文件：

```
plugins/ZeroCkate_SFServerPlugin/
├── config.yml          ← 主配置文件
├── data.db             ← SQLite 数据库（默认）
└── help.txt            ← /sh 命令的帮助文本
```

### 4. 验证安装

在控制台或游戏内执行：

```
/servermanagement
```

看到帮助信息即表示安装成功。

查看经济系统状态：

```
# 在控制台日志中应看到类似输出：
[INFO] Database ready: true
[INFO] Economy ready: true (Essentials=true, Vault=false)
[INFO] 插件已加载
```

## 升级

1. **停止服务器**
2. 备份 `plugins/ZeroCkate_SFServerPlugin/` 目录（特别是 `data.db`）
3. 替换为新版本的 jar 文件
4. 启动服务器

> ⚠️ 升级前务必备份！数据库结构可能随版本变化。

## 卸载

1. 停止服务器
2. 执行 `/servermanagement` 确认插件状态
3. 删除 `plugins/ZeroCkate_SFServerPlugin.jar`
4. （可选）删除 `plugins/ZeroCkate_SFServerPlugin/` 目录以清除所有数据

## 切换到 MySQL

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

## 下一步

- [快速开始](QuickStart) - 配置常用功能
- [命令参考](Commands) - 查看所有命令
- [配置文件](Configuration) - 详细配置说明
