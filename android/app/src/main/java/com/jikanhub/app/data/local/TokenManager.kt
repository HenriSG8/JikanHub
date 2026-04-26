package com.jikanhub.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val NAME_KEY = stringPreferencesKey("user_name")
    private val DARK_MODE_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("dark_mode")
    private val NOTIFICATION_SOUND_KEY = stringPreferencesKey("notification_sound_uri")
    private val TUTORIAL_COMPLETED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("tutorial_completed")
    private val LAST_SYNC_TIME_KEY = stringPreferencesKey("last_sync_time")
    
    // Easter Egg & Themes
    private val EASTER_EGG_UNLOCKED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("easter_egg_unlocked")
    private val APP_THEME_KEY = stringPreferencesKey("app_theme")

    val lastSyncTime: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SYNC_TIME_KEY]
        }

    suspend fun setLastSyncTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME_KEY] = time
        }
    }

    val isEasterEggUnlocked: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[EASTER_EGG_UNLOCKED_KEY] ?: false
        }

    suspend fun unlockEasterEgg() {
        context.dataStore.edit { preferences ->
            preferences[EASTER_EGG_UNLOCKED_KEY] = true
        }
    }

    val isTutorialCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TUTORIAL_COMPLETED_KEY] ?: false
        }

    suspend fun setTutorialCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TUTORIAL_COMPLETED_KEY] = completed
        }
    }

    val notificationSoundUri: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_SOUND_KEY]
        }

    suspend fun setNotificationSound(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_SOUND_KEY] = uri
        }
    }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[NAME_KEY] ?: "Usuário"
        }

    val appTheme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[APP_THEME_KEY] ?: "DARK" // Default is DARK
        }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[APP_THEME_KEY] = theme
        }
    }

    suspend fun saveAuthData(token: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[NAME_KEY] = name
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(NAME_KEY)
            preferences.remove(TUTORIAL_COMPLETED_KEY)
            preferences.remove(LAST_SYNC_TIME_KEY)
        }
    }
}
