package com.vladislav.currencytracker

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.*

class DownloadManager() {

    interface OnRequestFinishListener {
        fun onRequestFailure(exceptionMessage: String)
        fun onRequestSuccess(inputStreams: MutableList<InputStream>)
    }

    private var completedRequests = 0
    private val completedStreams = mutableListOf<InputStream>()
    private val client = OkHttpClient()
    private lateinit var urls: List<String>
    private lateinit var listener: OnRequestFinishListener

    private val requestCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            listener.onRequestFailure(e.localizedMessage)
        }

        override fun onResponse(call: Call, response: Response) {
            response.body()?.let {
                completedStreams.add(it.byteStream())
                completedRequests += 1
                if (urls.size == completedRequests) {
                    listener.onRequestSuccess(completedStreams)
                }
            }
        }
    }

    fun setupDownloader(urls: List<String>, listener: OnRequestFinishListener) {
        this.urls = urls
        this.listener = listener
    }

    fun downloadAll() {
        for (url in urls) {
            makeRequest(url)
        }
    }

    private fun makeRequest(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(requestCallback)
    }
}