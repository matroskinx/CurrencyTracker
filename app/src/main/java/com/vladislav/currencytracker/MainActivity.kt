package com.vladislav.currencytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.util.Xml
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ExchangeRateRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(ExchangeRateRepository::class.java)

        val exchangeRatesObserver = Observer<MutableList<DayExchangeRates>> {
            Toast.makeText(this, "Data downloaded", Toast.LENGTH_LONG).show()
        }

        viewModel.exchangeRates.observe(this, exchangeRatesObserver)

        button.setOnClickListener {
            if (!viewModel.isLoading) {
                viewModel.getRates()
            }
        }
    }

    companion object {
        const val TAG = "MainActivityTag"
    }
}
