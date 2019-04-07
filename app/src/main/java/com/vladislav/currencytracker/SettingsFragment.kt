package com.vladislav.currencytracker

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_settings.*
import java.lang.IllegalStateException

class SettingsFragment : Fragment() {

    private lateinit var viewModel: ExchangeRateRepository
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SettingsRecyclerAdapter

    private val itemClickListener = object : SettingsRecyclerAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            viewModel.visibilityChanged(position)
        }
    }
    private val itemDragListener = object : SettingsRecyclerAdapter.OnItemDragListener {
        override fun onItemDrag(fromPosition: Int, toPosition: Int) {
            viewModel.positionChanged(fromPosition, toPosition)
        }
    }

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

        adapter = SettingsRecyclerAdapter(
            viewModel.exchangeRates[0],
            viewModel.settingsList,
            itemClickListener,
            itemDragListener
        )
        rv_settings.adapter = adapter

        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rv_settings)
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
