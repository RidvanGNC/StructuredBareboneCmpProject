package org.structured_barebone.data.client

/**
 * Application version, assigned by each platform at start-up. The server reads it from the
 * `X-Client-Version` header and answers 426 when it is below the minimum it supports.
 */
object AppInfo {
    const val DEV_VERSION = "9999.0.0"
    private val numericPrefix = Regex("""^\d+(\.\d+){1,3}""")

    var version: String = ""

    /** Numeric part of [version]; development and debug builds without a real version report [DEV_VERSION]. */
    fun headerVersion(): String = numericPrefix.find(version)?.value ?: DEV_VERSION
}
