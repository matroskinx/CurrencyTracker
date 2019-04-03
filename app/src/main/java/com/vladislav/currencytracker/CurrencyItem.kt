package com.vladislav.currencytracker

data class CurrencyItem(
    val id: String,
    val numCode: String,
    val charCode: String,
    val scale: String,
    val name: String,
    val rate: String
)