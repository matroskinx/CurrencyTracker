package com.vladislav.currencytracker.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vladislav.currencytracker.Model.CurrencyItem
import com.vladislav.currencytracker.Model.DayExchangeRates
import com.vladislav.currencytracker.R
import kotlinx.android.synthetic.main.rv_row_currency.view.*


class RatesRecyclerAdapter(private val rates: List<DayExchangeRates>) :
    RecyclerView.Adapter<RatesRecyclerAdapter.ExchangeRatesHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRatesHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = inflater.inflate(R.layout.rv_row_currency, parent, false)
        return ExchangeRatesHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return rates[0].exchangeRates.size
    }

    override fun onBindViewHolder(holder: ExchangeRatesHolder, position: Int) {
        val firstItem: CurrencyItem = rates[0].exchangeRates[position]
        val secondItem: CurrencyItem = rates[1].exchangeRates[position]
        holder.bindCurrencyItems(firstItem, secondItem)
    }

    class ExchangeRatesHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view = v
        private var firstCurrencyItem: CurrencyItem? = null
        private var secondCurrencyItem: CurrencyItem? = null

        fun bindCurrencyItems(firstItem: CurrencyItem, secondItem: CurrencyItem) {
            this.firstCurrencyItem = firstItem
            this.secondCurrencyItem = secondItem
            view.rv_char_code.text = firstItem.charCode
            view.rv_scale.text = "${firstItem.scale} ${firstItem.name}"
            view.rv_frist_rate.text = firstItem.rate
            view.rv_second_rate.text = secondItem.rate
        }
    }
}