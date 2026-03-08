# FGA API 中的 storeId 说明

## storeId 是什么

**storeId（存储空间ID）** 是 FGA 权限模型的**顶层隔离维度**，表示“在哪一个存储空间下做权限操作”。  
一个 Store 内包含：该空间下的授权模型、关系元组、变更日志等，不同 Store 之间的数据彼此隔离。

可以类比为：
- 数据库里的 **Schema** 或 **库名**
- 多租户里的 **租户ID**
- 多应用里的 **应用/项目标识**

## storeId 从哪里来（技术来源）

在所有 FGA 接口里，**storeId 都来自请求的 URL 路径**，由**调用方（前端、网关、其他服务）在请求时传入**，例如：

- `POST /fga/stores/{storeId}/check`  
- `POST /fga/stores/{storeId}/write`  
- `GET /fga/stores/{storeId}`  
- …

也就是说：**服务端不会自动生成或从登录态里取 storeId**，必须由调用方在 URL 里带上。

## 调用方如何获得 storeId（业务来源）

常见几种方式：

1. **先创建再使用**  
   调用 **POST /fga/stores** 创建存储空间，响应体里会返回 `storeId`（如 `FgaStoreVO.storeId`），后续所有 Check/Write/Read 等请求都用这个值填到路径里的 `{storeId}`。

2. **从已有列表里选**  
   调用 **GET /fga/stores** 列出当前可用的存储空间，从返回的列表中取需要的 `storeId`，再用于后续请求。

3. **和业务/租户约定**  
   若你们约定“一个租户一个 Store”或“一个项目一个 Store”，则：
   - 登录/鉴权后得到的 **租户ID** 或 **项目ID** 可以直接当作 storeId 使用，或  
   - 在配置/网关里根据租户或项目映射到一个固定 storeId。  
   此时 storeId 仍由调用方按约定拼到 URL 里，只是取值来自你们的业务规则。

4. **固定单 Store**  
   若系统只有一个存储空间，可以在前端或网关配置一个固定的 storeId，所有 FGA 请求都使用同一个路径即可。

## 小结

| 问题           | 答案                                                                 |
|----------------|----------------------------------------------------------------------|
| storeId 从哪来？ | **从 URL 路径传入**，由调用方在请求时提供。                          |
| 谁提供？         | **调用方**（前端、网关、或其他调用 FGA 接口的服务）。                 |
| 怎么获取？       | 创建 Store（POST /fga/stores）、列出 Store（GET /fga/stores），或按业务约定（如租户ID）使用。 |

因此：**所有带 storeId 的 FGA API 都要求调用方先知道要操作哪个 Store，并把对应的 storeId 填到 URL 的 `{storeId}` 位置。**
