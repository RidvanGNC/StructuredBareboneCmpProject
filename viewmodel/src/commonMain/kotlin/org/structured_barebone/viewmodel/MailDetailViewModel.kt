package org.structured_barebone.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.structured_barebone.data.domain.MailReplyRepository
import org.structured_barebone.data.domain.MailRepository
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.MailReplyDto
import org.structured_barebone.shared.model.MailReplyType
import org.structured_barebone.shared.model.Result

class MailDetailViewModel(
    private val mailId: String,
    private val mailRepository: MailRepository,
    private val mailReplyRepository: MailReplyRepository,
) : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val mail: MailDto, val replies: List<MailReplyDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val mailResult = mailRepository.getMailById(mailId)
            val repliesResult = mailReplyRepository.getReplies(mailId)

            when {
                mailResult is Result.Success && repliesResult is Result.Success ->
                    _uiState.value = UiState.Success(mailResult.data, repliesResult.data)
                mailResult is Result.Error -> _uiState.value = UiState.Error(mailResult.message)
                repliesResult is Result.Error -> _uiState.value = UiState.Error(repliesResult.message)
            }
        }
    }

    fun generateDraft(replyType: MailReplyType) {
        viewModelScope.launch {
            val result = if (replyType == MailReplyType.Quote) {
                mailReplyRepository.generateQuoteDraft(mailId)
            } else {
                mailReplyRepository.generateDeliveryDraft(mailId)
            }
            if (result is Result.Success) load()
        }
    }

    fun updateDraft(replyId: String, subject: String, body: String) {
        viewModelScope.launch {
            val result = mailReplyRepository.updateDraft(replyId, subject, body)
            if (result is Result.Success) load()
        }
    }

    fun attachDeliveryFile(replyId: String, fileName: String) {
        viewModelScope.launch {
            val result = mailReplyRepository.attachDeliveryFile(replyId, fileName)
            if (result is Result.Success) load()
        }
    }

    fun approve(replyId: String, actualPrice: Double?) {
        viewModelScope.launch {
            val result = mailReplyRepository.approve(replyId, actualPrice)
            if (result is Result.Success) load()
        }
    }

    fun send(replyId: String) {
        viewModelScope.launch {
            val result = mailReplyRepository.send(replyId)
            if (result is Result.Success) load()
        }
    }
}
