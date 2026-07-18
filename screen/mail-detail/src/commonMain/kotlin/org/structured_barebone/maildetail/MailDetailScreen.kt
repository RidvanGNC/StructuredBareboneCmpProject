package org.structured_barebone.maildetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.MailReplyDto
import org.structured_barebone.shared.model.MailReplyType
import org.structured_barebone.viewmodel.MailDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailDetailScreen(mailId: String, onBack: () -> Unit) {
    val viewModel: MailDetailViewModel = koinInject(parameters = { parametersOf(mailId) })
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mail Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
            )
        },
    ) { padding ->
        when (val state = uiState) {
            is MailDetailViewModel.UiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is MailDetailViewModel.UiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is MailDetailViewModel.UiState.Success -> {
                val quoteReply = state.replies.find { it.replyType == MailReplyType.Quote }
                val deliveryReply = state.replies.find { it.replyType == MailReplyType.Delivery }

                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    MailSummary(state.mail)

                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Fiyat Teklifi") },
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Çeviri Teslimi") },
                        )
                    }

                    if (selectedTab == 0) {
                        ReplyEditor(
                            replyType = MailReplyType.Quote,
                            reply = quoteReply,
                            predictedPrice = state.mail.predictedPrice,
                            viewModel = viewModel,
                        )
                    } else {
                        ReplyEditor(
                            replyType = MailReplyType.Delivery,
                            reply = deliveryReply,
                            predictedPrice = state.mail.predictedPrice,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MailSummary(mail: MailDto) {
    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Dil: ${mail.predictedLanguage ?: "-"} / ${mail.confirmedLanguage ?: "-"}")
            if (mail.predictedPrice != null) {
                Text("Tahmini Fiyat: ${mail.predictedPrice}")
            }
            if (mail.actualPrice != null) {
                Text("Onaylanan Fiyat: ${mail.actualPrice}")
            }
        }
    }
}

@Composable
private fun ReplyEditor(
    replyType: MailReplyType,
    reply: MailReplyDto?,
    predictedPrice: Double?,
    viewModel: MailDetailViewModel,
) {
    if (reply == null) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Button(onClick = { viewModel.generateDraft(replyType) }) {
                Text("Taslak Oluştur")
            }
        }
        return
    }

    var subject by remember(reply.id) { mutableStateOf(reply.subject) }
    var body by remember(reply.id) { mutableStateOf(reply.body) }
    var actualPriceText by remember(reply.id) { mutableStateOf(predictedPrice?.toString() ?: "") }

    val isSent = reply.sentAt != null
    val isApproved = reply.isApproved
    val isEditable = !isApproved

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isSent) {
            Text("Gönderildi: ${reply.sentAt}", color = MaterialTheme.colorScheme.primary)
        } else if (isApproved) {
            Text("Onaylandı, gönderim bekliyor", color = MaterialTheme.colorScheme.secondary)
        }

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Konu") },
            enabled = isEditable,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("İçerik") },
            enabled = isEditable,
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
        )

        if (isEditable) {
            Button(onClick = { viewModel.updateDraft(reply.id, subject, body) }) {
                Text("Kaydet")
            }
        }

        if (replyType == MailReplyType.Delivery) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    enabled = isEditable,
                    onClick = { viewModel.attachDeliveryFile(reply.id, "ceviri-damgali.pdf") },
                ) {
                    Text("Dosya Seç (Simülasyon)")
                }
                reply.attachmentFileName?.let {
                    Text(text = it, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        if (replyType == MailReplyType.Quote && isEditable) {
            OutlinedTextField(
                value = actualPriceText,
                onValueChange = { actualPriceText = it },
                label = { Text("Onaylanan Fiyat") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isEditable) {
            Button(
                onClick = { viewModel.approve(reply.id, actualPriceText.toDoubleOrNull()) },
                enabled = replyType != MailReplyType.Delivery || reply.attachmentFileName != null,
            ) {
                Text("Onayla")
            }
        }

        if (isApproved && !isSent) {
            Button(onClick = { viewModel.send(reply.id) }) {
                Text("Gönder")
            }
        }
    }
}
