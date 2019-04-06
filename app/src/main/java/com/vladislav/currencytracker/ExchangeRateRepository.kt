package com.vladislav.currencytracker

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExchangeRateRepository : DownloadManager.OnRequestFinishListener, ViewModel() {

    var exchangeRates = listOf<DayExchangeRates>()
    val visibleRates = MutableLiveData<List<DayExchangeRates>>()
    var settingsList = mutableListOf<SettingsItem>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    override fun onRequestSuccess(rates: List<DayExchangeRates>) {
        isLoading.postValue(false)
        exchangeRates = rates
        getVisibleRates(rates)
    }

    override fun onRequestFailure(exceptionMessage: String) {
        isLoading.postValue(false)
        Log.d(TAG, "Failed to download data: $exceptionMessage")
    }

    fun getRates(sharedPreferences: SharedPreferences) {
        isLoading.value = true
        settingsList = SettingsManager(sharedPreferences).readSettings()
        val downloadManager = DownloadManager()
        downloadManager.setOnDownloadCompleteListener(this)
        downloadManager.downloadAll()
    }

    private fun getVisibleRates(rates: List<DayExchangeRates>) {
        val firstRatesFull: MutableList<CurrencyItem> = rates[0].exchangeRates
        val secondRatesFull: MutableList<CurrencyItem> = rates[1].exchangeRates
        val firstRatesSorted: MutableList<CurrencyItem> = mutableListOf()
        val secondRatesSorted: MutableList<CurrencyItem> = mutableListOf()

        //TODO optimize search
        for (item in settingsList) {
            val rate = firstRatesFull.find { it.id == item.id && item.isSelected }
            rate?.let {
                firstRatesSorted.add(rate)
            }

            val rate2 = secondRatesFull.find { it.id == item.id && item.isSelected }
            rate2?.let {
                secondRatesSorted.add(rate2)
            }
        }

        val firstDayExchangeRates = DayExchangeRates(firstRatesSorted, rates[0].date)
        val secondDayExchangeRates = DayExchangeRates(secondRatesSorted, rates[1].date)
        val configuredList = listOf(firstDayExchangeRates, secondDayExchangeRates)
        visibleRates.postValue(configuredList)
    }

    companion object {
        const val TAG = "ExchangeRateRepository"
    }

}