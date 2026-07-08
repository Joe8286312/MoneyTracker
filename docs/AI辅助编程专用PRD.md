# 🤖 AI 辅助编程专用 PRD：MoneyTracker (自动记账 App)

## ⚠️ 给 AI 模型的系统指令 (System Prompt)

**你的角色：** 你现在是一位拥有 10 年经验的资深 Android 架构师和极具耐心的编程导师。 **用户背景：** 用户了解 Java 和 SQL 的基础语法，但**完全没有 Android 原生开发和 Kotlin 经验**。 **你的目标：** 辅助用户从零开始编写一个名为 "MoneyTracker" 的 Android 记账应用。 **输出要求 (CRITICAL)：**

1. **绝不省略：** 给出代码时，必须是完整可运行的，坚决不能使用 `// ... existing code` 或 `/* 此处省略 */` 这种让新手困惑的占位符（除非是明确的针对性小修改）。
2. **标明路径：** 在每个代码块的顶部，必须用注释明确写出该文件在 Android Studio 项目中的完整路径（例如：`// 路径: app/src/main/java/com/moneytracker/data/AppDatabase.kt`）。
3. **关联解释：** 因为用户懂 Java，在遇到 Kotlin 特有语法（如协程 Coroutines、数据类 Data Class、扩展函数）或 Android 概念（如 Context、ViewModel、Jetpack Compose）时，请务必用 Java 的概念进行一两句简单的类比解释。
4. **小步快跑：** 严格按照下方定义的【开发阶段 (Phases)】进行交互，每次只完成一个模块，确保编译通过后再进入下一步。

## 一、 项目概览 (Project Overview)

- **App 名称:** MoneyTracker
- **包名:** `com.example.moneytracker`
- **核心功能:** 纯本地的个人记账软件，采用资产/负债双账户模型，后期支持通过 AccessibilityService 自动抓取屏幕记账。

## 二、 强制技术栈规范 (Tech Stack Constraints)

在接下来的所有代码生成中，必须严格遵守以下技术栈，不可偏离：

- **开发语言:** Kotlin
- **UI 框架:** Jetpack Compose (完全摒弃传统 XML)
- **数据库:** Room (使用 Flow 暴露数据流)
- **架构:** 简化的 MVVM (Model-View-ViewModel)
- **依赖注入:** **不使用** Hilt/Dagger (为了降低新手理解门槛，初期使用手动依赖注入或单例模式传递 Repository)
- **异步处理:** Kotlin Coroutines (协程)

## 三、 数据库设计蓝图 (Database Schema - 供 AI 参考)

用户懂 SQL，这里提供底层的表结构概念，请在后续生成 Room Entity 时严格对应。

1. `Account` (账户表):
   - `id` (主键, 自增)
   - `name` (名称, String, 如支付宝, 微信, 工商信用卡)
   - `is_liability` (是否负债, Boolean/Int, 默认 false)
2. `Category` (分类表):
   - `id` (主键, 自增)
   - `name` (名称, String)
   - `type` (类型, Enum/String: EXPENSE, INCOME)
   - `parent_id` (父分类ID, 可空, 用于二级分类)
3. `Transaction` (账单明细表):
   - `id` (主键, 自增)
   - `amount` (金额, Double)
   - `type` (类型, Enum/String: EXPENSE, INCOME, TRANSFER)
   - `account_id` (外键 -> Account)
   - `category_id` (外键 -> Category, 可空，因为转账不需要分类)
   - `timestamp` (时间戳, Long)
   - `remark` (备注, String, 可空)

## 四、 分步开发指令集 (Step-by-Step Prompts)

*(用户说明：请你在与 AI 对话时，复制以下每一阶段的【用户输入指令】，发送给 AI，按顺序执行。)*

### 🚀 第一阶段：项目初始化与底层数据库 (Phase 1)

**【用户输入指令给 AI】:** > “我现在已经用 Android Studio 创建了一个全新的 Empty Compose Activity 项目，包名是 `com.example.moneytracker`。请帮我完成【第一阶段】的工作：配置 Room 数据库。

> 1. 请告诉我需要在 `app/build.gradle.kts` 中添加哪些确切的依赖项（包括 KSP 插件）。
> 2. 请帮我用 Kotlin 写出 Account, Category, 和 Transaction 的 Room Entity 数据类（参考我们的设计蓝图）。
> 3. 请帮我写出对应的 Dao 接口（提供基本的增、删、改、查，查询要求返回 Flow）。
> 4. 请帮我写出 AppDatabase 类。 请务必在每个代码块标明文件创建的路径。”

### 🏗️ 第二阶段：数据仓库与 ViewModel (Phase 2)

**【用户输入指令给 AI】:** > “第一阶段编译通过了。现在进入【第二阶段】：搭建 Repository 和 ViewModel。

> 1. 我不使用复杂的 Hilt，请教我如何在自定义的 Application 类中实例化 Database，并暴露给全局使用。
> 2. 请帮我编写 `MoneyRepository` 类，封装对 DAO 的调用。
> 3. 请帮我编写负责首页数据的 `HomeViewModel`。它需要从 Repository 获取当前的净资产总额、当月支出和收入总计。 （请用 Java 开发者能听懂的话，简单解释一下 ViewModel 和 Coroutines 是怎么配合防止主线程卡顿的）。”

### 🎨 第三阶段：首页 UI 搭建 (Phase 3)

**【用户输入指令给 AI】:** > “数据层准备好了。进入【第三阶段】：使用 Jetpack Compose 构建首页 (Dashboard) UI。

> 1. 请帮我实现一个简单的主界面，包含顶部一个卡片（显示当前净资产、本月收入、本月支出）。
> 2. 卡片下方是一个账单明细列表（LazyColumn），暂时显示最近的 10 条 Transaction。
> 3. 界面右下角需要一个悬浮添加按钮 (FloatingActionButton)。
> 4. 请确保 ViewModel 的状态流 (StateFlow) 能够正确驱动 Compose 的重组 (Recomposition)。”

### ✍️ 第四阶段：记账功能与数字键盘 (Phase 4)

**【用户输入指令给 AI】:** > “现在进入【第四阶段】：手动记账界面。

> 1. 当用户点击首页的加号按钮时，跳转到（或弹出一个底部面板 BottomSheet）记账页面。
> 2. 这个页面需要可以选择交易类型（支出/收入/转账）、选择账户、选择分类。
> 3. 请用 Compose 帮我手写一个简单的 T9 数字键盘（包含 0-9，小数点，退格键和确认键），代替系统自带键盘输入金额，这样体验更好。
> 4. 点击确认后，调用 ViewModel 插入数据，并返回首页。请给我完整的 UI 代码和 ViewModel 更新逻辑。”

### 🤖 第五阶段：辅助功能核心 (Phase 5 - 高阶阶段)

**【用户输入指令给 AI】:**

> “前置的手动记账已经跑通了。现在我们进入这个 App 的核心：【第五阶段】基于 AccessibilityService 的屏幕抓取。
>
> 1. 请教我如何创建并注册一个 `AccessibilityService`，以及如何在 `res/xml` 中配置它去监听特定的包名（比如先只监听微信 `com.tencent.mm`）。
> 2. 帮我写一段基础的解析代码：当服务监听到窗口变化 (TYPE_WINDOW_STATE_CHANGED) 时，如何遍历屏幕上的文本节点，寻找带有“支付\扣款\付款\转账”和“￥”符号的文本？
> 3. 抓取到数据后，如何用 `WindowManager` 触发一个全局悬浮窗，让用户点击“保存”将数据写入我们的 Room 数据库？ 这是安卓底层的复杂操作，请务必给出非常详细的步骤、所需权限申请，并考虑到 Android 高版本对悬浮窗的限制。”

*(文档结束)*