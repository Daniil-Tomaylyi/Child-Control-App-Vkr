package com.example.childcontrol.addchild

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AddChildViewModelFactory(
    private val repository: AddChildRepository
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddChildViewModel::class.java)) {
            return AddChildViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}