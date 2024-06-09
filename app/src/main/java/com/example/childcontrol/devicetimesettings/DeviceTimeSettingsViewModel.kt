package com.example.childcontrol.devicetimesettings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.childcontrol.db.ChildDatabaseDao
import com.example.childcontrol.db.DeviceBannedTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceTimeSettingsViewModel(
    private val mAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val childDatabase: ChildDatabaseDao
) : ViewModel() {

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val userID = mAuth.currentUser!!.uid
    private var _bannedTime = MutableLiveData<List<DeviceBannedTime>?>()
    val bannedTime: LiveData<List<DeviceBannedTime>?> get() = _bannedTime
    private val DeviceLockRef =
        database.reference.child("Device is locked").child(userID).child("Time limits")

    fun saveTimeSettings(limitTimeDevice: String) {
        uiScope.launch {
            saveSettings(limitTimeDevice)
        }
    }

    private suspend fun saveSettings(limitTimeDevice: String) {
        withContext(Dispatchers.IO) {
            DeviceLockRef.child("Limit time device").setValue(limitTimeDevice)
            DeviceLockRef.child("Device banned time").setValue(_bannedTime.value)
        }
    }

    fun setDeviceBannedTime(startHour: Int, startMinutes: Int, endHour: Int, endMinutes: Int) {
        uiScope.launch {
            insertTime(
                DeviceBannedTime(
                    start_time_hours = startHour,
                    start_time_minutes = startMinutes,
                    end_time_hours = endHour,
                    end_time_minutes = endMinutes,
                    uid = userID
                )
            )
            _bannedTime.value = getTime()
        }

    }

    private suspend fun insertTime(deviceBannedTime: DeviceBannedTime) {
        withContext(Dispatchers.IO) {
            childDatabase.insertBannedTime(deviceBannedTime)
        }
    }

    fun getDeviceBannedTime() {
        uiScope.launch {
            _bannedTime.value = getTime()
            Log.w("BannedTimelist", _bannedTime.value!!.size.toString())
        }
    }

    private suspend fun getTime(): List<DeviceBannedTime> {
        return withContext(Dispatchers.IO) {
            childDatabase.getBannedTime(userID)
        }
    }

    fun delDeviceBannedTime(uid: String, id: Long) {
        uiScope.launch {
            delTime(uid, id)
            _bannedTime.value = getTime()
        }
    }

    private suspend fun delTime(uid: String, id: Long) {
        withContext(Dispatchers.IO) {
            childDatabase.deleteBannedTime(uid, id)
        }
    }

    fun updateDeviceBannedTime(
        id: Long,
        uid: String,
        startEditHour: Int,
        startEditMinute: Int,
        endEditHour: Int,
        endEditMinute: Int,

        ) {
        uiScope.launch {
            updateTime(startEditHour, startEditMinute, endEditHour, endEditMinute, uid, id)
            _bannedTime.value = getTime()
        }
    }

    private suspend fun updateTime(
        startEditHour: Int,
        startEditMinute: Int,
        endEditHour: Int,
        endEditMinute: Int,
        uid: String,
        id: Long
    ) {
        withContext(Dispatchers.IO) {
            childDatabase.updateBannedTime(
                startEditHour,
                startEditMinute,
                endEditHour,
                endEditMinute,
                uid,
                id
            )
        }
    }
}