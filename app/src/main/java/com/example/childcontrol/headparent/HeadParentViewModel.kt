package com.example.childcontrol.headparent


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcontrol.addchild.ChildInfo
import com.example.childcontrol.headchild.AppInfo
import com.example.childcontrol.headchild.DeviceUsage
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HeadParentViewModel(private val repository: HeadParentRepository) :
    ViewModel() {

    // LiveData для хранения и обновления геолокации ребенка
    private var _locationChild = MutableLiveData<GeoLocation?>()
    val locationChild: LiveData<GeoLocation?> get() = _locationChild

    // LiveData для хранения и обновления списка приложений
    private var _appList = MutableLiveData<List<AppInfo>?>()
    val appList: LiveData<List<AppInfo>?> get() = _appList

    // LiveData для хранения и обновления информации об использовании устройства
    private var _usageDevice = MutableLiveData<DeviceUsage?>()
    val usageDevice: LiveData<DeviceUsage?> get() = _usageDevice

    // LiveData для хранения и обновления информации о ребенке
    private var _infoChild = MutableLiveData<ChildInfo?>()
    val infoChild: LiveData<ChildInfo?> get() = _infoChild

    // LiveData для хранения и обновления состояния блокировки устройства
    private var _DeviceLock = MutableLiveData<Boolean?>()
    val DeviceLock: LiveData<Boolean?> get() = _DeviceLock

    // Функция для получения геолокации ребенка
    fun getLocation() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getLocation {
                _locationChild.value = it
            }
        }
    }

    // Функция для получения списка приложений
    fun getAppList() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getAppList {
                _appList.value = it
            }
        }
    }

    // Функция для получения информации об использовании устройства
    fun getDeviceUsage() {
        repository.getDeviceUsage {
            _usageDevice.value = it
        }
    }

    // Функция для установки состояния блокировки устройства
    fun setLockDeviceState(isLocked: Boolean) {
        _DeviceLock.value = isLocked
        viewModelScope.launch(Dispatchers.Main) {
            repository.setLockDeviceState(_DeviceLock.value)
        }
    }

    // Функция для получения информации о ребенке
    fun getChildInfo() {
        repository.getChildInfo {
            _infoChild.value = it
        }
    }
}

