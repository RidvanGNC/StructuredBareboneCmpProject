package org.structured_barebone.pricingrates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_currency_eur
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_currency_label
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_currency_try
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_currency_usd
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_language_code_label
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_loading
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_rate_label
import structuredbarebonecmpproject.shared.generated.resources.pricing_rates_title
import org.structured_barebone.shared.components.LanguageLabel
import org.structured_barebone.shared.components.LanguageSelectField
import org.structured_barebone.shared.model.Currency
import org.structured_barebone.shared.model.PricingRateDto
import org.structured_barebone.viewmodel.PricingRatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingRatesScreen() {
    val viewModel: PricingRatesViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    var languageCode by remember { mutableStateOf<String?>(null) }
    var rateText by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf(Currency.TRY) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.pricing_rates_title)) }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageSelectField(
                        label = stringResource(Res.string.pricing_rates_language_code_label),
                        selectedCode = languageCode,
                        onSelect = { languageCode = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = { Text(stringResource(Res.string.pricing_rates_rate_label)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    CurrencyDropdown(
                        label = stringResource(Res.string.pricing_rates_currency_label),
                        selected = currency,
                        onSelect = { currency = it },
                    )
                    Button(
                        onClick = {
                            val code = languageCode
                            val rate = rateText.toDoubleOrNull()
                            if (code != null && rate != null) {
                                viewModel.upsert(code, rate, currency)
                                languageCode = null
                                rateText = ""
                                currency = Currency.TRY
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
                                currency = rate.currency
                            },
                            onDelete = { viewModel.remove(rate.id) },
                        )
                    }
                }
            }
        }
    }
}

private fun currencySymbol(currency: Currency): String = when (currency) {
    Currency.TRY -> "₺"
    Currency.USD -> "$"
    Currency.EUR -> "€"
}

@Composable
private fun currencyName(currency: Currency): String = when (currency) {
    Currency.TRY -> stringResource(Res.string.pricing_rates_currency_try)
    Currency.USD -> stringResource(Res.string.pricing_rates_currency_usd)
    Currency.EUR -> stringResource(Res.string.pricing_rates_currency_eur)
}

@Composable
private fun CurrencyDropdown(label: String, selected: Currency, onSelect: (Currency) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("${currencySymbol(selected)} ${currencyName(selected)}", modifier = Modifier.fillMaxWidth())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Currency.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text("${currencySymbol(option)} ${currencyName(option)}") },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
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
                LanguageLabel(code = rate.languageCode, style = MaterialTheme.typography.titleMedium)
                Text(text = "${currencySymbol(rate.currency)} ${rate.ratePerCharacter}")
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
