package com.vladislav.currencytracker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ExchangeRateRepository : DownloadManager.OnRequestFinishListener, ViewModel() {

    val exchangeRates = MutableLiveData<List<DayExchangeRates>>()
    var isLoading: Boolean = false

    override fun onRequestSuccess(rates: List<DayExchangeRates>) {
        isLoading = false
        exchangeRates.postValue(rates)
    }

    override fun onRequestFailure(exceptionMessage: String) {
        Log.d(TAG, "Failed to download data")
    }

    fun getRates() {
        isLoading = true
        val downloadManager = DownloadManager()
        downloadManager.setOnDownloadCompleteListener(this)
        downloadManager.downloadAll()
    }

    companion object {
        const val TAG = "ExchangeRateRepository"
    }

}