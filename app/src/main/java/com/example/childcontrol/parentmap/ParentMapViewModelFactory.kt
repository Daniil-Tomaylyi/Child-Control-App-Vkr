package com.example.childcontrol.parentmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ParentMapViewModelFactory(private val repository: ParentMapRepository) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParentMapViewModel::class.java)) {
            return ParentMapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}