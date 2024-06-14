package com.example.childcontrol.reg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class RegViewModelFactory(private val repository: RegRepository) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegViewModel::class.java)) {
            return RegViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}