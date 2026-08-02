# 权限列表

本文档列出 SF 插件使用的所有权限节点。

## 默认权限

以下权限默认所有玩家都拥有（无需手动赋予）：

| 权限 | 说明 |
|------|------|
| `servermanagement.use` | 使用 `/servermanagement` 命令 |

## 传送权限

| 权限 | 说明 | 默认 |
|------|------|------|
| `sf.spawn.set` | 设置出生点 `/setspawn` | OP |
| `sf.warp.set` | 设置/删除公共传送点 | OP |
| `sf.teleport.bypass` | 跳过传送冷却 | OP |
| `sf.admin.tp` | 管理员传送 `/tp` `/tphere` | OP |

> 💡 普通的 `/spawn` `/home` `/warp` `/tpa` 等命令**所有玩家默认可用**。

## 管理员权限

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

## 系统权限

| 权限 | 说明 | 默认 |
|------|------|------|
| `servermanagement.reload` | 重载配置文件 | OP |

## 推荐权限分配

### 使用 LuckPerms 分配

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

### 给玩家管理员权限

```bash
lp user 玩家名 permission set sf.admin.* true
```

## 通配符权限

SF 支持以下通配符（需权限插件支持，如 LuckPerms）：

| 通配符 | 包含 |
|--------|------|
| `sf.admin.*` | 所有 `sf.admin.xxx` 权限 |
| `sf.*` | 所有 `sf.xxx` 权限 |
| `sf.spawn.*` | 出生点相关权限 |
| `sf.warp.*` | 传送点相关权限 |
| `sf.teleport.*` | 传送相关权限 |
