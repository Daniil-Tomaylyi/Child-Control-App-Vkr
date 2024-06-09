package com.example.childcontrol.devicetimesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.childcontrol.db.ChildDatabaseDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DeviceTimeSettingsViewModelFactory(
    private val mAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val childDatabase: ChildDatabaseDao
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceTimeSettingsViewModel::class.java)) {
            return DeviceTimeSettingsViewModel(mAuth, database, childDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}