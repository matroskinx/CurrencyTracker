package com.vladislav.currencytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.util.Xml
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ExchangeRateRepository
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        rv_currency.layoutManager = linearLayoutManager

        viewModel = ViewModelProviders.of(this).get(ExchangeRateRepository::class.java)

        val exchangeRatesObserver = Observer<List<DayExchangeRates>> {
            Toast.makeText(this, "Data downloaded", Toast.LENGTH_LONG).show()
            adapter = RecyclerAdapter(it)
            rv_currency.adapter = adapter
        }

        viewModel.exchangeRates.observe(this, exchangeRatesObserver)
        viewModel.getRates()
    }

    companion object {
        const val TAG = "MainActivityTag"
    }
}
