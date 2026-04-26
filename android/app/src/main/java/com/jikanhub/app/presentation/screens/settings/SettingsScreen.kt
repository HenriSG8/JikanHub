package com.jikanhub.app.presentation.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.jikanhub.app.R
import com.jikanhub.app.presentation.theme.JikanOnSurface
import com.jikanhub.app.presentation.theme.JikanSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.drawer_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JikanSurface,
                    titleContentColor = JikanOnSurface
                )
            )
        },
        containerColor = JikanSurface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleMedium,
                color = JikanOnSurface,
                fontWeight = FontWeight.Bold
            )

            // Language Options
            LanguageItem(
                label = "Português (Brasil)",
                selected = currentLocale.contains("pt"),
                onClick = {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("pt-BR")
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            )

            LanguageItem(
                label = "English",
                selected = currentLocale.contains("en") || currentLocale.isEmpty(),
                onClick = {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            )
        }
    }
}

@Composable
private fun LanguageItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else JikanSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Language,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else JikanOnSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = JikanOnSurface
                )
            }
            if (selected) {
                RadioButton(selected = true, onClick = null)
            }
        }
    }
}
