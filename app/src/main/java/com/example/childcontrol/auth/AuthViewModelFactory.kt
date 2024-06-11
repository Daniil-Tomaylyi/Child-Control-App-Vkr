package com.example.childcontrol.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.childcontrol.db.ChildDatabaseDao
import com.example.childcontrol.reg.RegViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModelFactory(
    private val mAuth: FirebaseAuth, private val database: FirebaseDatabase
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(mAuth,database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}