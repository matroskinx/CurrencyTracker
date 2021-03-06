package com.vladislav.currencytracker.View

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.vladislav.currencytracker.ViewModel.RatesViewModel
import com.vladislav.currencytracker.Model.DayExchangeRates
import com.vladislav.currencytracker.R
import com.vladislav.currencytracker.Adapters.RatesRecyclerAdapter
import com.vladislav.currencytracker.ViewModel.RatesViewModelFactory
import kotlinx.android.synthetic.main.fragment_currency.*
import java.lang.IllegalStateException

class CurrencyFragment : Fragment() {
    private lateinit var viewModel: RatesViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RatesRecyclerAdapter
    private var sharedPreferences: SharedPreferences? = null
    private var isLoaded: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_currency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity)
        rv_currency.layoutManager = linearLayoutManager
        rv_currency.setHasFixedSize(true)
        rv_currency.isNestedScrollingEnabled = false

        val exchangeRatesObserver = Observer<List<DayExchangeRates>> {
            first_date.text = it[0].date
            second_date.text = it[1].date
            adapter = RatesRecyclerAdapter(it)
            rv_currency.adapter = adapter
            isLoaded = true
            invalidateOptionsMenu(activity)
        }

        val isLoadingObserver = Observer<Boolean> {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

        val hasErrorObserver = Observer<Boolean> {
            if (it) {
                Toast.makeText(activity, "Failed to load rates", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.visibleRates.observe(this, exchangeRatesObserver)
        viewModel.isLoading.observe(this, isLoadingObserver)
        viewModel.hasError.observe(this, hasErrorObserver)

        sharedPreferences?.let {
            if (isNetworkConnected()) {
                viewModel.getRates()
            } else {
                Toast.makeText(activity, "No connectivity", Toast.LENGTH_LONG).show()
            }
        } ?: throw IllegalStateException("Unable to get preferences")
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (isLoaded) {
            menu.findItem(R.id.settingsFragment).isVisible = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel = ViewModelProviders.of(it, RatesViewModelFactory(it.getPreferences(Context.MODE_PRIVATE)))
                .get(RatesViewModel::class.java)
        } ?: throw IllegalStateException("Invalid activity")

        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsFragment) {
            viewModel.backupSettings()
        }
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }
}
