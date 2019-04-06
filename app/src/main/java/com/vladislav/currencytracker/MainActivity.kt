package com.vladislav.currencytracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.util.Xml
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        rv_currency.setHasFixedSize(true)

        viewModel = ViewModelProviders.of(this).get(ExchangeRateRepository::class.java)

        val exchangeRatesObserver = Observer<List<DayExchangeRates>> {
            Toast.makeText(this, "Data downloaded", Toast.LENGTH_LONG).show()
            first_date.text = it[0].date
            second_date.text = it[1].date
            adapter = RecyclerAdapter(it)
            rv_currency.adapter = adapter
        }

        val isLoadingObserver = Observer<Boolean> {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

        viewModel.exchangeRates.observe(this, exchangeRatesObserver)
        viewModel.isLoading.observe(this, isLoadingObserver)
        viewModel.getRates()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_menu_item -> {
                Toast.makeText(this, "Open settings", Toast.LENGTH_LONG).show()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "MainActivityTag"
    }
}
