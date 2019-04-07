package com.vladislav.currencytracker.ViewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vladislav.currencytracker.Model.CurrencyItem
import com.vladislav.currencytracker.Model.DayExchangeRates
import com.vladislav.currencytracker.Model.SettingsItem
import com.vladislav.currencytracker.Repository.RatesRemoteRepository
import com.vladislav.currencytracker.Repository.SettingsRepository
import java.util.*


class RatesViewModel : RatesRemoteRepository.OnRequestFinishListener, ViewModel() {

    var exchangeRates = listOf<DayExchangeRates>()
    val visibleRates = MutableLiveData<List<DayExchangeRates>>()
    var settingsList = mutableListOf<SettingsItem>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var hasError: MutableLiveData<Boolean> = MutableLiveData(false)

    private var isLoaded = false
    private var jsonBackupSettings: String = ""
    private var jsonBackupExchangeRates: String = ""
    private val downloadManager = RatesRemoteRepository()
    private lateinit var settingsRepository: SettingsRepository

    override fun onRequestSuccess(rates: List<DayExchangeRates>) {
        isLoading.postValue(false)
        isLoaded = true
        exchangeRates = rates
        settingsList = settingsRepository.readSettings(rates[0])
        getVisibleRates()
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
        settingsRepository = SettingsRepository(sharedPreferences)
        downloadManager.setOnDownloadCompleteListener(this)
        downloadManager.downloadAll()
    }

    private fun getVisibleRates() {
        val configuredList = sortVisibleRates()
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

        val firstDayRates = DayExchangeRates(firstRatesSorted, exchangeRates[0].date)
        val secondDayRates = DayExchangeRates(secondRatesSorted, exchangeRates[1].date)
        exchangeRates = listOf(firstDayRates, secondDayRates)
    }

    private fun sortVisibleRates(): MutableList<DayExchangeRates> {
        val firstRatesFull = exchangeRates[0].exchangeRates
        val secondRatesFull = exchangeRates[1].exchangeRates
        val firstRatesSorted: MutableList<CurrencyItem> = mutableListOf()
        val secondRatesSorted: MutableList<CurrencyItem> = mutableListOf()

        val selectedSettings = settingsList.filter { it.isSelected }

        for (item in selectedSettings) {
            val rate = firstRatesFull.find { it.id == item.id }
            rate?.let {
                firstRatesSorted.add(rate)
            }

            val rate2 = secondRatesFull.find { it.id == item.id }
            rate2?.let {
                secondRatesSorted.add(rate2)
            }
        }

        val firstDayRates = DayExchangeRates(firstRatesSorted, exchangeRates[0].date)
        val secondDayRates = DayExchangeRates(secondRatesSorted, exchangeRates[1].date)
        return mutableListOf(firstDayRates, secondDayRates)
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
        getVisibleRates()
        settingsRepository.saveSettings(settingsList)
    }

    fun discardSettingsChanges() {
        val gson = Gson()
        val ratesType = object : TypeToken<List<DayExchangeRates>>() {}.type
        val settingsType = object : TypeToken<MutableList<SettingsItem>>() {}.type
        exchangeRates = gson.fromJson(jsonBackupExchangeRates, ratesType)
        settingsList = gson.fromJson(jsonBackupSettings, settingsType)
    }

    companion object {
        const val TAG = "RatesViewModel"
    }

}