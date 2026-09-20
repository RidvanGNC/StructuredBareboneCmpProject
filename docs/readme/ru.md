# Structured Barebone CMP Project

Шаблон содержит независимые клиенты KMP/Compose для Android, iOS и desktop, а также отдельное
React web-приложение. Общей границей является API-контракт, а не UI или реализация data-слоя.

## Быстрый старт

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :desktopApp:run
cd webApp && npm ci && npm run dev
```

Используйте JDK 17+ и Node `^20.19.0 || >=22.12.0`.

Demo Operations Workspace использует только локальные fixture-данные. Базовый проект
single-tenant; multi-tenant подключается явно через `:packages:tenant`. Начните с
[документации tenant](../tenant-architecture.md) и [API-контракта](../../contracts/README.md).
