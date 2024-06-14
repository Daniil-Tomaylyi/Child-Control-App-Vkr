package com.example.childcontrol.devicetimesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeviceTimeSettingsViewModelFactory(private val repository: DeviceTimeSettingsRepository) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceTimeSettingsViewModel::class.java)) {
            return DeviceTimeSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}