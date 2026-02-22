package io.example.wedzy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wedzy_preferences")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val WEDDING_PROFILE_ID = longPreferencesKey("wedding_profile_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val REMINDER_DAYS_BEFORE = stringPreferencesKey("reminder_days_before")
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
        // Note: HERO_BACKGROUND_IMAGE is now user-specific, created dynamically
    }
    
    private fun getUserSpecificHeroImageKey(): Preferences.Key<String> {
        val userId = getCurrentUserId()
        return stringPreferencesKey("hero_background_image_$userId")
    }
    
    private fun getCurrentUserId(): String {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (firebaseUser != null) {
            firebaseUser.uid
        } else {
            val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            prefs.getString("anonymous_user_id", null) ?: "default"
        }
    }
    
    private fun getUserSpecificOnboardingKey(): Preferences.Key<Boolean> {
        val userId = getCurrentUserId()
        return booleanPreferencesKey("onboarding_completed_$userId")
    }
    
    private fun getUserSpecificProfileIdKey(): Preferences.Key<Long> {
        val userId = getCurrentUserId()
        return longPreferencesKey("wedding_profile_id_$userId")
    }
    
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[getUserSpecificOnboardingKey()] ?: false
    }
    
    val weddingProfileId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[getUserSpecificProfileIdKey()]
    }
    
    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: "system"
    }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }
    
    val selectedCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_CURRENCY] ?: "USD"
    }
    
    val heroBackgroundImage: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[getUserSpecificHeroImageKey()]
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[getUserSpecificOnboardingKey()] = completed
        }
    }
    
    suspend fun setWeddingProfileId(id: Long) {
        context.dataStore.edit { preferences ->
            preferences[getUserSpecificProfileIdKey()] = id
        }
    }
    
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun setSelectedCurrency(currencyCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_CURRENCY] = currencyCode
        }
    }
    
    suspend fun setHeroBackgroundImage(imageUri: String?) {
        context.dataStore.edit { preferences ->
            val key = getUserSpecificHeroImageKey()
            if (imageUri != null) {
                preferences[key] = imageUri
            } else {
                preferences.remove(key)
            }
        }
    }
    
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
