package com.vladislav.currencytracker.Model

data class CurrencyItem(
    val id: String,
    val numCode: String,
    val charCode: String,
    val scale: String,
    val name: String,
    val rate: String
)