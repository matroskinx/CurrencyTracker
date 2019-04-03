package com.vladislav.currencytracker

import android.content.res.XmlResourceParser
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val doc: XmlResourceParser = resources.getXml(R.xml.daily_rates)
            XmlParser(doc).test()
        }
    }
}
