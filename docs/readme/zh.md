# Structured Barebone CMP Project

该模板包含面向 Android、iOS 与 desktop 的独立 KMP/Compose 客户端，以及独立的 React Web
应用。共享边界是 API 契约，而不是 UI 或 data 层实现。

## 快速开始

```bash
gradlew.bat :androidApp:assembleDebug
gradlew.bat :desktopApp:run
cd webApp && npm ci && npm run dev
```

请使用 JDK 17+ 与 Node `^20.19.0 || >=22.12.0`。

Operations Workspace 演示仅使用本地 fixture 数据。基础项目是 single-tenant；如需
multi-tenant，显式接入 `:packages:tenant`。请先阅读 [tenant 架构](../tenant-architecture.md)
和 [API 契约](../../contracts/README.md)。演示界面可在英语和土耳其语之间切换，所选语言保存在设备上
（见架构决策中的本地化部分）。
