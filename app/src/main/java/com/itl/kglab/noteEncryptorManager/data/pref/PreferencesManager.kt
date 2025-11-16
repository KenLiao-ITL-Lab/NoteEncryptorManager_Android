package com.itl.kglab.noteEncryptorManager.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun clearLastVersionKeys() {
        dataStore.edit { pref ->
            pref.remove(SETTING_PREFIX)
            pref.remove(SETTING_SUFFIX)
            pref.remove(SETTING_SAMPLE_INDEX)
            pref.remove(SETTING_SAMPLE_SIZE)
        }
    }

    fun getSettingInfo(): Flow<SettingInfo> {
        return dataStore.data.map { pref ->
            val algorithm = pref[SETTING_ALGORITHM] ?: ""
            SettingInfo(
                algorithmName = algorithm,
            )
        }
    }

    suspend fun setSettingInfo(
        settingInfo: SettingInfo
    ) {
        dataStore.edit { pref ->
            pref[SETTING_ALGORITHM] = settingInfo.algorithmName
        }
    }

    companion object {
        val SETTING_ALGORITHM = stringPreferencesKey("setting_algorithm")
        val SETTING_PREFIX = stringPreferencesKey("setting_prefix")
        val SETTING_SUFFIX = stringPreferencesKey("setting_suffix")
        val SETTING_SAMPLE_INDEX = intPreferencesKey("setting_sample_index")
        val SETTING_SAMPLE_SIZE = intPreferencesKey("setting_sample_size")

    }

}