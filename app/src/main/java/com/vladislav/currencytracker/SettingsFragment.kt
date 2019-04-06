package com.vladislav.currencytracker

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_settings.*
import java.lang.IllegalStateException

class SettingsFragment : Fragment() {

    private lateinit var viewModel: ExchangeRateRepository
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SettingsRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it).get(ExchangeRateRepository::class.java)
        } ?: throw IllegalStateException("Invalid activity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(activity)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }
}
