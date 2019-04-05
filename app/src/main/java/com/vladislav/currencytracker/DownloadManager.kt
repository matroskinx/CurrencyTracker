package com.vladislav.currencytracker

import okhttp3.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.XMLFormatter

class DownloadManager {

    interface OnRequestFinishListener {
        fun onRequestFailure(exceptionMessage: String)
        fun onRequestSuccess(rates: List<DayExchangeRates>)
    }

    private val completedStreams = mutableListOf<InputStream>()
    private val client = OkHttpClient()
    private val urlsQueue: ArrayDeque<String> = ArrayDeque()
    private lateinit var listener: OnRequestFinishListener

    private val requestCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            listener.onRequestFailure(e.localizedMessage)
        }


        override fun onResponse(call: Call, response: Response) {
            response.body()?.let {
                completedStreams.add(it.byteStream())
                if (urlsQueue.isEmpty()) {
                    listener.onRequestSuccess(processStreams())
                } else {
                    makeRequest(urlsQueue.pop())
                }
            }
        }
    }

    private fun formUrls()
    {
        val currentDate = Calendar.getInstance()

        val dateDayBefore = Calendar.getInstance()
        dateDayBefore.roll(Calendar.DAY_OF_MONTH, false)

        val dateDayAfter = Calendar.getInstance()
        dateDayAfter.roll(Calendar.DAY_OF_MONTH, true)

        val sdf = SimpleDateFormat("MM.dd.yyyy")

        val todayUrl = BASE_URL + sdf.format(currentDate.time)
        val tomorrowUrl = BASE_URL + sdf.format(dateDayAfter.time)
        val yesterdayUrl = BASE_URL + sdf.format(dateDayBefore.time)
        urlsQueue.addAll(listOf(todayUrl, tomorrowUrl, yesterdayUrl))
    }

    private fun processStreams(): List<DayExchangeRates>
    {
        lateinit var tomorrowRates: DayExchangeRates
        val todayRates: DayExchangeRates = XmlParser(completedStreams[TODAY]).test()
        val yesterdayRates: DayExchangeRates = XmlParser(completedStreams[YESTERDAY]).test()

        try {
            tomorrowRates = XmlParser(completedStreams[TOMORROW]).test()
        } catch (e: XmlPullParserException) {
            return listOf(yesterdayRates, todayRates)
        }

        return listOf(todayRates, tomorrowRates)
    }

    fun setOnDownloadCompleteListener(listener: OnRequestFinishListener) {
        this.listener = listener
    }

    fun downloadAll() {
        formUrls()
        if (!urlsQueue.isEmpty()) {
            makeRequest(urlsQueue.pop())
        }
    }

    private fun makeRequest(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(requestCallback)
    }

    companion object {
        const val BASE_URL = "http://www.nbrb.by/Services/XmlExRates.aspx?ondate="
        const val TODAY = 0
        const val TOMORROW = 1
        const val YESTERDAY = 2
    }
}