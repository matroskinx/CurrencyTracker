package com.vladislav.currencytracker

import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateRepository : DownloadManager.OnRequestFinishListener {

    val exchangeRates = mutableListOf<DayExchangeRates>()

    override fun onRequestSuccess(inputStreams: MutableList<InputStream>) {
        for (stream in inputStreams) {
            exchangeRates.add(XmlParser(stream).test())
        }
    }

    override fun onRequestFailure(exceptionMessage: String) {
    }

    fun getRates() {
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
    }

}