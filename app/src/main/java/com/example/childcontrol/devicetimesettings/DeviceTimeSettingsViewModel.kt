package com.example.childcontrol.devicetimesettings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcontrol.db.DeviceBannedTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceTimeSettingsViewModel(private val repository: DeviceTimeSettingsRepository) :
    ViewModel() {

    // Объявление переменной для хранения времени, в течение которого устройство запрещено
    private var _bannedTime = MutableLiveData<List<DeviceBannedTime>?>()

    // Получение значения запрещенного времени
    val bannedTime: LiveData<List<DeviceBannedTime>?> get() = _bannedTime

    // Сохранение настроек времени для устройства
    fun saveTimeSettings(limitTimeDevice: String) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.saveSettings(limitTimeDevice, _bannedTime.value)
        }
    }

    // Установка времени, в течение которого устройство запрещено
    fun setDeviceBannedTime(startHour: Int, startMinutes: Int, endHour: Int, endMinutes: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.insertTime(startHour, startMinutes, endHour, endMinutes)
            _bannedTime.value = repository.getTime()
        }

    }

    // Получение времени, в течение которого устройство запрещено
    fun getDeviceBannedTime() {
        viewModelScope.launch(Dispatchers.Main) {
            _bannedTime.value = repository.getTime()
            Log.w("BannedTimelist", _bannedTime.value!!.size.toString())
        }
    }

    // Удаление времени, в течение которого устройство запрещено
    fun delDeviceBannedTime(uid: String, id: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.delTime(uid, id)
            _bannedTime.value = repository.getTime()
        }
    }

    // Обновление времени, в течение которого устройство запрещено
    fun updateDeviceBannedTime(
        id: Long,
        uid: String,
        startEditHour: Int,
        startEditMinute: Int,
        endEditHour: Int,
        endEditMinute: Int,

        ) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.updateTime(
                startEditHour,
                startEditMinute,
                endEditHour,
                endEditMinute,
                uid,
                id
            )
            _bannedTime.value = repository.getTime()
        }
    }

}
