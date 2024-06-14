package com.example.childcontrol.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class RoleViewModelFactory(
    private val repository: RoleRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoleViewModel::class.java)) {
            return RoleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}