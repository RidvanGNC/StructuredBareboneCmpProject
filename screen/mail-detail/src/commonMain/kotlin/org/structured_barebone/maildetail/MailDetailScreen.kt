package org.structured_barebone.maildetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.structured_barebone.shared.components.LanguageLabel
import org.structured_barebone.shared.components.LanguageSelectField
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_back_cd
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_actual_price
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_actual_price_label
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_approve
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_approved_pending_send
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_attachment_download
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_body_label
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_confirm_language
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_confirmed_language_label
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_content_section
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_delivery_tab
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_generate_draft
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_pick_file_simulation
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_predicted_language_label
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_predicted_price
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_quote_tab
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_reanalyze_cd
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_send
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_sent_at
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_subject_label
import structuredbarebonecmpproject.shared.generated.resources.mail_detail_title
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.MailReplyDto
import org.structured_barebone.shared.model.MailReplyType
import org.structured_barebone.viewmodel.MailDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailDetailScreen(mailId: String, onBack: () -> Unit) {
    val viewModel: MailDetailViewModel = koinInject(parameters = { parametersOf(mailId) })
    val uiState by viewModel.uiState.collectAsState()
    val attachmentError by viewModel.attachmentError.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(attachmentError) {
        attachmentError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAttachmentError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.mail_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back_cd))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.reanalyze() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(Res.string.mail_detail_reanalyze_cd))
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
                    MailSummary(state.mail, viewModel)

                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text(stringResource(Res.string.mail_detail_quote_tab)) },
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text(stringResource(Res.string.mail_detail_delivery_tab)) },
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
private fun MailSummary(mail: MailDto, viewModel: MailDetailViewModel) {
    Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val subject = mail.subject
            if (!subject.isNullOrBlank()) {
                Text(subject, style = MaterialTheme.typography.titleMedium)
            }
            val body = mail.body
            if (!body.isNullOrBlank()) {
                Text(
                    stringResource(Res.string.mail_detail_content_section),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(body, style = MaterialTheme.typography.bodyMedium)
            }

            if (mail.predictedPrice != null) {
                Text(stringResource(Res.string.mail_detail_predicted_price, mail.predictedPrice.toString()))
            }
            if (mail.actualPrice != null) {
                Text(stringResource(Res.string.mail_detail_actual_price, mail.actualPrice.toString()))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.mail_detail_predicted_language_label))
                Spacer(modifier = Modifier.width(8.dp))
                LanguageLabel(code = mail.predictedLanguage)
            }

            var selectedLanguage by remember(mail.id) { mutableStateOf(mail.confirmedLanguage) }
            LanguageSelectField(
                label = stringResource(Res.string.mail_detail_confirmed_language_label),
                selectedCode = selectedLanguage,
                onSelect = { code -> selectedLanguage = code },
                modifier = Modifier.fillMaxWidth(),
            )
            if (selectedLanguage != null && selectedLanguage != mail.confirmedLanguage) {
                Button(onClick = { viewModel.confirmLanguage(selectedLanguage!!) }) {
                    Text(stringResource(Res.string.mail_detail_confirm_language))
                }
            }

            val attachmentFileName = mail.attachmentFileName
            if (mail.hasAttachment && attachmentFileName != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(attachmentFileName, modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = { viewModel.downloadAttachment(attachmentFileName) }) {
                        Text(stringResource(Res.string.mail_detail_attachment_download))
                    }
                }
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
                Text(stringResource(Res.string.mail_detail_generate_draft))
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
            Text(stringResource(Res.string.mail_detail_sent_at, reply.sentAt.toString()), color = MaterialTheme.colorScheme.primary)
        } else if (isApproved) {
            Text(stringResource(Res.string.mail_detail_approved_pending_send), color = MaterialTheme.colorScheme.secondary)
        }

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text(stringResource(Res.string.mail_detail_subject_label)) },
            enabled = isEditable,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text(stringResource(Res.string.mail_detail_body_label)) },
            enabled = isEditable,
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
        )

        if (isEditable) {
            Button(onClick = { viewModel.updateDraft(reply.id, subject, body) }) {
                Text(stringResource(Res.string.common_save))
            }
        }

        if (replyType == MailReplyType.Delivery) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    enabled = isEditable,
                    onClick = { viewModel.attachDeliveryFile(reply.id, "ceviri-damgali.pdf") },
                ) {
                    Text(stringResource(Res.string.mail_detail_pick_file_simulation))
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
                label = { Text(stringResource(Res.string.mail_detail_actual_price_label)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isEditable) {
            Button(
                onClick = { viewModel.approve(reply.id, actualPriceText.toDoubleOrNull()) },
                enabled = replyType != MailReplyType.Delivery || reply.attachmentFileName != null,
            ) {
                Text(stringResource(Res.string.mail_detail_approve))
            }
        }

        if (isApproved && !isSent) {
            Button(onClick = { viewModel.send(reply.id) }) {
                Text(stringResource(Res.string.mail_detail_send))
            }
        }
    }
}
