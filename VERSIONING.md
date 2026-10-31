# Versioning

Bu şablon, uygulama sürümünü doğrudan Gradle dosyasına yazmak yerine yapılandırmadan alır.

| Öncelik | Version name | Version code |
| --- | --- | --- |
| CI ortam değişkeni | `APP_VERSION_NAME` | `APP_VERSION_CODE` |
| GitHub Actions | `APP_VERSION_NAME` | `GITHUB_RUN_NUMBER` |
| Gradle özelliği | `app.version.name` | `app.version.code` |
| Yerel varsayılan | `0.1.0-SNAPSHOT` | `1` |

Yerel bir sürüm üretmek için:

```powershell
.\gradlew.bat :androidApp:assembleDebug `
  -Papp.version.name=0.2.0-dev `
  -Papp.version.code=2
```

CI, her dağıtım için artan bir `APP_VERSION_CODE` sağlamalıdır. `versionCode` mağaza ve cihaz güncellemelerinde monoton artmalıdır; bu nedenle Git commit sayısı veya tarih/saatten türetilmez. Sürüm adı SemVer biçiminde tutulmalıdır (örneğin `1.4.0` veya `1.4.0-rc.1`).

Bu proje GitHub Actions kullanıyorsa `GITHUB_RUN_NUMBER` otomatik bir kod sağlar. Başka bir CI sistemi kullanılıyorsa kendi artan build numarasını `APP_VERSION_CODE` olarak iletmelidir.
