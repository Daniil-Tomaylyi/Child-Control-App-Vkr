package com.example.childcontrol.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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

class LocationService(): Service() {
    private val NOTIFICATION_ID = 3
    private val handler = Handler(Looper.getMainLooper())
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


    private fun subscribeToLocationUpdate() {
        Log.w("LocationService", locationManager.toString())
        if (locationManager != null && myLocationListener != null) {
            locationManager?.subscribeForLocationUpdates(
                desired_accuracy,
                minimal_time,
                minimal_distance,
                use_in_background,
                FilteringMode.OFF,
                myLocationListener!!
            )
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForegroundService()
        changeLocation()
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }
    private fun createNotification(): Notification {
        val notificationChannelId = "Location_CHANNEL_ID"
        val channelName = "Location Service"
        val channelDescription = "Notification for Location Service"
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = channelDescription
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
        val builder = NotificationCompat.Builder(this, notificationChannelId)

        return builder.build()
    }
    private fun changeLocation(){
        locationManager = MapKitFactory.getInstance().createLocationManager()
        myLocationListener = object : LocationListener {
            override fun onLocationUpdated(location: Location) {
                uiScope.launch {
                    myLocation = location.position

                    headChildFragmentRepository.setLocation(
                        myLocation?.latitude,
                        myLocation?.longitude
                    )
                }
            }

            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    Toast.makeText(this@LocationService, "Местоположение недоступно", Toast.LENGTH_SHORT).show()
                }
            }
        }
        subscribeToLocationUpdate()
    }
}