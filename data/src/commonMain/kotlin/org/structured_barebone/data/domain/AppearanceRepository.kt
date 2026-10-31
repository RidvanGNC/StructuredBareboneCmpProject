package org.structured_barebone.data.domain

import kotlinx.coroutines.flow.StateFlow
import org.structured_barebone.shared.model.ThemeMode

/** The light/dark choice of the user; persisted on the device. */
interface AppearanceRepository {
    val themeMode: StateFlow<ThemeMode>

    fun select(mode: ThemeMode)
}
