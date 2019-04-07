package com.vladislav.currencytracker

import okhttp3.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DownloadManager {

    interface OnRequestFinishListener {
        fun onRequestFailure(exceptionMessage: String)
        fun onRequestSuccess(rates: List<DayExchangeRates>)
    }

    private val completedStreams = mutableMapOf<Int, InputStream>()
    private val urlsToLoad = mutableMapOf<Int, String>()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private lateinit var listener: OnRequestFinishListener
    private val requestCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            listener.onRequestFailure(e.localizedMessage)
        }

        override fun onResponse(call: Call, response: Response) {
            response.body()?.let {
                val tag: Int = call.request().tag() as Int
                completedStreams[tag] = it.byteStream()
                if (completedStreams.size == urlsToLoad.size) {
                    formResponse()
                }
            }
        }
    }

    private fun formResponse() {
        try {
            val processedData = processStreams()
            listener.onRequestSuccess(processedData)
        } catch (e: XmlPullParserException) {
            listener.onRequestFailure(e.localizedMessage)
        }
    }

    private fun formUrls() {
        val currentDate = Calendar.getInstance()
        val dateDayBefore = Calendar.getInstance()

        dateDayBefore.roll(Calendar.DAY_OF_MONTH, false)
        val dateDayAfter = Calendar.getInstance()
        dateDayAfter.roll(Calendar.DAY_OF_MONTH, true)

        val sdf = SimpleDateFormat("MM.dd.yyyy")
        val todayUrl = BASE_URL + sdf.format(currentDate.time)
        val tomorrowUrl = BASE_URL + sdf.format(dateDayAfter.time)
        val yesterdayUrl = BASE_URL + sdf.format(dateDayBefore.time)

        urlsToLoad[TODAY] = todayUrl
        urlsToLoad[TOMORROW] = tomorrowUrl
        urlsToLoad[YESTERDAY] = yesterdayUrl
    }

    private fun processStreams(): List<DayExchangeRates> {

        lateinit var tomorrowRates: DayExchangeRates
        val todayRates: DayExchangeRates = XmlParser(completedStreams[TODAY]!!).parse()
        val yesterdayRates: DayExchangeRates = XmlParser(completedStreams[YESTERDAY]!!).parse()

        try {
            tomorrowRates = XmlParser(completedStreams[TOMORROW]!!).parse()
        } catch (e: XmlPullParserException) {
            listener.onRequestFailure(e.localizedMessage)
            return listOf(yesterdayRates, todayRates)
        }

        return listOf(todayRates, tomorrowRates)
    }

    fun setOnDownloadCompleteListener(listener: OnRequestFinishListener) {
        this.listener = listener
    }

    fun downloadAll() {
        formUrls()
        for (item in urlsToLoad) {
            makeRequest(item.value, item.key)
        }
    }

    private fun makeRequest(url: String, tag: Int) {
        val request = Request.Builder()
            .tag(tag)
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