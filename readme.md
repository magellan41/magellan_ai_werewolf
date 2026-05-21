# AiWerewolf（AI 狼人杀）

基于 Spring Boot + 阿里云 DashScope 大语言模型的 AI 驱动狼人杀游戏系统。每个游戏玩家由一个独立的 AI Agent 扮演，支持 AI vs AI 全自动对战，也支持人类玩家通过 Web 前端实时参与。

## 目录

- [特性](#特性)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [游戏规则与流程](#游戏规则与流程)
- [运行方式](#运行方式)
- [API 接口](#api-接口)
- [架构设计](#架构设计)
- [配置说明](#配置说明)
- [日志输出](#日志输出)

---

## 特性

- **7 人标准狼人杀屠边局**：2 狼人 + 1 预言家 + 1 女巫 + 3 平民
- **独立 AI Agent**：每个玩家拥有独立的 DashScope Agent 和系统提示词，具备独立"人格"和对话记忆
- **完整游戏流程**：狼人行动 → 女巫行动 → 预言家查验 → 白天发言 → 投票 → 决斗 → 胜负判定
- **双运行模式**：
  - **控制台模式**（`GameConsole`）：纯命令行交互，支持人类通过 Scanner 参与(自动开启天眼)
  - **Web SSE 模式**（`GameSse`）：通过 SSE 实时推送游戏消息，前端可展示完整游戏过程
- **人类玩家支持**：人类玩家可通过自然语言发言，AI 作为"格式转换助手"将自然语言转为结构化 JSON
- **JSON 结构化输出**：各阶段 AI 输出均为结构化 JSON，便于程序解析和前端展示
- **私信系统**：玩家可在发言阶段互相发送私信

---

## 技术栈

| 技术               | 版本       | 说明                       |
|--------------------|------------|----------------------------|
| Java               | 17+        | 开发语言                   |
| Spring Boot        | 3.5.14     | Web 应用框架               |
| Spring AI Alibaba  | 1.1.2.0    | DashScope 大模型集成与 Agent 框架 |
| Maven              | —          | 项目构建                   |
| Lombok             | —          | 简化代码                   |
| Jackson            | (内置)     | JSON 序列化                 |

**外部服务**：阿里云 DashScope API（需要 API Key），请设置环境变量DASHSCOPE_API_KEY为<你的API_KEY>

---

## 项目结构

```
AiWerewolf/
├── pom.xml                              # Maven 配置
├── readme.md
├── mvnw / mvnw.cmd                       # Maven Wrapper
├── src/
│   ├── main/java/xyz/syyrjx/aiwerewolf/
│   │   ├── AiWerewolfApplication.java   # Spring Boot 启动类
│   │   ├── ServletInitializer.java      # WAR 部署初始化器
│   │   ├── GameConsole.java             # 控制台版游戏主逻辑
│   │   ├── Test.java                    # 简易输入测试工具
│   │   │
│   │   ├── agent/
│   │   │   └── DashScopeAgent.java      # DashScope AI Agent 封装
│   │   │
│   │   ├── controller/
│   │   │   ├── SseController.java       # SSE 推送端点
│   │   │   └── UserTextController.java  # 用户输入端点
│   │   │
│   │   ├── entity/
│   │   │   ├── GameMessage.java         # SSE 推送的游戏消息
│   │   │   ├── MagellanResult.java      # 通用返回结果封装
│   │   │   └── UserText.java            # 用户输入实体
│   │   │
│   │   ├── game/
│   │   │   └── GameSse.java             # Web SSE 版游戏主逻辑
│   │   │
│   │   ├── message/                     # 各阶段结构化消息模型
│   │   │   ├── BattleMessage.java       # 决斗阶段消息
│   │   │   ├── PrivateMessage.java      # 私信消息
│   │   │   ├── RawPrivateMessage.java   # 原始私信消息
│   │   │   ├── SeerMessage.java         # 预言家查验消息
│   │   │   ├── SpeechMessage.java       # 发言阶段消息
│   │   │   ├── VoteMessage.java         # 投票消息
│   │   │   ├── WitchMessage.java        # 女巫行动消息
│   │   │   └── WolfMessage.java         # 狼人行动消息
│   │   │
│   │   ├── player/                      # 玩家类（继承体系）
│   │   │   ├── Player.java              # 玩家基类
│   │   │   ├── Wolf.java                # 狼人
│   │   │   ├── Seer.java                # 预言家
│   │   │   ├── Witch.java               # 女巫
│   │   │   └── Commoner.java            # 平民
│   │   │
│   │   └── utils/
│   │       ├── GameUtil.java            # 工具类（JSON 解析、胜负判断）
│   │       ├── JsonPrompt.java          # 各阶段 JSON 输出格式定义
│   │       ├── SseUtil.java             # SSE 连接管理与消息推送
│   │       ├── SystemPrompt.java        # 系统提示词（当前使用版本）
│   │       └── SystemPrompt1.java       # 系统提示词（老版本）
│   │
│   ├── main/resources/
│   │   ├── application.properties      # 应用配置
│   │   ├── page/
│   │   └── page.html                   # 前端界面
│   └── test/java/xyz/syyrjx/aiwerewolf/
│       └── AiWerewolfApplicationTests.java
```

---

## 游戏规则与流程

### 角色配置

| 角色   | 人数 | 阵营 | 技能                       |
|--------|------|------|----------------------------|
| 狼人   | 2    | 狼人 | 每晚击杀一名玩家（第一夜不可空刀） |
| 预言家 | 1    | 好人 | 每晚查验一名玩家身份       |
| 女巫   | 1    | 好人 | 拥有一瓶解药和一瓶毒药     |
| 平民   | 3    | 好人 | 无特殊技能                 |

### 每日流程

```
黑夜阶段
  ├── 1. 狼人阶段：两狼协商后选择击杀目标（第一夜不可空刀）
  ├── 2. 女巫阶段：选择使用解药(0) / 毒药(1) / 不用药(-1)
  └── 3. 预言家阶段：查验一名玩家身份，系统告知好人/狼人

黎明阶段（胜负判断）
  ├── 狼人全灭 → 好人胜利
  └── 平民或神职全灭 → 狼人胜利

白天阶段
  ├── 4. 发言阶段：存活玩家依次发言，可私信其他玩家
  ├── 5. 投票阶段：投票选出嫌疑人
  ├── 6. 决斗阶段：平票时进入，双方发言后再次投票，直至分出胜负
  └── 7. 最终胜负判断
```

---

## 运行方式

### 前置条件

1. **JDK 17+**
2. **Maven**（或使用项目自带的 `mvnw`）
3. **阿里云 DashScope API Key**：设置环境变量 `DASHSCOPE_API_KEY`

```bash
# Windows (PowerShell)
$env:DASHSCOPE_API_KEY="your_api_key_here"

# Linux / macOS
export DASHSCOPE_API_KEY="your_api_key_here"
```

### 构建

```bash
# Windows
mvnw.cmd clean package -DskipTests

# Linux / macOS
./mvnw clean package -DskipTests
```

### 启动

#### 方式一：Web SSE 模式（推荐）

```bash
java -jar target/AiWerewolf-0.0.1-SNAPSHOT.war
```

应用启动后默认监听 `8080` 端口，前端通过以下接口交互：

1. **建立 SSE 连接**：`GET /sse/stream?userId=xxx`
2. **启动游戏**：`POST /user/start`
3. **提交发言**：`POST /user/text`

#### 方式二：控制台模式

直接运行 `GameConsole` 类的 `main` 方法（可在 IDE 中运行），通过控制台 Scanner 进行交互。

#### 方式三：部署到外部 Tomcat

项目支持 WAR 部署，通过 `ServletInitializer` 适配外部 Servlet 容器。

---

## API 接口

### SSE 实时推送

| 方法 | 路径                | 说明                                    |
|------|---------------------|-----------------------------------------|
| GET  | `/sse/stream?userId={userId}` | 建立 SSE 长连接，接收游戏实时消息 |

**消息格式**（`GameMessage`）：

```json
{
  "id": 3,
  "type": "gamer|system",
  "text": "消息内容"
}
```

- `type = "gamer"`：玩家相关的游戏消息（发言、投票等）
- `type = "system"`：系统消息（游戏阶段、胜负结果等）

### 用户交互

| 方法 | 路径          | 说明                       |
|------|---------------|----------------------------|
| POST | `/user/start` | 启动一局新游戏             |
| POST | `/user/text`  | 提交人类玩家发言            |

**`/user/text` 请求体**：

```json
{
  "userId": "user001",
  "text": "我觉得3号玩家很可疑..."
}
```

**返回格式**（`MagellanResult`）：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 架构设计

```
┌──────────┐     SSE 推送      ┌──────────────────┐
│  Web 前端 │◄─────────────────│  SseController    │
│          │──────────────────►│  UserTextController│
└──────────┘   HTTP POST       └────────┬─────────┘
                                        │ 创建并启动
                                        ▼
                                  ┌───────────┐
                                  │  GameSse   │ (Runnable, 独立线程)
                                  │  游戏主循环  │
                                  └─────┬─────┘
                        ┌───────────────┼───────────────┐
                        ▼               ▼               ▼
                  ┌──────────┐   ┌──────────┐   ┌──────────┐
                  │ Player(0)│   │ Player(1)│...│ Player(6)│
                  │  (Wolf)  │   │ (Seer)   │   │(Commoner)│
                  └────┬─────┘   └────┬─────┘   └────┬─────┘
                       │              │              │
                       ▼              ▼              ▼
                  ┌────────────────────────────────────────┐
                  │          DashScopeAgent                 │
                  │  (ReactAgent + DashScopeChatModel)       │
                  │         → 阿里云 DashScope API            │
                  └────────────────────────────────────────┘
```

### 核心设计

- **SSE + CompletableFuture 阻塞/唤醒机制**：游戏主循环通过 `SseUtil.get()` 阻塞等待人类玩家输入，前端提交发言后通过 `CompletableFuture.complete()` 唤醒线程继续执行。
- **AI Agent 独立性**：每个 `Player` 独立持有 `DashScopeAgent` 实例，拥有独立的对话记忆，实现角色个性化决策。
- **人类玩家模式**：当某位玩家为人类时，其 AI Agent 切换为"格式转换助手"角色，仅负责将人类的自然语言发言转换为结构化 JSON，不代做决策。

---

## 配置说明

### 应用配置

`application.properties`（当前仅一项）：

```properties
spring.application.name=AiWerewolf
```

### 环境变量

| 环境变量            | 必填 | 说明                           |
|---------------------|------|--------------------------------|
| `DASHSCOPE_API_KEY` | 是   | 阿里云 DashScope API 密钥      |

### 硬编码配置（可优化为外部配置）

| 配置项           | 位置                        | 当前值                                     |
|------------------|-----------------------------|--------------------------------------------|
| AI 模型名称      | `GameSse` / `GameConsole`   | `qwen3-max` / `deepseek-v4-pro`           |
| 日志输出路径     | `GameSse` / `GameConsole`   | `D:\temp\drama.txt`           |
| 角色与人数配置   | `GameSse` / `GameConsole`   | 7 人固定局（2狼1预言家1女巫3平民）          |

---

## 日志输出

游戏完整对话日志输出至：`D:\temp\drama.txt`

日志包含每轮各玩家的 AI 响应原文，用于复盘分析游戏策略。

---

## 设计亮点

- **玩家独立人格**：每个 AI 玩家拥有独立的系统提示词和对话记忆，模拟真实玩家的差异化行为
- **人类无缝接入**：人类玩家可用自然语言参与，AI 自动完成格式转换
- **同步阻塞模型**：基于 `CompletableFuture` 实现的等待/唤醒机制，使异步 Web 请求能驱动同步游戏流程
- **结构化输出**：所有 AI 决策输出为严格 JSON 格式，便于程序解析和前端渲染

## 潜在改进方向

- 将硬编码的模型名称、日志路径、角色配置提取为 `application.properties` 配置项
- 支持自定义角色组合与玩家人数
- 提取 `GameConsole` 与 `GameSse` 的公共逻辑，减少代码重复
- `SseUtil` 中的 `CompletableFuture` 增加超时和异常兜底，防止内存泄漏
