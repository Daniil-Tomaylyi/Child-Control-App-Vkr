package com.example.childcontrol.tittle

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TittleViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TittleViewModel::class.java)) {
            return TittleViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}