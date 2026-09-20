package org.structured_barebone.packages.tenant.runtime

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.structured_barebone.packages.tenant.data.TenantRepository
import org.structured_barebone.packages.tenant.model.TenantBootstrap

sealed interface TenantSessionState {
    data object Idle : TenantSessionState
    data object Loading : TenantSessionState
    data class Ready(val bootstrap: TenantBootstrap) : TenantSessionState
    data class Error(val message: String) : TenantSessionState
}

/** Presentation layers own coroutine scopes; this store never creates an unbounded scope. */
class TenantSessionStore(
    private val repository: TenantRepository,
) {
    private val mutableState = MutableStateFlow<TenantSessionState>(TenantSessionState.Idle)
    val state: StateFlow<TenantSessionState> = mutableState.asStateFlow()

    suspend fun refresh() = update { repository.loadBootstrap() }

    suspend fun selectActiveTenant(tenantId: String) = update {
        repository.selectActiveTenant(tenantId)
    }

    private suspend fun update(request: suspend () -> TenantBootstrap) {
        mutableState.value = TenantSessionState.Loading
        try {
            mutableState.value = TenantSessionState.Ready(request())
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            mutableState.value = TenantSessionState.Error("Tenant context could not be refreshed.")
        }
    }
}
