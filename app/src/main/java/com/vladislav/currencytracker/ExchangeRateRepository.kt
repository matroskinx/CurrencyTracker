package com.vladislav.currencytracker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExchangeRateRepository : DownloadManager.OnRequestFinishListener, ViewModel() {

    val exchangeRates = MutableLiveData<List<DayExchangeRates>>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    override fun onRequestSuccess(rates: List<DayExchangeRates>) {
        isLoading.postValue(false)
        exchangeRates.postValue(rates)
    }

    override fun onRequestFailure(exceptionMessage: String) {
        Log.d(TAG, "Failed to download data")
    }

    fun getRates() {
        isLoading.value = true
        val downloadManager = DownloadManager()
        downloadManager.setOnDownloadCompleteListener(this)
        downloadManager.downloadAll()
    }

    companion object {
        const val TAG = "ExchangeRateRepository"
    }

}