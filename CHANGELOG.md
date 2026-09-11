# 更新日志

版本按功能累加。**1.0.2**（含音乐）在 `main` 分支；不含音乐的 **1.0.1** 保留在 `no-music` 分支。

## 1.0.2 — 增加音乐功能

在原有 CraftEngine 显示功能之上，合并 **AllMusic 客户端**，构建为**同一个 jar**。

- **音乐播放 + HUD**：游戏内播放服务端下发的歌曲，显示歌曲信息、歌词、封面等 HUD。
- **统一 Mojang 官方映射**：AllMusic 客户端源码原生使用 Mojang 映射，因此整个客户端改为 Mojang 映射编译；Rainveil 侧的 22 个客户端源文件同步从 Yarn 移植到 Mojang 命名（AllMusic 源码未改动）。
- **打包网络库**：把 Apache HttpComponents 5（`httpclient5`/`httpcore5`）打进 jar，供 AllMusic 拉取音频使用。
- **配方/物品显示功能保持不变**（即 1.0.0 + 1.0.1 全部功能仍然可用）。

> **音乐功能需要服务端配套**，只有客户端不会出歌：
> 1. 服务器需安装**配套版本的 AllMusic 服务端**（Paper/Folia/Velocity 或 Forge/NeoForge/Fabric）；
> 2. 服务器需配置音乐 API（例如 [netapi](https://github.com/Coloryr/netapi)，放进 `allmusic/api` 后重启）；
> 3. 客户端 `mods` 需另装 **ModernUI**（AllMusic 的 HUD 渲染依赖，未打进本 jar）。

## 1.0.1 — 让 Jade（玉）显示 CraftEngine 物品

- **方块图标**：Jade 拾取 CraftEngine 自定义方块时，用真实的客户端物品图标替换伪装方块（音符盒、绊线等）的图标。
- **方块名称**：用 CraftEngine 的真实显示名替换 Jade 原本显示的伪装方块名。
- **家具图标**：Jade 拾取 CraftEngine 家具（实体）时，按需向服务端查询真实图标并显示。
- **CraftEngine 身份标识**：方块 tooltip 中可显示对应的 CraftEngine id。

## 1.0.0 — 让 JEI 显示 CraftEngine 物品

- **物品列表**：CraftEngine 自定义物品（重绘皮肤、`item_model`、自定义名称）在 JEI 物品列表里显示真正的皮肤，不再退回原版材质。
- **配方精确显示**：合成台配方按每个格子的**精确外观**展示，产物/原料不再显示成"任意一个同类型物品"；锻造台使用自定义 JEI 配方类别展示精确的模板/基底/添加物/产物。
- **配方同步**：本 Minecraft 版本的服务端不再向客户端下发原版配方封包，由服务端插件重建并经 Fabric `recipe_sync` 重发，保证 JEI 配方书可用。

---

## 版本与分支对应

| 版本 | 功能 | 分支 |
| --- | --- | --- |
| 1.0.2 | JEI 显示 + Jade 显示 + 音乐 | `main` |
| 1.0.1 | JEI 显示 + Jade 显示 | `no-music` |
| 1.0.0 | JEI 显示 | 历史版本 |
