package com.ddenfi.mystoryapp.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreUtil {
    const val DATA_STORE_NAME = "USER_DATASTORE"
    val LOGIN_STATE_KEY = booleanPreferencesKey("LOGIN_STATE_KEY")
    val TOKEN_AUTH_KEY = stringPreferencesKey("TOKEN_AUTH_KEY")
}