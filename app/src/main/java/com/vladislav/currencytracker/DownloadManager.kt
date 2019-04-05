package com.vladislav.currencytracker

import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.*

class DownloadManager {

    interface OnRequestFinishListener {
        fun onRequestFailure(exceptionMessage: String)
        fun onRequestSuccess(inputStreams: MutableList<InputStream>)
    }

    private val completedStreams = mutableListOf<InputStream>()
    private val client = OkHttpClient()
    private lateinit var urlsQueue: ArrayDeque<String>
    private lateinit var listener: OnRequestFinishListener

    private val requestCallback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            listener.onRequestFailure(e.localizedMessage)
        }


        override fun onResponse(call: Call, response: Response) {
            response.body()?.let {
                completedStreams.add(it.byteStream())
                if (urlsQueue.isEmpty()) {
                    listener.onRequestSuccess(completedStreams)
                } else {
                    downloadAll()
                }
            }
        }
    }

    fun setupDownloader(urls: List<String>, listener: OnRequestFinishListener) {
        this.urlsQueue = ArrayDeque(urls)
        this.listener = listener
    }

    fun downloadAll() {
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
}