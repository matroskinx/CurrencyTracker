package com.vladislav.currencytracker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.callback.Callback


class ExchangeRateRepository : DownloadManager.OnRequestFinishListener, ViewModel() {

    val exchangeRates = MutableLiveData<MutableList<DayExchangeRates>>()
    var isLoading: Boolean = false

    override fun onRequestSuccess(inputStreams: MutableList<InputStream>) {
        val ratesList = mutableListOf<DayExchangeRates>()
        for (stream in inputStreams) {
            ratesList.add(XmlParser(stream).test())
        }
        isLoading = false
        exchangeRates.postValue(ratesList)
    }

    override fun onRequestFailure(exceptionMessage: String) {
        Log.d(TAG, "Failed to download data")
    }

    fun getRates() {
        //TODO(Move this part to DownloadManager(Model))
        isLoading = true
        val currentDate = Calendar.getInstance()

        val dateDayBefore = Calendar.getInstance()
        dateDayBefore.roll(Calendar.DAY_OF_MONTH, false)

        val dateDayAfter = Calendar.getInstance()
        dateDayAfter.roll(Calendar.DAY_OF_MONTH, true)

        val sdf = SimpleDateFormat("MM.dd.yyyy")

        val todayUrl = BASE_URL + sdf.format(currentDate.time)
        val tomorrowUrl = BASE_URL + sdf.format(dateDayAfter.time)
        val yesterdayUrl = BASE_URL + sdf.format(dateDayBefore.time)

        val downloadManager = DownloadManager()
        downloadManager.setupDownloader(listOf(todayUrl, tomorrowUrl, yesterdayUrl), this)
        downloadManager.downloadAll()
    }

    companion object {
        const val BASE_URL = "http://www.nbrb.by/Services/XmlExRates.aspx?ondate="
        const val TAG = "ExchangeRateRepository"
    }

}