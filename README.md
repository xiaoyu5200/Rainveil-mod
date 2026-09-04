# Rainveil-mod

给服务器自己用的 CraftEngine 桥接插件 + 客户端模组组合。
作用是让 [CraftEngine](https://github.com/Xiao-MoMi/CraftEngine) 的自定义物品、方块、家具和配方
在客户端的 **JEI / Jade** 里显示成**真实外观**，而不是原版材质或"任意同类型物品"。

## 功能

- **物品显示**：CraftEngine 自定义物品（重绘皮肤、`item_model`、自定义名称）在 JEI 物品列表和配方里显示真正的皮肤，不再退回原版材质。
- **配方正确显示**：合成台、锻造台等配方格子里显示具体的产物/原料，而不是"任意一个同类型物品"。
- **方块/家具图标**：Jade 拾取 CraftEngine 方块和家具时，显示真实图标和 CraftEngine 名称，不残留"音符盒（东）"这类伪装方块信息。
- **配方同步**：本版本服务端不再向客户端发原版配方封包，由插件自己重建并同步，保证 JEI 配方书能用。

## 组成

| 目录 | 说明 |
| --- | --- |
| `server/` | Paper 插件 `CraftEngineClientBridge`：读取 CraftEngine 物品/配方数据，还原精确外观并通过插件消息发给客户端，同时重建配方同步。 |
| `client/` | Fabric 模组 `CraftEngineClientMod`（Minecraft 1.21.11）：接收数据，喂给 JEI 物品列表/配方分类，并给 Jade 提供真实图标。 |
| `protocol/` | 服务端与客户端共用的协议库。 |

## 版本支持

- **服务端**：Paper/ASPaper，Minecraft **1.21.11**，服务器需安装并加载匹配版本的 CraftEngine。
- **客户端**：Minecraft 1.21.11，Fabric Loader 0.19.3 + Fabric API 0.141.6+1.21.11，JEI 27.22.0.66，Jade 19.0.3。
- JEI / Jade 均可选：装哪个就对哪个生效，都不装也能正常启动。

## 构建

```powershell
# 服务端插件（1.21.11）
cd server
./gradlew clean shadowJar -Ptarget=1.21.11

# 客户端模组
cd client
./gradlew clean build
```

产物：`server/build/libs/CraftEngineClientBridge-*.jar`（放服务器 `plugins/`）、
`client/build/libs/ceclientmod-1.21.11-*.jar`（放 `.minecraft/mods/`）。

## 已知限制

- 锻造台的盔甲纹饰配方（trim）不在精确展示范围内（产物是运行时动态合成的）。
- JEI 的精确展示配方在连接时一次性注册；服务端热重载后新增/修改的配方要玩家重新连接才会刷新。
- 只提供 1.21.11 一个目标，其他 1.21.x 补丁版本不承诺兼容。

## 许可证

[MIT](LICENSE)。CraftEngine 及仓库中的社区版 CraftEngine 开发依赖是独立第三方软件，不受本项目 MIT 许可覆盖；构建依赖仅用于 `compileOnly`，运行服务器仍需自行提供合适版本的 CraftEngine。
