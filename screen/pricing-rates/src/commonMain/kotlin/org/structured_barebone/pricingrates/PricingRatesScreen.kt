package org.structured_barebone.pricingrates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import structuredbarebonecmpproject.shared.generated.resources.Res
import structuredbarebonecmpproject.shared.generated.resources.common_delete_cd
import structuredbarebonecmpproject.shared.generated.resources.common_edit_cd
import structuredbarebonecmpproject.shared.generated.resources.common_save
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_language_code_label
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_loading
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_rate_label
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_title
import org.structured_barebone.shared.model.PricingRateDto
import org.structured_barebone.viewmodel.PricingRatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingRatesScreen() {
    val viewModel: PricingRatesViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    var languageCode by remember { mutableStateOf("") }
    var rateText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.pricing_rates_title)) }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = languageCode,
                        onValueChange = { languageCode = it },
                        label = { Text(stringResource(Res.string.pricing_rates_language_code_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = { Text(stringResource(Res.string.pricing_rates_rate_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = {
                            val rate = rateText.toDoubleOrNull()
                            if (languageCode.isNotBlank() && rate != null) {
                                viewModel.upsert(languageCode.trim().lowercase(), rate)
                                languageCode = ""
                                rateText = ""
                            }
                        },
                    ) {
                        Text(stringResource(Res.string.common_save))
                    }
                }
            }

            when (val state = uiState) {
                is PricingRatesViewModel.UiState.Loading -> Text(
                    text = stringResource(Res.string.pricing_rates_loading),
                    modifier = Modifier.padding(12.dp),
                )

                is PricingRatesViewModel.UiState.Error -> Text(
                    text = state.message,
                    modifier = Modifier.padding(12.dp),
                )

                is PricingRatesViewModel.UiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.rates) { rate ->
                        RateRow(
                            rate = rate,
                            onEdit = {
                                languageCode = rate.languageCode
                                rateText = rate.ratePerCharacter.toString()
                            },
                            onDelete = { viewModel.remove(rate.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RateRow(rate: PricingRateDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = rate.languageCode.uppercase(), style = MaterialTheme.typography.titleMedium)
                Text(text = rate.ratePerCharacter.toString())
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.common_edit_cd))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.common_delete_cd))
                }
            }
        }
    }
}
