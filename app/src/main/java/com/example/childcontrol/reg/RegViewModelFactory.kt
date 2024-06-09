package com.example.childcontrol.reg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.childcontrol.db.ChildDatabaseDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegViewModelFactory(
    private val mAuth: FirebaseAuth, private val database: FirebaseDatabase
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegViewModel::class.java)) {
            return RegViewModel(mAuth, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}