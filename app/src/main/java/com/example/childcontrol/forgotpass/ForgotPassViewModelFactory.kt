package com.example.childcontrol.forgotpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.firebase.auth.FirebaseAuth


class ForgotPassViewModelFactory(
    private val mAuth: FirebaseAuth
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPassViewModel::class.java)) {
            return ForgotPassViewModel(mAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}