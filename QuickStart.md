# 快速开始

本指南将带你在 5 分钟内完成 SF 插件的基础配置。

## 1. 设置出生点

作为 OP，站在你想要的位置执行：

```
/setspawn
```

之后玩家可以用 `/spawn` 回到这个位置。

## 2. 创建公共传送点

站在你想让玩家传送到的位置：

```
/setwarp shop
/setwarp pvp
/setwarp spawn
```

玩家输入 `/warp shop` 即可传送到商店。

查看所有传送点：

```
/warps
```

## 3. 设置个人家

玩家可以设置自己的家：

```
/sethome home       # 设置名为 "home" 的家
/sethome farm       # 设置名为 "farm" 的家
/home home          # 传送到 "home"
/homes              # 查看所有家
/delhome farm       # 删除 "farm"
```

## 4. TPA 请求传送

玩家之间互相传送：

```
玩家A: /tpa 玩家B      # 请求传送到 玩家B
玩家B: /tpaccept       # 接受
玩家B: /tpdeny         # 或拒绝

玩家A: /tpahere 玩家B  # 邀请 玩家B 过来
玩家B: /tpaccept       # 接受
```

请求默认 60 秒超时，可在 `config.yml` 修改。

## 5. 使用管理员工具

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

## 6. 返回上次位置

每次传送前都会自动记录位置，可以用 `/back` 返回：

```
/spawn               # 传送到出生点
/back                # 返回刚才的位置
```

## 7. 配置冷却和延迟

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

重载配置：

```
/servermanagement reload
```

## 8. 权限设置

使用 LuckPerms 或类似插件分配权限：

```
# 给所有玩家基础传送权限（默认就有）
lp group default permission set sf.teleport.bypass false

# 给 VIP 跳过冷却权限
lp group vip permission set sf.teleport.bypass true

# 给管理员所有权限
lp group admin permission set sf.admin.* true
```

## 下一步

- [命令参考](Commands) - 完整命令列表
- [权限列表](Permissions) - 所有权限节点
- [配置文件](Configuration) - 高级配置
