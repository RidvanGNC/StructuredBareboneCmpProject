package org.structured_barebone.data.client

/** Minimal log that is silent unless [debug] is switched on (debug builds and the desktop shell). */
object AppLog {
    var debug: Boolean = false

    fun d(message: String) {
        if (debug) println(message)
    }
}
