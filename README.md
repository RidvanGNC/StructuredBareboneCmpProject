# StructuredBareboneCmpProject

## Language / Dil

- [English](#english)
- [Türkçe](#turkce)
- [Русский](#russian)
- [中文](#chinese)

---

<a id="english"></a>

## English

A Kotlin Multiplatform (KMP) template targeting Android and iOS, built with Compose Multiplatform and a modular structure. See [VERSIONING.md](VERSIONING.md) for deterministic local and CI version configuration.

## Tech Stack

- Kotlin Multiplatform (`2.3.21`)
- Compose Multiplatform (`1.10.3`)
- Koin (`4.2.2`) for dependency injection
- Ktor (`3.4.3`) for networking
- Kotlinx Serialization + Coroutines

## Project Structure Analysis

```text
.
|-- composeApp/                 # Main app module (Android app + shared Compose UI entry)
|-- desktopApp/                 # Desktop window and native installer packaging entry
|-- iosApp/                     # Native iOS Swift wrapper project
|-- shared/                     # Shared CMP business contracts/models (no UI/navigation)
|-- data/                       # Data layer: API client, repository interfaces/implementations
|-- viewmodel/                  # Presentation logic (ViewModel)
|-- navigation/                 # NavGraph setup and route bindings
|-- di/                         # Koin module definitions
|-- screen/
|   `-- template/               # Feature screen UI module (example/template)
|-- webApp/                     # Independent React/TypeScript SPA (separate Node build)
|-- packages/tenant/            # Optional KMP tenant + entitlement capability
|-- contracts/                  # API contracts shared semantically by KMP and React
|-- gradle/                     # Wrapper + version catalog
|-- build.gradle.kts            # Root build config
|-- settings.gradle.kts         # Included modules
`-- README.md
```

## Module Responsibilities

- `:composeApp`
  - App entry point (`App.kt`), Android `MainActivity`, iOS `MainViewController` bridge.
  - Depends on `:navigation`, `:di`.
- `:navigation`
  - Defines `SetupNavGraph`, currently routes to `TemplateScreen`.
  - Owns navigation route types and depends on `:screen:template`.
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
  - Common CMP business contracts/models such as the result wrapper (`Result`).

- `webApp/`
  - Independent React UI, browser HTTP client and web-only state. It shares API contracts with CMP,
    never Compose screens, Koin, Ktor implementations or the Gradle module tree.

## Current App Flow

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## Build and Run

### Prerequisites

- JDK 17+
- Android Studio (for Android)
- Xcode (for iOS)

### Android (from terminal)

On Windows:

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :composeApp:installDebug
```

### Desktop / Windows

```bash
gradlew.bat :desktopApp:run
gradlew.bat :desktopApp:packageMsi
```

`desktopApp` only owns the JVM window and MSI packaging. Shared UI, state, DI, and data modules remain reusable; the JVM-specific Ktor client is implemented in `data/src/jvmMain`.

### Web / React

```bash
cd webApp
npm ci
npm run dev
```

`webApp` is intentionally independent from Gradle and Compose. Its only integration point is the
backend API contract; see [webApp/ARCHITECTURE.md](webApp/ARCHITECTURE.md) and
[webApp/SECURITY.md](webApp/SECURITY.md).

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

- `ApiClient.BASE_URL` is intentionally non-routable (`https://example.invalid`); a consuming app must supply its own API configuration.
- The sample `TemplateRepositoryImpl` currently returns mock/template data and is ready to be replaced with real API calls.

---

<a id="turkce"></a>

## Türkçe

Android ve iOS hedefleyen, Compose Multiplatform ile oluşturulmuş modüler bir Kotlin Multiplatform (KMP) şablonu. Yerel ve CI sürüm ayarları için [VERSIONING.md](VERSIONING.md) dosyasına bakın.

## Teknoloji Yığını

- Kotlin Multiplatform (`2.3.21`)
- Compose Multiplatform (`1.10.3`)
- Bağımlılık enjeksiyonu için Koin (`4.2.2`)
- Ağ işlemleri için Ktor (`3.4.3`)
- Kotlinx Serialization + Coroutines

## Proje Klasör Yapısı Analizi

```text
.
|-- composeApp/                 # Ana uygulama modülü (Android uygulaması + paylaşılan Compose giriş noktası)
|-- desktopApp/                 # Masaüstü penceresi ve native paketleme giriş noktası
|-- iosApp/                     # Yerel iOS Swift sarmalayıcı projesi
|-- shared/                     # Ortak CMP iş sözleşmeleri/modelleri (UI/navigasyon içermez)
|-- data/                       # Veri katmanı: API istemcisi, repository arayüzleri/uygulamaları
|-- viewmodel/                  # Sunum katmanı mantığı (ViewModel)
|-- navigation/                 # NavGraph kurulumu ve rota eşlemeleri
|-- di/                         # Koin modül tanımları
|-- screen/
|   `-- template/               # Özellik ekranı UI modülü (örnek/şablon)
|-- webApp/                     # Bağımsız React/TypeScript SPA (ayrı Node derlemesi)
|-- packages/tenant/            # Opsiyonel KMP tenant + entitlement yeteneği
|-- contracts/                  # KMP ve React'in anlamsal olarak paylaştığı API sözleşmeleri
|-- gradle/                     # Wrapper + version catalog
|-- build.gradle.kts            # Kök build ayarı
|-- settings.gradle.kts         # Dahil edilen modüller
`-- README.md
```

## Modül Sorumlulukları

- `:composeApp`
  - Uygulama giriş noktası (`App.kt`), Android `MainActivity`, iOS `MainViewController` köprüsü.
  - `:navigation`, `:di` modüllerine bağlıdır.
- `:navigation`
  - `SetupNavGraph` tanımlar, şu an `TemplateScreen` rotasına gider.
  - Navigasyon rota türlerini sahiplenir ve `:screen:template` modülüne bağlıdır.
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
  - `Result` gibi ortak CMP iş sözleşmelerini/modellerini içerir.

- `webApp/`
  - Bağımsız React UI, tarayıcı HTTP istemcisi ve web'e ait state'i içerir. CMP ile yalnızca API
    sözleşmesini paylaşır; Compose ekranlarını, Koin'i, Ktor uygulamasını veya Gradle modül ağacını paylaşmaz.

## Mevcut Uygulama Akışı

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## Derleme ve Çalıştırma

### Gereksinimler

- JDK 17+
- Android Studio (Android için)
- Xcode (iOS için)

### Android (terminalden)

Windows:

```bash
gradlew.bat :composeApp:assembleDebug
gradlew.bat :composeApp:installDebug
```

### Desktop / Windows

```bash
gradlew.bat :desktopApp:run
gradlew.bat :desktopApp:packageMsi
```

`desktopApp` yalnızca JVM penceresi ve MSI paketlemesinden sorumludur. Ortak UI, state, DI ve data modülleri yeniden kullanılmaya devam eder; JVM'e özgü Ktor istemcisi `data/src/jvmMain` içinde uygulanır.

### Web / React

```bash
cd webApp
npm ci
npm run dev
```

`webApp`, Gradle ve Compose'tan kasıtlı olarak bağımsızdır. Tek entegrasyon noktası backend API
sözleşmesidir; ayrıntılar için [webApp/ARCHITECTURE.md](webApp/ARCHITECTURE.md) ve
[webApp/SECURITY.md](webApp/SECURITY.md) dosyalarına bakın.

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

- `ApiClient.BASE_URL` kasıtlı olarak yönlendirilemeyen bir yer tutucudur (`https://example.invalid`); kullanan uygulama kendi API yapılandırmasını sağlamalıdır.
- Örnek `TemplateRepositoryImpl` şu anda mock/şablon veri döndürür ve gerçek API çağrılarıyla değiştirilmeye hazırdır.

---

<a id="russian"></a>

## Русский

Модульный шаблон Kotlin Multiplatform (KMP) для Android и iOS, построенный на Compose Multiplatform.

## Технологии

- Kotlin Multiplatform (`2.3.21`)
- Compose Multiplatform (`1.10.3`)
- Koin (`4.2.2`) для dependency injection
- Ktor (`3.4.3`) для сетевых запросов
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

- JDK 17+
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

- `ApiClient.BASE_URL` намеренно использует недоступный адрес-заполнитель (`https://example.invalid`); использующее приложение должно предоставить собственную конфигурацию API.
- Пример `TemplateRepositoryImpl` пока возвращает mock/template данные и готов к замене на реальные API вызовы.

---

<a id="chinese"></a>

## 中文

这是一个面向 Android 和 iOS 的 Kotlin Multiplatform（KMP）模块化模板，基于 Compose Multiplatform 构建。

## 技术栈

- Kotlin Multiplatform (`2.3.21`)
- Compose Multiplatform (`1.10.3`)
- Koin (`4.2.2`)（依赖注入）
- Ktor (`3.4.3`)（网络层）
- Kotlinx Serialization + Coroutines

## 项目结构分析

```text
.
|-- composeApp/                 # 主应用模块（Android 应用 + 共享 Compose 入口）
|-- iosApp/                     # 原生 iOS Swift 包装工程
|-- shared/                     # 共享 CMP 业务契约/模型（不含 UI/导航）
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
  - 通用 CMP 业务契约/模型，例如结果封装（`Result`）。

## 当前应用流程

`MainActivity/MainViewController` -> `App()` -> `SetupNavGraph()` -> `TemplateScreen` -> `TemplateViewModel` -> `TemplateRepository` -> `TemplateRepositoryImpl` -> `Result`

## 构建与运行

### 前置要求

- JDK 17+
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

- `ApiClient.BASE_URL` 有意使用不可路由的占位地址（`https://example.invalid`）；使用此模板的应用必须提供自己的 API 配置。
- 示例 `TemplateRepositoryImpl` 目前返回 mock/template 数据，后续可替换为真实 API 调用。
