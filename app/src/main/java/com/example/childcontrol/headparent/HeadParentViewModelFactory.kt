package com.example.childcontrol.headparent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class HeadParentViewModelFactory(private val repository: HeadParentRepository) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeadParentViewModel::class.java)) {
            return HeadParentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}