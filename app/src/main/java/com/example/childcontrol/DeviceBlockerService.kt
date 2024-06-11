package com.example.childcontrol

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.childcontrol.db.DeviceBannedTime
import com.example.childcontrol.headchild.DeviceUsage
import com.example.childcontrol.headchild.HeadChildFragmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalTime


class DeviceBlockerService() : Service() {

    private val NOTIFICATION_ID = 2
    private var isLock: Boolean? = false
    private var newIsLock: Boolean? = false
    private var limitTimeDevice: Int? = null
    private var bannedTimesDevice: List<DeviceBannedTime>? = null
    private var newBannedTimesDevice: List<DeviceBannedTime>? = null
    private var newLimitTimeDevice: Int? = null
    private var currentTimeDevice = LocalTime.now()
    private var deviceUsageTime: DeviceUsage? = null
    private lateinit var notificationManager:NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)
    private val handler = Handler(Looper.getMainLooper())
    private val runnableCode = object : Runnable {
        override fun run() {
            isLock = headChildFragmentRepository.getDeviceBlockStatus()

            Log.w("islock", isLock.toString())
            if (newIsLock != isLock) {
                if (isLock == true) {
                    lockdevice()
                    newIsLock = isLock
                } else {
                    unlockdevice()
                    newIsLock = isLock
                }
            }
            handler.postDelayed(this, 10000)
        }
    }
    private val runnableCodeHourDevice = object : Runnable {
        override fun run() {
            headChildFragmentRepository.getDeviceTimeLimitsLockStatus { limitTimeDeviceValue ->
                limitTimeDevice = limitTimeDeviceValue?.replace(" ч", "")?.toInt() ?: 0

            deviceUsageTime = headChildFragmentRepository.getDeviceUsage()
            if (limitTimeDevice != 0) {

                if (newLimitTimeDevice != limitTimeDevice) {
                    if (deviceUsageTime!!.usageHours.toInt() == limitTimeDevice) {
                        lockdevice()
                        newLimitTimeDevice = limitTimeDevice
                    }
                    if (deviceUsageTime!!.usageHours.toInt() == 0) {
                        unlockdevice()
                        newLimitTimeDevice = limitTimeDevice
                    }
                    if (deviceUsageTime!!.usageHours.toInt() == limitTimeDevice!! - 1){
                        createNotifyUsageTimeout()

                    }
                }
            }
            }
            handler.postDelayed(this, 3600000)
        }
    }

    private fun createNotifyUsageTimeout() {

        notificationManager = getSystemService(NotificationManager::class.java)
        notificationChannel = NotificationChannel("DEVICE_BLOCKER_CHANNEL_ID", "Уведомления о блокировки устройства", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)

        val builder = NotificationCompat.Builder(this, "DEVICE_BLOCKER_CHANNEL_ID")
            .setSmallIcon(R.drawable.free_icon_notification_3239952)
            .setContentTitle("Устройство скоро будет заблокировано")
            .setContentText("Cкоро вы израсходуете весь ваш лимит пользования устройством выделенный на день")
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private val runnableBannedTimeDevice = object : Runnable {
        override fun run() {
            bannedTimesDevice = headChildFragmentRepository.getBannedTimeDevice()
            currentTimeDevice = LocalTime.now()
            bannedTimesDevice?.forEach { bannedTime ->
                newBannedTimesDevice?.forEach { newBannedTimeDevice ->
                    if (newBannedTimeDevice != bannedTime) {
                        if (bannedTime.start_time_hours == currentTimeDevice.hour) {
                            if (bannedTime.start_time_minutes == currentTimeDevice.minute) {
                                lockdevice()
                                newBannedTimeDevice.start_time_hours = bannedTime.start_time_hours
                                newBannedTimeDevice.start_time_minutes =
                                    bannedTime.start_time_minutes
                            }
                        }
                        if (bannedTime.end_time_hours == currentTimeDevice.hour) {
                            if (bannedTime.end_time_minutes == currentTimeDevice.minute) {
                                unlockdevice()
                                newBannedTimeDevice.end_time_hours = bannedTime.end_time_hours
                                newBannedTimeDevice.end_time_minutes = bannedTime.end_time_minutes
                            }
                        }
                    }
                }
            }
            handler.postDelayed(this, 60000)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        startForegroundService()
        handler.post(runnableCode)
        handler.post(runnableCodeHourDevice)
        handler.post(runnableBannedTimeDevice)
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "DEVICE_BLOCKER_CHANNEL_ID"
        val channelName = "Уведомления о блокировки устройства"
        val channelDescription = "Notification for Device Blocker Service"
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

    private fun unlockdevice() {
        val intent = Intent("ACTION_UNLOCK_DEVICE")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun lockdevice() {
        val intent = Intent(this, LockScreenDeviceActivity::class.java)
        Log.w("LockScreenDeviceActivity", "start")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}