package com.jikanhub.app.presentation.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.jikanhub.app.R
import com.jikanhub.app.presentation.theme.JikanOnSurface
import com.jikanhub.app.presentation.theme.JikanOnSurfaceVariant
import com.jikanhub.app.presentation.theme.JikanSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    
    Scaffold(
        containerColor = JikanSurface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Custom Top Bar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = JikanOnSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.drawer_settings),
                    style = MaterialTheme.typography.titleLarge,
                    color = JikanOnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.settings_notification_sound),
                    style = MaterialTheme.typography.titleMedium,
                    color = JikanOnSurface,
                    fontWeight = FontWeight.Bold
                )

                SoundPickerItem(
                    viewModel = hiltViewModel()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.settings_sync_manual),
                    style = MaterialTheme.typography.titleMedium,
                    color = JikanOnSurface,
                    fontWeight = FontWeight.Bold
                )

                ManualSyncItem(
                    viewModel = hiltViewModel()
                )
            }
        }
    }
}

@Composable
private fun SoundPickerItem(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val soundUri by viewModel.notificationSoundUri.collectAsState()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<android.net.Uri>(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            uri?.let { viewModel.saveNotificationSound(it.toString()) }
        }
    }

    Surface(
        onClick = {
            val intent = android.content.Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_NOTIFICATION)
                putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TITLE, "Selecione o som")
                putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, soundUri?.let { android.net.Uri.parse(it) })
                putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            }
            launcher.launch(intent)
        },
        color = JikanSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, JikanOnSurface.copy(alpha = 0.1f))
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
                    androidx.compose.material.icons.Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = JikanOnSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.settings_pick_sound),
                        style = MaterialTheme.typography.bodyLarge,
                        color = JikanOnSurface
                    )
                    soundUri?.let {
                        Text(
                            text = android.media.RingtoneManager.getRingtone(context, android.net.Uri.parse(it)).getTitle(context),
                            style = MaterialTheme.typography.labelSmall,
                            color = JikanOnSurfaceVariant
                        )
                    }
                }
            }
            Icon(
                androidx.compose.material.icons.Icons.Default.ChevronRight,
                contentDescription = null,
                tint = JikanOnSurfaceVariant
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

@Composable
private fun ManualSyncItem(
    viewModel: SettingsViewModel
) {
    val isSyncing by viewModel.isSyncing.collectAsState()

    Surface(
        onClick = { if (!isSyncing) viewModel.manualSync() },
        color = JikanSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, JikanOnSurface.copy(alpha = 0.1f))
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
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = if (isSyncing) MaterialTheme.colorScheme.primary else JikanOnSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.settings_sync_now),
                        style = MaterialTheme.typography.bodyLarge,
                        color = JikanOnSurface
                    )
                    if (isSyncing) {
                        Text(
                            text = stringResource(R.string.settings_sync_running),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = JikanOnSurfaceVariant
                )
            }
        }
    }
}
