package com.vladislav.currencytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SettingsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linearLayoutManager = LinearLayoutManager(this)
        rv_settings.layoutManager = linearLayoutManager
        rv_settings.setHasFixedSize(true)

        val currencyItem = CurrencyItem("1", "2", "usd", "4", "test", "2")
        val currencyItem2 = CurrencyItem("2", "3", "eur", "5", "test", "5")

        val dayExchangeRates = DayExchangeRates(mutableListOf(currencyItem, currencyItem2), "06.04/2019")
        val sitem = SettingsItem("1", true)
        val sitem2 = SettingsItem("2", false)
        val boolList = mutableListOf(sitem, sitem2)

        adapter = SettingsRecyclerAdapter(dayExchangeRates, boolList)
        rv_settings.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
