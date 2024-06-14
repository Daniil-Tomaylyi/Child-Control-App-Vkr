package com.example.childcontrol.headchild


import androidx.lifecycle.LiveData
import com.example.childcontrol.applist.LockApp
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

    private val userID = mAuth.currentUser?.uid // ID текущего пользователя
    // Ссылки на различные узлы в базе данных Firebase
    private val ChildPositionRef = database.reference.child("Child position") // Позиция ребенка
    private val DeviceLockRef = database.reference.child("Device is locked").child(userID!!).child("lockedDevice") // Статус блокировки устройства
    private val deviceLimitTimeDeviceLockRef = database.reference.child("Device is locked").child(userID!!).child("Time limits").child("Limit time device") // Ограничение времени использования устройства
    private val deviceBannedTimeLockRef = database.reference.child("Device is locked").child(userID!!).child("Time limits").child("Device banned time") // Время блокировки устройства
    private val AppListRef = database.reference.child("appList").child(userID!!) // Список приложений
    private val DeviceUsageRef = database.reference.child("deviceUsage").child(userID!!) // Использование устройства
    private val AppListLockRef = database.reference.child("lockAppList").child(userID!!) // Список заблокированных приложений
    private val geoFire: GeoFire = GeoFire(ChildPositionRef) // Экземпляр GeoFire для работы с геолокацией
    // Переменные для хранения данных
    private var limitTimeDevice: String? = null // Ограничение времени использования устройства
    private var bannedTimeDevice: List<DeviceBannedTime>? = null // Время блокировки устройства
    private var DeviceLock: Boolean? = null // Статус блокировки устройства
    private var AppListLock: List<LockApp>? = null // Список заблокированных приложений
    private var DeviceUsage = DeviceUsage() // Использование устройства

    // Функция для установки местоположения
    suspend fun setLocation(latitude: Double?, longitude: Double?) {
        withContext(Dispatchers.IO) {
            geoFire.setLocation(userID, GeoLocation(latitude!!, longitude!!))
        }
    }

    // Функция для получения статуса ограничения времени использования устройства
    fun getDeviceTimeLimitsLockStatus(callback: (String?) -> Unit) {
        deviceLimitTimeDeviceLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                limitTimeDevice = dataSnapshot.getValue(String::class.java)
                callback(limitTimeDevice)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Функция для получения статуса блокировки устройства
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

    // Функция для получения данных об использовании устройства
    fun getDeviceUsage(): DeviceUsage {
        DeviceUsageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    DeviceUsage = dataSnapshot.getValue(DeviceUsage::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return DeviceUsage
    }

    // Функция для получения времени блокировки устройства
    fun getBannedTimeDevice(): List<DeviceBannedTime>? {
        deviceBannedTimeLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bannedTimeDevice = dataSnapshot.children.mapNotNull { it.getValue(DeviceBannedTime::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return bannedTimeDevice
    }

    // Функция для получения статуса блокировки приложений
    fun getAppBlockStatus(): List<LockApp>? {
        AppListLockRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                AppListLock = dataSnapshot.children.mapNotNull { it.getValue(LockApp::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return AppListLock
    }

    // Функция для обновления списка приложений
    suspend fun updateAppList(appList: List<AppInfo>) {
        withContext(Dispatchers.IO) {
            AppListRef.setValue(appList)
        }
    }

    // Функция для обновления данных об использовании устройства
    suspend fun updateUsageDevice(usageDevice: LiveData<DeviceUsage?>) {
        withContext(Dispatchers.IO) {
            DeviceUsageRef.setValue(usageDevice)
        }
    }
}
