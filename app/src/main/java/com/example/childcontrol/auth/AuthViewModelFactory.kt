package com.example.childcontrol.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.childcontrol.db.ChildDatabaseDao
import com.example.childcontrol.reg.RegViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModelFactory(
    private val mAuth: FirebaseAuth
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(mAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}