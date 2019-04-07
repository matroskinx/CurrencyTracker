package com.vladislav.currencytracker

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class ExchangeRateRepository : DownloadManager.OnRequestFinishListener, ViewModel() {

    var exchangeRates = listOf<DayExchangeRates>()
    val visibleRates = MutableLiveData<List<DayExchangeRates>>()
    var settingsList = mutableListOf<SettingsItem>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var hasError: MutableLiveData<Boolean> = MutableLiveData(false)

    private var isLoaded = false
    private var jsonBackupSettings: String = ""
    private var jsonBackupExchangeRates: String = ""
    private val downloadManager = DownloadManager()
    private lateinit var settingsManager: SettingsManager

    override fun onRequestSuccess(rates: List<DayExchangeRates>) {
        isLoading.postValue(false)
        isLoaded = true
        exchangeRates = rates
        settingsList = settingsManager.readSettings(rates[0])
        getVisibleRates(rates)
    }

    override fun onRequestFailure(exceptionMessage: String) {
        isLoading.postValue(false)
        isLoaded = false
        hasError.postValue(true)
        Log.d(TAG, "Failed to download data: $exceptionMessage")
    }

    fun getRates(sharedPreferences: SharedPreferences) {
        if (isLoaded || isLoading.value == true) {
            return
        }
        isLoading.value = true
        settingsManager = SettingsManager(sharedPreferences)
        downloadManager.setOnDownloadCompleteListener(this)
        downloadManager.downloadAll()
    }

    private fun getVisibleRates(rates: List<DayExchangeRates>) {
        val configuredList = sortVisibleRates(rates, settingsList)
        visibleRates.postValue(configuredList)
    }

    private fun sortExchangeRates() {
        val firstRatesFull = exchangeRates[0].exchangeRates
        val secondRatesFull = exchangeRates[1].exchangeRates
        val firstRatesSorted: MutableList<CurrencyItem> = mutableListOf()
        val secondRatesSorted: MutableList<CurrencyItem> = mutableListOf()
        for (item in settingsList) {
            val rate = firstRatesFull.find { it.id == item.id }
            rate?.let {
                firstRatesSorted.add(rate)
            }
            val rate2 = secondRatesFull.find { it.id == item.id }
            rate2?.let {
                secondRatesSorted.add(rate2)
            }
        }

        val firstDayExchangeRates = DayExchangeRates(firstRatesSorted, exchangeRates[0].date)
        val secondDayExchangeRates = DayExchangeRates(secondRatesSorted, exchangeRates[1].date)
        exchangeRates = listOf(firstDayExchangeRates, secondDayExchangeRates)
    }

    private fun sortVisibleRates(
        rates: List<DayExchangeRates>,
        settings: MutableList<SettingsItem>
    ): MutableList<DayExchangeRates> {
        val firstRatesFull: MutableList<CurrencyItem> = rates[0].exchangeRates
        val secondRatesFull: MutableList<CurrencyItem> = rates[1].exchangeRates
        val firstRatesSorted: MutableList<CurrencyItem> = mutableListOf()
        val secondRatesSorted: MutableList<CurrencyItem> = mutableListOf()

        val selectedSettings = settings.filter { it.isSelected }

        for (item in selectedSettings) {
            val rate = firstRatesFull.find { it.id == item.id}
            rate?.let {
                firstRatesSorted.add(rate)
            }

            val rate2 = secondRatesFull.find { it.id == item.id }
            rate2?.let {
                secondRatesSorted.add(rate2)
            }
        }

        val firstDayExchangeRates = DayExchangeRates(firstRatesSorted, rates[0].date)
        val secondDayExchangeRates = DayExchangeRates(secondRatesSorted, rates[1].date)
        return mutableListOf(firstDayExchangeRates, secondDayExchangeRates)
    }

    fun visibilityChanged(position: Int) {
        settingsList[position].isSelected = !settingsList[position].isSelected
    }

    fun positionChanged(fromPosition: Int, toPosition: Int) {
        Collections.swap(settingsList, fromPosition, toPosition)
    }

    fun backupSettings() {
        val gson = Gson()
        jsonBackupSettings = gson.toJson(settingsList)
        jsonBackupExchangeRates = gson.toJson(exchangeRates)
    }

    fun saveSettingsChanges() {
        sortExchangeRates()
        getVisibleRates(exchangeRates)
        settingsManager.saveSettings(settingsList)
    }

    fun discardSettingsChanges() {
        val gson = Gson()
        val ratesType = object : TypeToken<List<DayExchangeRates>>() {}.type
        val settingsType = object : TypeToken<MutableList<SettingsItem>>() {}.type
        exchangeRates = gson.fromJson(jsonBackupExchangeRates, ratesType)
        settingsList = gson.fromJson(jsonBackupSettings, settingsType)
    }

    companion object {
        const val TAG = "ExchangeRateRepository"
    }

}