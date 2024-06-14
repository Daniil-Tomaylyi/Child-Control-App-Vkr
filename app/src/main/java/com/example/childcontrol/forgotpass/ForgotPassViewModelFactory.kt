package com.example.childcontrol.forgotpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ForgotPassViewModelFactory(
    private val repository: ForgotPassRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPassViewModel::class.java)) {
            return ForgotPassViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}