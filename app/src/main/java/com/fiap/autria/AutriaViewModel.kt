package com.fiap.autria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AutriaViewModel : ViewModel() {
    private val backend = BackendClient()
    private val _state = MutableStateFlow(NavigationState())
    val state = _state.asStateFlow()

    private val _notice = MutableStateFlow<String?>(null)
    val notice = _notice.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                runCatching { backend.getAppState() }
                    .onSuccess { _state.value = it }
                    .onFailure {
                        _state.value = _state.value.copy(connected = false, backendOnline = false)
                    }
                delay(500)
            }
        }
    }

    fun setAudioEnabled(enabled: Boolean) {
        val old = _state.value
        _state.value = old.copy(audioEnabled = enabled)
        viewModelScope.launch {
            runCatching { backend.setAudioEnabled(enabled, old) }
                .onFailure {
                    _state.value = old
                    showNotice("Não foi possível alterar a orientação por voz: ${it.message}")
                }
        }
    }

    fun triggerEmergency() {
        viewModelScope.launch {
            runCatching { backend.triggerEmergency() }
                .onSuccess { showNotice("Emergência enviada aos contatos cadastrados.") }
                .onFailure { showNotice("Emergência não enviada: ${it.message}") }
        }
    }

    private fun showNotice(message: String) {
        _notice.value = message
        viewModelScope.launch {
            delay(4_000)
            if (_notice.value == message) _notice.value = null
        }
    }
}
