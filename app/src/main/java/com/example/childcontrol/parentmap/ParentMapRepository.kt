package com.example.childcontrol.parentmap

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.LocationCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParentMapRepository(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) {
    // Получение текущего идентификатора пользователя
    private val userID = mAuth.currentUser?.uid
    // Ссылка на дочерний узел "Child position" в базе данных
    private val ChildPositionRef = database.reference.child("Child position")
    // Инициализация GeoFire с ссылкой на дочерний узел
    private val geoFire: GeoFire = GeoFire(ChildPositionRef)
    // Переменная для хранения местоположения ребенка
    private var locationChild: GeoLocation? = null

    // Функция для получения местоположения
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
}
