package com.vladislav.currencytracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_row_settings.view.*
import java.util.*

class SettingsRecyclerAdapter(
    private val rate: DayExchangeRates,
    private val settings: MutableList<SettingsItem>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<SettingsRecyclerAdapter.SettingsHolder>(), ItemTouchHelperCallback.ItemTouchHelperAdapter {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(rate.exchangeRates, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(rate.exchangeRates, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsRecyclerAdapter.SettingsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = inflater.inflate(R.layout.rv_row_settings, parent, false)
        return SettingsHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(holder: SettingsRecyclerAdapter.SettingsHolder, position: Int) {
        val currencyItem: CurrencyItem = rate.exchangeRates[position]
        val isSelected = settings[position].isSelected
        holder.bindSettingsItems(currencyItem, isSelected, listener)
    }

    class SettingsHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view = v
        private var currencyItem: CurrencyItem? = null
        private var isSelected: Boolean? = null

        fun bindSettingsItems(currencyItem: CurrencyItem, isSelected: Boolean, listener: OnItemClickListener) {
            this.currencyItem = currencyItem
            this.isSelected = isSelected
            view.rv_switch.isChecked = isSelected
            view.rv_set_char_code.text = currencyItem.charCode
            view.rv_set_scale.text = "${currencyItem.scale} ${currencyItem.name}"
            view.rv_switch.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(adapterPosition)
                }
            }
        }
    }
}