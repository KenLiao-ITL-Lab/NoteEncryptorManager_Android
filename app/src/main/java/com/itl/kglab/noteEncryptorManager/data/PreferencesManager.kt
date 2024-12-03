package com.itl.kglab.noteEncryptorManager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.itl.kglab.noteEncryptorManager.tools.HashAlgorithmType
import com.itl.kglab.noteEncryptorManager.tools.SettingInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {

    fun getSettingInfo(): Flow<SettingInfo> {
        return dataStore.data.map { pref ->
            val algorithm = HashAlgorithmType.getTypeByName(pref[SETTING_ALGORITHM] ?: "")
            SettingInfo(
                algorithm = algorithm,
                prefixText = pref[SETTING_PREFIX] ?: "",
                suffixText = pref[SETTING_SUFFIX] ?: "",
                sampleIndex = pref[SETTING_SAMPLE_INDEX] ?: 0,
                samplingSize = pref[SETTING_SAMPLE_SIZE] ?: 0
            )
        }
    }

    suspend fun setSettingInfo(
        settingInfo: SettingInfo
    ) {
        dataStore.edit { pref ->
            pref[SETTING_ALGORITHM] = settingInfo.algorithm.algorithmName
            pref[SETTING_PREFIX] = settingInfo.prefixText
            pref[SETTING_SUFFIX] = settingInfo.suffixText
            pref[SETTING_SAMPLE_INDEX] = settingInfo.sampleIndex
            pref[SETTING_SAMPLE_SIZE] = settingInfo.samplingSize
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