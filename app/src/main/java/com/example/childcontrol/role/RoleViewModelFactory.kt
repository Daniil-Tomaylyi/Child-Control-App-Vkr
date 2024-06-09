package com.example.childcontrol.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RoleViewModelFactory(
    private val mAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoleViewModel::class.java)) {
            return RoleViewModel(mAuth, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}