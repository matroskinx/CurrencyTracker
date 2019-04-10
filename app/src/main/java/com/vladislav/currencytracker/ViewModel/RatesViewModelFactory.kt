package com.vladislav.currencytracker.ViewModel

import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RatesViewModelFactory(private val sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RatesViewModel(sharedPreferences) as T
    }
}