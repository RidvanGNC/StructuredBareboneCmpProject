// Optional product branding for the build. A product with a single, fixed identity can delete this file
// (and brand.properties.example); the build then uses the defaults written in the application modules.
//
// Values come from a properties file named by -PbrandFile=<path> and can be overridden one by one with
// -Pbrand.<key>=<value>. Keys:
//   productName         display name of the Android app
//   applicationId       Android application id
//   desktopPackageName  name of the desktop installer package
//   upgradeUuid         Windows installer upgrade code; unique per product and never changed afterwards.
//                       Required to build the MSI, optional otherwise.
//   vendor              publisher name shown by the Windows installer (optional)
//   androidIconDir      directory with res-style folders (mipmap-*/ic_launcher*) that replace the
//                       template launcher icons (optional)
//   desktopIcon         .ico file used by the Windows installer (optional)
//
// Relative paths (the brand file and the two icon paths) are resolved against the repository root.
// The result is published as rootProject.extra["brandValues"] (a Map<String, String>); path values are
// stored as absolute paths.
import java.util.Properties

fun resolve(path: String): File = File(path).let { if (it.isAbsolute) it else rootProject.file(path) }

val brandFile: File? = (findProperty("brandFile") as String?)
    ?.takeIf { it.isNotBlank() }
    ?.let(::resolve)

val fileValues = Properties().apply {
    brandFile?.let { file ->
        require(file.isFile) { "brandFile not found: $file" }
        file.inputStream().use { load(it) }
    }
}

val brandKeys = listOf(
    "productName", "applicationId", "desktopPackageName", "upgradeUuid", "vendor", "androidIconDir", "desktopIcon",
)
val directoryKeys = setOf("androidIconDir")
val fileKeys = setOf("desktopIcon")

extra["brandValues"] = brandKeys.mapNotNull { key ->
    val raw = (findProperty("brand.$key") as String?)?.takeIf { it.isNotBlank() }
        ?: fileValues.getProperty(key)?.trim()?.takeIf { it.isNotBlank() }
    raw?.let { value ->
        val resolved = when (key) {
            in directoryKeys -> resolve(value).also { require(it.isDirectory) { "brand $key is not a directory: $it" } }.absolutePath
            in fileKeys -> resolve(value).also { require(it.isFile) { "brand $key is not a file: $it" } }.absolutePath
            "upgradeUuid" -> value.also {
                val uuid = runCatching { java.util.UUID.fromString(it) }.getOrNull()
                require(uuid != null) { "brand upgradeUuid is not a UUID: $it" }
                require(uuid.mostSignificantBits != 0L || uuid.leastSignificantBits != 0L) {
                    "brand upgradeUuid is the placeholder from brand.properties.example; generate a new one"
                }
            }
            else -> value
        }
        key to resolved
    }
}.toMap()
