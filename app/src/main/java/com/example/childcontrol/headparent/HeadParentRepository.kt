package com.example.childcontrol.headparent

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HeadParentRepository(
    private val mAuth: FirebaseAuth, // Ссылка на экземпляр FirebaseAuth
    private val database: FirebaseDatabase // Ссылка на экземпляр FirebaseDatabase
) {
    private val userID = mAuth.currentUser?.uid // Получение идентификатора текущего пользователя

    // Ссылки на различные узлы в базе данных Firebase
    private val childInfoRef = database.reference.child("Child Info").child(userID!!)
    private val ChildPositionRef = database.reference.child("Child position")
    private val lockDeviceRef =
        database.reference.child("Device is locked").child(userID!!).child("lockedDevice")
    private val appListRef = database.reference.child("appList").child(userID!!)
    private val DeviceUsageRef =
        database.reference.child("deviceUsage").child(userID!!).child("value")
    private val geoFire: GeoFire =
        GeoFire(ChildPositionRef) // Использование GeoFire для работы с геолокацией

    // Переменные для хранения данных
    private var locationChild: GeoLocation? = null
    private var appList: List<AppInfo>? = null
    private var usageDevice: DeviceUsage? = null
    private var infoChild: ChildInfo? = null

    // Функция для получения геолокации ребенка
    suspend fun getLocation(callback: (GeoLocation?) -> Unit) {
        withContext(Dispatchers.IO) {
            // Получение местоположения пользователя
            geoFire.getLocation(userID, object : LocationCallback {
                override fun onLocationResult(key: String?, location: GeoLocation?) {
                    if (location != null) {
                        // Если местоположение получено, сохраняем его
                        locationChild = location
                        // Обработка полученного местоположения
                    } else {
                        // Если местоположение не получено, устанавливаем значение null
                        locationChild = null
                    }
                    // Возвращаем местоположение через callback
                    callback(locationChild)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки при получении местоположения
                }
            })
        }
    }

    // Функция для получения списка приложений
    fun getAppList(callback: (List<AppInfo>?) -> Unit) {
        appListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                appList =
                    dataSnapshot.children.mapNotNull { it.getValue(AppInfo::class.java) }
                callback(appList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Функция для получения информации об использовании устройства
    fun getDeviceUsage(callback: (DeviceUsage?) -> Unit) {
        DeviceUsageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usageDevice = dataSnapshot.getValue(DeviceUsage::class.java)
                callback(usageDevice)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Функция для установки состояния блокировки устройства
    suspend fun setLockDeviceState(deviceLock: Boolean?) {
        withContext(Dispatchers.IO) {
            lockDeviceRef.setValue(deviceLock)
        }
    }

    // Функция для получения информации о ребенке
    fun getChildInfo(callback: (ChildInfo?) -> Unit) {
        childInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                infoChild = dataSnapshot.getValue(ChildInfo::class.java)
                callback(infoChild)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
