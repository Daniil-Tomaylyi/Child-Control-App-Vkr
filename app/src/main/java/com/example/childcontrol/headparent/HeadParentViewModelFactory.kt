package com.example.childcontrol.headparent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.childcontrol.headchild.HeadChildViewModel
import com.firebase.geofire.GeoFire
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HeadParentViewModelFactory(
    private val mAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeadParentViewModel::class.java)) {
            return HeadParentViewModel(mAuth, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}