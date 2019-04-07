package com.vladislav.currencytracker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SettingsManager(private val prefs: SharedPreferences) {

    fun saveSettings(settingsList: MutableList<SettingsItem>) {
        val gson = Gson()
        val jsonSettingsList: String = gson.toJson(settingsList)
        with(prefs.edit()) {
            putString(PREF_KEY, jsonSettingsList)
            apply()
        }
    }

    private fun getBasicSettings(rates: DayExchangeRates): MutableList<SettingsItem> {
        val basicSettings = mutableListOf<SettingsItem>()
        for (rate in rates.exchangeRates) {
            if (rate.id == USD_ID || rate.id == EUR_ID || rate.id == RUB_ID) {
                basicSettings.add(SettingsItem(rate.id, true))
            } else {
                basicSettings.add(SettingsItem(rate.id, false))
            }
        }
        return basicSettings
    }


    fun readSettings(rates: DayExchangeRates): MutableList<SettingsItem> {
        prefs.getString(PREF_KEY, null)?.let {
            val gson = Gson()
            val turnsType = object : TypeToken<MutableList<SettingsItem>>() {}.type
            return gson.fromJson(it, turnsType)
        } ?: return getBasicSettings(rates)
    }

    companion object {
        private const val USD_ID = "145"
        private const val EUR_ID = "292"
        private const val RUB_ID = "298"
        private const val PREF_KEY = "Settings_key"
    }
}