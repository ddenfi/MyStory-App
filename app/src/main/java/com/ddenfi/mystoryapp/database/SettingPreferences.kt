package com.ddenfi.mystoryapp.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.ddenfi.mystoryapp.utils.DataStoreUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingPreferences(private val context:Context) {

    private val Context.userPreferenceDataStore: DataStore<Preferences> by preferencesDataStore(
        name = DataStoreUtil.DATA_STORE_NAME
    )

    suspend fun saveLoginState(isLogin:Boolean){
        context.userPreferenceDataStore.edit {
            it[DataStoreUtil.LOGIN_STATE_KEY] = isLogin
        }
    }

    suspend fun saveTokenAuth(tokenAuth:String){
        context.userPreferenceDataStore.edit {
            it[DataStoreUtil.TOKEN_AUTH_KEY] = tokenAuth
        }
    }

    fun getLoginState(): Flow<Boolean> =
        context.userPreferenceDataStore.data.map {
            it[DataStoreUtil.LOGIN_STATE_KEY] ?: false
        }

    fun getTokenAuth(): Flow<String> =
        context.userPreferenceDataStore.data.map {
            it[DataStoreUtil.TOKEN_AUTH_KEY] ?: ""
        }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(context: Context): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}