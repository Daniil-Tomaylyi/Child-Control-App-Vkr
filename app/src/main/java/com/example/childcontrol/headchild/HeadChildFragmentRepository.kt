package com.example.childcontrol.headchild

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.childcontrol.applist.lockApp
import com.example.childcontrol.db.DeviceBannedTime
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HeadChildFragmentRepository(
    private val mAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    private val userID = mAuth.currentUser?.uid
    private val ChildPositionRef = database.reference.child("Child position")
    private val DeviceLockRef =
        database.reference.child("Device is locked").child(userID!!).child("lockedDevice")
    private val deviceLimitTimeDeviceLockRef =
        database.reference.child("Device is locked").child(userID!!).child("Time limits")
            .child("Limit time device")
    private val deviceBannedTimeLockRef =
        database.reference.child("Device is locked").child(userID!!).child("Time limits")
            .child("Device banned time")
    private val AppListRef = database.reference.child("appList").child(userID!!)
    private val DeviceUsageRef = database.reference.child("deviceUsage").child(userID!!)
    private val AppListLockRef = database.reference.child("lockAppList").child(userID!!)
    private val geoFire: GeoFire = GeoFire(ChildPositionRef)
    private var limitTimeDevice: String? = null
    private var bannedTimeDevice: List<DeviceBannedTime>? = null
    private var DeviceLock: Boolean? = null
    private var AppListLock: List<lockApp>? = null
    private var DeviceUsage = DeviceUsage()
    suspend fun setLocation(latitude: Double?, longitude: Double?) {
        withContext(Dispatchers.IO) {
            geoFire.setLocation(userID, GeoLocation(latitude!!, longitude!!))
        }
    }

    suspend fun removeLocation() {
        withContext(Dispatchers.IO) {
            geoFire.removeLocation(userID)
        }
    }

    fun getDeviceTimeLimitsLockStatus(callback: (String?) -> Unit) {

        deviceLimitTimeDeviceLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                limitTimeDevice = dataSnapshot.getValue(String::class.java)
                callback(limitTimeDevice)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        Log.w("limitTimeDeviceRepository",limitTimeDevice.toString())

    }

    fun getDeviceBlockStatus(): Boolean? {
        DeviceLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DeviceLock = dataSnapshot.getValue(Boolean::class.java)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return DeviceLock
    }

    fun getDeviceUsage(): DeviceUsage {
        DeviceUsageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    DeviceUsage = dataSnapshot.getValue(DeviceUsage::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log.w("devicelocstat", "Статус блокировки недоступен")
            }
        })
        return DeviceUsage
    }

    fun getBannedTimeDevice(): List<DeviceBannedTime>? {
        deviceBannedTimeLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bannedTimeDevice =
                    dataSnapshot.children.mapNotNull { it.getValue(DeviceBannedTime::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log.w("devicelocstat", "Статус блокировки недоступен")
            }
        })
        return bannedTimeDevice
    }

    fun getAppBlockStatus(): List<lockApp>? {
        AppListLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                AppListLock = dataSnapshot.children.mapNotNull { it.getValue(lockApp::class.java) }
                Log.w("applock", "${AppListLock}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Log.w("devicelocstat", "Статус блокировки недоступен")
            }
        })
        return AppListLock
    }

    suspend fun updateAppList(appList: List<AppInfo>) {
        withContext(Dispatchers.IO) {
            AppListRef.setValue(appList)
        }
    }

    suspend fun updateUsageDevice(usageDevice: LiveData<DeviceUsage?>) {
        withContext(Dispatchers.IO) {
            DeviceUsageRef.setValue(usageDevice)
        }
    }

}