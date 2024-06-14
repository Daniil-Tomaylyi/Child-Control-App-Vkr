package com.example.childcontrol.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.childcontrol.headchild.HeadChildFragmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocationService() : Service() {
    private val NOTIFICATION_ID = 3

    private var locationManager: LocationManager? = null

    private var myLocationListener: LocationListener? = null

    private var myLocation: Point? = null

    private val desired_accuracy = 0.0

    private val minimal_time: Long = 1000

    private val minimal_distance = 1.0

    private val use_in_background = false

    private val mAuth = FirebaseAuth.getInstance()

    private val database = FirebaseDatabase.getInstance()

    private val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)

    private var locationServiceJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + locationServiceJob)

    private lateinit var notification: Notification

    private lateinit var notificationManager: NotificationManager

    private lateinit var notificationChannel: NotificationChannel

    private lateinit var builder: NotificationCompat.Builder

    private val notificationChannelId = "Location_CHANNEL_ID"

    private val channelName = "Location Service"

    private val channelDescription = "Notification for Location Service"

    // Функция для подписки на обновления местоположения
    private fun subscribeToLocationUpdate() {
        // Выводим в лог информацию о менеджере местоположения
        Log.w("LocationService", locationManager.toString())
        // Если менеджер местоположения и слушатель местоположения не равны null, то подписываемся на обновления
        if (locationManager != null && myLocationListener != null) {
            locationManager?.subscribeForLocationUpdates(
                desired_accuracy, // желаемая точность
                minimal_time, // минимальное время между обновлениями
                minimal_distance, // минимальное расстояние между обновлениями
                use_in_background, // использовать ли в фоновом режиме
                FilteringMode.OFF, // режим фильтрации
                myLocationListener!! // слушатель местоположения
            )
        }
    }

    // Функция вызывается при старте сервиса
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForegroundService() // запускаем сервис в фоновом режиме
        changeLocation() // меняем местоположение
        return START_STICKY // сервис будет перезапущен, если он будет уничтожен системой
    }

    // Функция вызывается при привязке сервиса
    override fun onBind(intent: Intent?): IBinder? {
        return null // сервис не предоставляет привязку, поэтому возвращаем null
    }

    // Функция для запуска сервиса в фоновом режиме
    private fun startForegroundService() {
        notification = createNotification() // создаем уведомление
        startForeground(
            NOTIFICATION_ID,
            notification
        ) // запускаем сервис в фоновом режиме с уведомлением
    }

    // Функция для создания уведомления
    private fun createNotification(): Notification {
        notificationChannel = NotificationChannel(
            notificationChannelId, // идентификатор канала уведомлений
            channelName, // имя канала
            NotificationManager.IMPORTANCE_DEFAULT // важность уведомлений
        )
        notificationChannel.description = channelDescription // описание канала
        notificationManager =
            getSystemService(NotificationManager::class.java) // получаем менеджер уведомлений
        notificationManager.createNotificationChannel(notificationChannel) // создаем канал уведомлений
        builder =
            NotificationCompat.Builder(this, notificationChannelId) // создаем строителя уведомлений

        return builder.build() // строим уведомление
    }

    // Функция для изменения местоположения
    private fun changeLocation() {
        locationManager =
            MapKitFactory.getInstance().createLocationManager() // создаем менеджер местоположения
        myLocationListener = object : LocationListener {
            // Функция вызывается при обновлении местоположения
            override fun onLocationUpdated(location: Location) {
                uiScope.launch {
                    myLocation = location.position // обновляем местоположение

                    // устанавливаем местоположение в репозитории
                    headChildFragmentRepository.setLocation(
                        myLocation?.latitude, // широта
                        myLocation?.longitude // долгота
                    )
                }
            }

            // Функция вызывается при обновлении статуса местоположения
            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
                // если местоположение недоступно, то выводим соответствующее уведомление
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    Toast.makeText(
                        this@LocationService,
                        "Местоположение недоступно",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        subscribeToLocationUpdate() // подписываемся на обновления местоположения
    }

}