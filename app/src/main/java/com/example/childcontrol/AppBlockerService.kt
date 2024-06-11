package com.example.childcontrol


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.childcontrol.applist.lockApp
import com.example.childcontrol.headchild.HeadChildFragmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context


class AppBlockerService : Service() {

    private var lockApps: List<lockApp>? = null
    private var newlockApps: List<lockApp>? = null
    private val NOTIFICATION_ID = 1
    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    private val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)
    private val handler = Handler(Looper.getMainLooper())
    private val runnableCode = object : Runnable {
        override fun run() {
            lockApps = headChildFragmentRepository.getAppBlockStatus()
            if (newlockApps != lockApps) {
                lockApps?.forEach { lockApp ->
                    if (isAppRunning(lockApp.packageName)) {
                        if (lockApp.lockedApp) {
                            blockApp(lockApp.packageName)
                            newlockApps = lockApps
                            Log.w("lockedapp", "true")
                        } else {
                            unlockApp(lockApp.packageName)
                            newlockApps = lockApps
                            Log.w("lockedapp", "false")
                        }
                    }

                }
            }
            handler.postDelayed(this, 10000)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForegroundService()
        handler.post(runnableCode)
        return START_STICKY
    }


    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "APP_BLOCKER_CHANNEL_ID"
        val channelName = "Уведомления о блокировки приложений"
        val channelDescription = "Notification for App Blocker Service"
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun isAppRunning(packageName: String): Boolean {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val timeNow = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            timeNow - 1000 * 60 * 60,
            timeNow
        )


        val foregroundApp = stats.maxByOrNull { it.lastTimeUsed }

        return foregroundApp != null && foregroundApp.packageName == packageName
    }


    private fun blockApp(packageName: String) {
        val intent = Intent(this, LockScreenDeviceActivity::class.java)
        intent.putExtra("EXTRA_PACKAGE_NAME", packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun unlockApp(packageName: String) {
        val intent = Intent("ACTION_UNLOCK_APP")
        intent.putExtra("EXTRA_PACKAGE_NAME", packageName)
        sendBroadcast(intent)
    }

}

