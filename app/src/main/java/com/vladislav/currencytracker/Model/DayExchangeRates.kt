package com.vladislav.currencytracker.Model

import com.vladislav.currencytracker.Model.CurrencyItem

data class DayExchangeRates(val exchangeRates: MutableList<CurrencyItem>, val date: String)