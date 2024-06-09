package com.example.childcontrol.parentmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.childcontrol.headparent.HeadParentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ParentMapViewModelFactory(
    private val mAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParentMapViewModel::class.java)) {
            return ParentMapViewModel(mAuth, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}