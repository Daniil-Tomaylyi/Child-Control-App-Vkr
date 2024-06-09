package com.example.childcontrol.headchild

import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HeadChildViewModelFactory(
    private val repository: HeadChildFragmentRepository,
    private val packageManager: PackageManager,
    private val usageStatsManager: UsageStatsManager
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeadChildViewModel::class.java)) {
            return HeadChildViewModel(repository, packageManager, usageStatsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}