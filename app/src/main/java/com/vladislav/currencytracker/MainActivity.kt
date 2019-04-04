package com.vladislav.currencytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.util.Xml
import java.io.InputStream


class MainActivity : AppCompatActivity(), DownloadManager.OnRequestFinishListener {
    override fun onRequestFailure(exceptionMessage: String) {
        Log.d(TAG, "request:fail")
    }

    override fun onRequestSuccess(inputStreams: MutableList<InputStream>) {

        Log.d(TAG, "request:ok")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            ExchangeRateRepository().getRates()
        }
    }

    companion object {
        const val TAG = "MainActivityTag"
    }
}
