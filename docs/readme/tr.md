# Structured Barebone CMP Project

Bu şablonda üç bağımsız istemci giriş noktası vardır: Kotlin Multiplatform ve Compose ile
Android/iOS/desktop; ayrıca bağımsız bir React web uygulaması. UI, navigasyon, DI veya istemci data
uygulaması değil; yalnızca backend sözleşmeleri paylaşılır.

## Hızlı başlangıç

```bash
# Android
gradlew.bat :androidApp:assembleDebug

# Desktop / Windows
gradlew.bat :desktopApp:run

# React web
cd webApp
npm ci
npm run dev
```

Gradle için JDK 17+, React için Node `^20.19.0 || >=22.12.0` gerekir.

## Demo

İki istemci de paketler, çalışma alanı metrikleri ve aktivite içeren bir **Operations Workspace**
fixture’ı gösterir. Bu içerik canlı tenant entegrasyonu değildir; backend bağlanmadan önce aşağıdaki
karar rehberleri okunmalıdır.

## Kritik kararlar

| Karar | Varsayılan | Değiştirmeden önce |
| --- | --- | --- |
| Tenant modu | Single-tenant temel; multi-tenant opt-in | [tenant mimarisi](../tenant-architecture.md) |
| Web mimarisi | Bağımsız React uygulaması | [webApp rehberi](../../webApp/README.md) |
| Özellik dağıtımı | Sunucu entitlement’ının açtığı, yerelde derlenmiş paketler | [tenant paket rehberi](../../packages/tenant/README.md) |
| Ortak sınır | Ortak KMP/React kodu değil, OpenAPI sözleşmesi | [contracts rehberi](../../contracts/README.md) |

## Sürümleme ve doğrulama

[VERSIONING.md](../../VERSIONING.md) dosyasına bakın. Commit öncesinde ilgili Gradle derleme/test
görevlerini; web değişikliklerinde `npm run build` ve iki npm audit komutunu çalıştırın.
