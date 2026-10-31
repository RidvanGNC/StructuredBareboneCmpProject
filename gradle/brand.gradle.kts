// Optional product branding for the build. A product with a single, fixed identity can delete this file
// (and brand.properties.example); the build then uses the defaults written in the application modules.
//
// Values come from a properties file named by -PbrandFile=<path> and can be overridden one by one with
// -Pbrand.<key>=<value>. Keys:
//   productName         display name of the Android app
//   applicationId       Android application id
//   desktopPackageName  name of the desktop installer package
//
// The result is published as rootProject.extra["brandValues"] (a Map<String, String>).
import java.util.Properties

val brandFile: File? = (findProperty("brandFile") as String?)
    ?.takeIf { it.isNotBlank() }
    ?.let { path -> File(path).let { if (it.isAbsolute) it else rootProject.file(path) } }

val fileValues = Properties().apply {
    brandFile?.let { file ->
        require(file.isFile) { "brandFile not found: $file" }
        file.inputStream().use { load(it) }
    }
}

val brandKeys = listOf("productName", "applicationId", "desktopPackageName")

extra["brandValues"] = brandKeys.mapNotNull { key ->
    val value = (findProperty("brand.$key") as String?)?.takeIf { it.isNotBlank() }
        ?: fileValues.getProperty(key)?.takeIf { it.isNotBlank() }
    value?.let { key to it }
}.toMap()
