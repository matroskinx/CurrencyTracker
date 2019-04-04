package com.vladislav.currencytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import java.io.InputStream


class MainActivity : AppCompatActivity(), DownloadManager.OnRequestFinishListener {
    override fun onRequestFailure(exceptionMessage: String) {
        Log.d(TAG, "request:fail")
    }

    override fun onRequestSuccess(inputStream: InputStream) {
        XmlParser(inputStream).test()
        Log.d(TAG, "request:ok")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            DownloadManager(this).download("http://www.nbrb.by/Services/XmlExRates.aspx?ondate=04.04.2019")
        }
    }

    companion object {
        const val TAG = "MainActivityTag"
    }
}
