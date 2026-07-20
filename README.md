# StructuredBareboneCmpProject

## Language / Dil

- [English](#english)
- [Türkçe](#turkce)
- [Русский](#russian)
- [中文](#chinese)

---

<a id="english"></a>

## English

A Kotlin Multiplatform (KMP) template targeting Android and iOS, built with Compose Multiplatform and a modular structure.

## Tech Stack

- Kotlin Multiplatform (`2.3.20`)
- Compose Multiplatform (`1.10.3`)
- Koin (`4.2.1`) for dependency injection
- Ktor (`3.4.2`) for networking
- Kotlinx Serialization + Coroutines

## Project Structure Analysis

```text
.
|-- composeApp/                 # Main app module (Android app + shared Compose UI entry)
|-- iosApp/                     # Native iOS Swift wrapper project
|-- shared/                     # Shared contracts/models/navigation primitives
|-- data/                       # Data layer: API client, repository interfaces/implementations
|-- viewmodel/                  # Presentation logic (ViewModel)
|-- navigation/                 # NavGraph setup and route bindings
|-- di/                         # Koin module definitions
|-- screen/
|   `-- template/               # Feature screen UI module (example/template)
|-- gradle/                     # Wrapper + version catalog
|-- build.gradle.kts            # Root build config
|-- settings.gradle.kts         # Included modules
`-- README.md
```

## Module Responsibilities

- `:composeApp`
  - App entry point (`App.kt`), Android `MainActivity`, iOS `MainViewController` bridge.
  - Depends on `:shared`, `:navigation`, `:di`.
- `:navigation`
  - Defines `SetupNavGraph`, currently routes to `TemplateScreen`.
  - Depends on `:shared`, `:screen:template`.
- `:screen:template`
  - Feature UI (`TemplateScreen`) and ViewModel injection from Koin.
  - Depends on `:viewmodel`.
- `:viewmodel`
  - `TemplateViewModel` manages state and calls repository.
  - Depends on `:shared`, `:data`.
- `:data`
  - Ktor client setup, repository contract + implementation, safe request wrapper.
  - Depends on `:shared`.
- `:di`
  - Wires `ApiClient`, `TemplateRepository`, and `TemplateViewModel` in Koin.
  - Depends on `:shared`, `:data`, `:viewmodel`.
- `:shared`
  - Common result wrapper (`Result`) and navigation route model (`Screen`).

## Current App Flow

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## Build and Run

### Prerequisites

- JDK 11+
- Android Studio (for Android)
- Xcode (for iOS)

### Android (from terminal)

On Windows:

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :composeApp:installDebug
```

On macOS/Linux:

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Select a simulator/device.
3. Run the `iosApp` scheme.

## Notes

- `ApiClient.BASE_URL` is currently a placeholder (`https://www.google.com`).
- The sample `TemplateRepositoryImpl` currently returns mock/template data and is ready to be replaced with real API calls.

---

<a id="turkce"></a>

## Türkçe

Android ve iOS hedefleyen, Compose Multiplatform ile oluşturulmuş modüler bir Kotlin Multiplatform (KMP) şablonu.

## Teknoloji Yığını

- Kotlin Multiplatform (`2.3.20`)
- Compose Multiplatform (`1.10.3`)
- Bağımlılık enjeksiyonu için Koin (`4.2.1`)
- Ağ işlemleri için Ktor (`3.4.2`)
- Kotlinx Serialization + Coroutines

## Proje Klasör Yapısı Analizi

```text
.
|-- composeApp/                 # Ana uygulama modülü (Android uygulaması + paylaşılan Compose giriş noktası)
|-- iosApp/                     # Yerel iOS Swift sarmalayıcı projesi
|-- shared/                     # Ortak sözleşmeler/modeller/navigasyon temel bileşenleri
|-- data/                       # Veri katmanı: API istemcisi, repository arayüzleri/uygulamaları
|-- viewmodel/                  # Sunum katmanı mantığı (ViewModel)
|-- navigation/                 # NavGraph kurulumu ve rota eşlemeleri
|-- di/                         # Koin modül tanımları
|-- screen/
|   `-- template/               # Özellik ekranı UI modülü (örnek/şablon)
|-- gradle/                     # Wrapper + version catalog
|-- build.gradle.kts            # Kök build ayarı
|-- settings.gradle.kts         # Dahil edilen modüller
`-- README.md
```

## Modül Sorumlulukları

- `:composeApp`
  - Uygulama giriş noktası (`App.kt`), Android `MainActivity`, iOS `MainViewController` köprüsü.
  - `:shared`, `:navigation`, `:di` modüllerine bağlıdır.
- `:navigation`
  - `SetupNavGraph` tanımlar, şu an `TemplateScreen` rotasına gider.
  - `:shared`, `:screen:template` modüllerine bağlıdır.
- `:screen:template`
  - Özellik UI (`TemplateScreen`) ve Koin ile ViewModel enjeksiyonu içerir.
  - `:viewmodel` modülüne bağlıdır.
- `:viewmodel`
  - `TemplateViewModel` durum yönetimi yapar ve repository çağırır.
  - `:shared`, `:data` modüllerine bağlıdır.
- `:data`
  - Ktor istemci kurulumu, repository sözleşmesi + uygulaması, güvenli istek sarmalayıcısı içerir.
  - `:shared` modülüne bağlıdır.
- `:di`
  - Koin içinde `ApiClient`, `TemplateRepository` ve `TemplateViewModel` bağımlılıklarını bağlar.
  - `:shared`, `:data`, `:viewmodel` modüllerine bağlıdır.
- `:shared`
  - Ortak sonuç sarmalayıcısı (`Result`) ve navigasyon rota modeli (`Screen`) içerir.

## Mevcut Uygulama Akışı

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## Derleme ve Çalıştırma

### Gereksinimler

- JDK 11+
- Android Studio (Android için)
- Xcode (iOS için)

### Android (terminalden)

Windows:

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :composeApp:installDebug
```

macOS/Linux:

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### iOS

1. Xcode ile `iosApp/iosApp.xcodeproj` dosyasını açın.
2. Bir simülatör/cihaz seçin.
3. `iosApp` şemasını çalıştırın.

## Notlar

- `ApiClient.BASE_URL` şu anda bir yer tutucudur (`https://www.google.com`).
- Örnek `TemplateRepositoryImpl` şu anda mock/şablon veri döndürür ve gerçek API çağrılarıyla değiştirilmeye hazırdır.

---

<a id="russian"></a>

## Русский

Модульный шаблон Kotlin Multiplatform (KMP) для Android и iOS, построенный на Compose Multiplatform.

## Технологии

- Kotlin Multiplatform (`2.3.20`)
- Compose Multiplatform (`1.10.3`)
- Koin (`4.2.1`) для dependency injection
- Ktor (`3.4.2`) для сетевых запросов
- Kotlinx Serialization + Coroutines

## Анализ структуры проекта

```text
.
|-- composeApp/                 # Основной модуль приложения (Android + общий Compose entry)
|-- iosApp/                     # Нативный iOS-проект-обертка на Swift
|-- shared/                     # Общие контракты/модели/базовые элементы навигации
|-- data/                       # Data layer: API client, repository интерфейсы/реализации
|-- viewmodel/                  # Логика presentation-слоя (ViewModel)
|-- navigation/                 # Настройка NavGraph и привязка маршрутов
|-- di/                         # Определения модулей Koin
|-- screen/
|   `-- template/               # UI-модуль экрана (пример/шаблон)
|-- gradle/                     # Wrapper + version catalog
|-- build.gradle.kts            # Корневая build-конфигурация
|-- settings.gradle.kts         # Подключенные модули
`-- README.md
```

## Ответственность модулей

- `:composeApp`
  - Точка входа приложения (`App.kt`), Android `MainActivity`, iOS мост `MainViewController`.
  - Зависит от `:shared`, `:navigation`, `:di`.
- `:navigation`
  - Содержит `SetupNavGraph`, сейчас ведет на `TemplateScreen`.
  - Зависит от `:shared`, `:screen:template`.
- `:screen:template`
  - UI экрана (`TemplateScreen`) и внедрение ViewModel через Koin.
  - Зависит от `:viewmodel`.
- `:viewmodel`
  - `TemplateViewModel` управляет состоянием и вызывает repository.
  - Зависит от `:shared`, `:data`.
- `:data`
  - Настройка Ktor-клиента, контракт + реализация repository, safe request wrapper.
  - Зависит от `:shared`.
- `:di`
  - Связывает `ApiClient`, `TemplateRepository` и `TemplateViewModel` в Koin.
  - Зависит от `:shared`, `:data`, `:viewmodel`.
- `:shared`
  - Общий контейнер результата (`Result`) и модель навигационных маршрутов (`Screen`).

## Текущий поток приложения

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## Сборка и запуск

### Требования

- JDK 11+
- Android Studio (для Android)
- Xcode (для iOS)

### Android (из терминала)

Windows:

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :composeApp:installDebug
```

macOS/Linux:

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### iOS

1. Откройте `iosApp/iosApp.xcodeproj` в Xcode.
2. Выберите симулятор/устройство.
3. Запустите схему `iosApp`.

## Примечания

- `ApiClient.BASE_URL` сейчас содержит placeholder (`https://www.google.com`).
- Пример `TemplateRepositoryImpl` пока возвращает mock/template данные и готов к замене на реальные API вызовы.

---

<a id="chinese"></a>

## 中文

这是一个面向 Android 和 iOS 的 Kotlin Multiplatform（KMP）模块化模板，基于 Compose Multiplatform 构建。

## 技术栈

- Kotlin Multiplatform (`2.3.20`)
- Compose Multiplatform (`1.10.3`)
- Koin (`4.2.1`)（依赖注入）
- Ktor (`3.4.2`)（网络层）
- Kotlinx Serialization + Coroutines

## 项目结构分析

```text
.
|-- composeApp/                 # 主应用模块（Android 应用 + 共享 Compose 入口）
|-- iosApp/                     # 原生 iOS Swift 包装工程
|-- shared/                     # 共享契约/模型/导航基础组件
|-- data/                       # 数据层：API 客户端、Repository 接口/实现
|-- viewmodel/                  # 展示层逻辑（ViewModel）
|-- navigation/                 # NavGraph 配置与路由绑定
|-- di/                         # Koin 模块定义
|-- screen/
|   `-- template/               # 功能页面 UI 模块（示例/模板）
|-- gradle/                     # Wrapper + version catalog
|-- build.gradle.kts            # 根构建配置
|-- settings.gradle.kts         # 已包含模块
`-- README.md
```

## 模块职责

- `:composeApp`
  - 应用入口（`App.kt`）、Android `MainActivity`、iOS `MainViewController` 桥接。
  - 依赖 `:shared`、`:navigation`、`:di`。
- `:navigation`
  - 定义 `SetupNavGraph`，当前路由到 `TemplateScreen`。
  - 依赖 `:shared`、`:screen:template`。
- `:screen:template`
  - 功能 UI（`TemplateScreen`）以及通过 Koin 注入 ViewModel。
  - 依赖 `:viewmodel`。
- `:viewmodel`
  - `TemplateViewModel` 管理状态并调用 repository。
  - 依赖 `:shared`、`:data`。
- `:data`
  - Ktor 客户端配置、repository 契约与实现、safe request 包装。
  - 依赖 `:shared`。
- `:di`
  - 在 Koin 中装配 `ApiClient`、`TemplateRepository` 和 `TemplateViewModel`。
  - 依赖 `:shared`、`:data`、`:viewmodel`。
- `:shared`
  - 通用结果封装（`Result`）和导航路由模型（`Screen`）。

## 当前应用流程

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## 构建与运行

### 前置要求

- JDK 11+
- Android Studio（用于 Android）
- Xcode（用于 iOS）

### Android（命令行）

Windows:

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :composeApp:installDebug
```

macOS/Linux:

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### iOS

1. 在 Xcode 中打开 `iosApp/iosApp.xcodeproj`。
2. 选择模拟器或设备。
3. 运行 `iosApp` scheme。

## 备注

- `ApiClient.BASE_URL` 当前是占位地址（`https://www.google.com`）。
- 示例 `TemplateRepositoryImpl` 目前返回 mock/template 数据，后续可替换为真实 API 调用。
