package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.structured_barebone.data.domain.AppearanceRepository
import org.structured_barebone.shared.model.ThemeMode

class AppearanceViewModel(private val appearanceRepository: AppearanceRepository) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = appearanceRepository.themeMode

    fun select(mode: ThemeMode) = appearanceRepository.select(mode)
}
