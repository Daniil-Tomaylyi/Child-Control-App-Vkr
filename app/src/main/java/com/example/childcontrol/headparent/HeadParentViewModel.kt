package com.example.childcontrol.headparent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.childcontrol.addchild.ChildInfo
import com.example.childcontrol.headchild.AppInfo
import com.example.childcontrol.headchild.DeviceUsage
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.LocationCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HeadParentViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) :
    ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _locationChild = MutableLiveData<GeoLocation?>()
    val locationChild: LiveData<GeoLocation?> get() = _locationChild
    private var _appList = MutableLiveData<List<AppInfo>?>()
    val appList: LiveData<List<AppInfo>?> get() = _appList
    private var _usageDevice = MutableLiveData<DeviceUsage?>()
    val usageDevice: LiveData<DeviceUsage?> get() = _usageDevice
    private var _infoChild = MutableLiveData<ChildInfo?>()
    val infoChild: LiveData<ChildInfo?> get() = _infoChild
    private var _DeviceLock = MutableLiveData<Boolean?>()
    val DeviceLock: LiveData<Boolean?> get() = _DeviceLock
    private val userID = mAuth.currentUser?.uid
    private val childInfoRef = database.reference.child("Child Info").child(userID!!)
    private val ChildPositionRef = database.reference.child("Child position")
    private val lockDeviceRef =
        database.reference.child("Device is locked").child(userID!!).child("lockedDevice")
    private val appListRef = database.reference.child("appList").child(userID!!)
    private val DeviceUsageRef =
        database.reference.child("deviceUsage").child(userID!!).child("value")
    private val geoFire: GeoFire = GeoFire(ChildPositionRef)
    fun getLocation() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                Log.w("uid:", userID!!)
                geoFire.getLocation(userID, object : LocationCallback {
                    override fun onLocationResult(key: String?, location: GeoLocation?) {
                        if (location != null) {
                            _locationChild.value = location
                            //  Log.w("loc", "my location - ${location.latitude},${location.longitude}")
                            // Обработка полученного местоположения
                        } else {
                            _locationChild.value = null
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log.w("locavaible", "Местоположение недоступно")
                    }
                })
            }
        }
    }

    fun getAppList() {
        appListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _appList.value =
                    dataSnapshot.children.mapNotNull { it.getValue(AppInfo::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getDeviceUsage() {
        DeviceUsageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _usageDevice.value = dataSnapshot.getValue(DeviceUsage::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun setLockDeviceState(isLocked: Boolean) {
        _DeviceLock.value = isLocked
        uiScope.launch {
            withContext(Dispatchers.IO) {
                lockDeviceRef.setValue(_DeviceLock.value)
            }
        }
    }

    fun getChildInfo() {
        childInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _infoChild.value = dataSnapshot.getValue(ChildInfo::class.java)
                Log.w("ChildInfo", _infoChild.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
