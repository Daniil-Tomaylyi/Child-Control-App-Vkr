package com.example.childcontrol.applist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AppListViewModelFactory(
    private val repository: AppListRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppListViewModel::class.java)) {
            return AppListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}