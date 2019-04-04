package com.vladislav.currencytracker

import okhttp3.*
import java.io.IOException
import java.io.InputStream

class DownloadManager(val listener: OnRequestFinishListener) {

    interface OnRequestFinishListener {
        fun onRequestFailure(exceptionMessage: String)
        fun onRequestSuccess(inputStream: InputStream)
    }

    fun download(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onRequestFailure(e.localizedMessage)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.let {
                    listener.onRequestSuccess(it.byteStream())
                }
            }
        })
    }
}