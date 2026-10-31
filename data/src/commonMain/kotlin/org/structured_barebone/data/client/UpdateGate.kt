package org.structured_barebone.data.client

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Becomes `required` when the server answers 426 (this client version is no longer supported).
 * The host app decides how to present it; it only clears when the process restarts with a new version.
 */
class UpdateGate {
    private val mutableRequired = MutableStateFlow(false)
    val required: StateFlow<Boolean> = mutableRequired.asStateFlow()

    fun markRequired() {
        mutableRequired.value = true
    }
}
