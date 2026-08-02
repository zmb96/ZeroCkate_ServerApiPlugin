# ZeroCkate SFServerPlugin

> 一款现代化、模块化的 Minecraft 服务器管理插件，提供完整的管理员工具、传送系统、经济系统，并对外暴露简洁的 API 供其他插件调用。

![Java](https://img.shields.io/badge/Java-21-orange)
![Bukkit](https://img.shields.io/badge/Bukkit-1.21.5-green)
![License](https://img.shields.io/badge/License-MIT-blue)

## ✨ 特性

- 🚀 **极简 API**：一行代码完成日志、经济、传送、调度等操作
- 🎯 **28+ 内置命令**：传送、家、传送点、TPA、管理员工具一应俱全
- 💰 **双后端经济**：自动检测 EssentialsX / Vault，无需手动配置
- 🗄️ **持久化存储**：内置 SQLite / MySQL 切换，零配置开箱即用
- 🔔 **完整事件系统**：120+ Bukkit 事件分类封装，链式调用
- 🛡️ **管理员工具**：无敌、隐身、飞行、治疗、清包等 12 个常用命令
- 🔌 **第三方接入**：通过 Bukkit ServicesManager 暴露 `SFApi` 接口
- ⚡ **异步安全**：经济操作自动回滚、传送防移动取消

## 📦 安装

只需 3 步：

1. 下载最新 [Releases](https://github.com/yourname/ZeroCkate_SFServerPlugin/releases) 的 `.jar` 文件
2. 放入服务器的 `plugins/` 目录
3. 启动服务器（可选：安装 [Vault](https://www.spigotmc.org/resources/vault.34315/) 和 [EssentialsX](https://essentialsx.net/) 以启用经济系统）

详见 👉 [安装指南](Installation)

## 🎮 命令速览

| 类别 | 命令 |
|------|------|
| 传送 | `/spawn` `/home` `/warp` `/back` `/tpa` `/tp` |
| 管理 | `/gm` `/fly` `/god` `/vanish` `/heal` `/feed` `/clear` `/speed` |
| 工具 | `/ec` `/wb` `/suicide` |

完整命令列表见 👉 [命令参考](Commands)

## 💻 开发者

如果你想在自己的插件中调用 SF 的功能：

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        if (SFApi.isAvailable()) {
            SFApi api = SFApi.get();
            api.info("已接入 SF API！");
            api.giveMoney(player, 100);
            api.broadcast("欢迎！");
        }
    }
}
```

详见 👉 [API 接入示例](API-Examples) | [API 接口文档](API-Reference)

## 📚 文档目录

- [安装指南](Installation)
- [快速开始](QuickStart)
- [命令参考](Commands)
- [权限列表](Permissions)
- [配置文件](Configuration)
- [API 接口文档](API-Reference)
- [API 接入示例](API-Examples)
- [常见问题](FAQ)

## 📄 License

MIT License - 详见 [LICENSE](https://github.com/yourname/ZeroCkate_SFServerPlugin/blob/main/LICENSE)
