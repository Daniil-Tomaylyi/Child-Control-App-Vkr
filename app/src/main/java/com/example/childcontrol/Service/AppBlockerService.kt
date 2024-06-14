package com.example.childcontrol.Service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.childcontrol.LockScreenActivity
import com.example.childcontrol.applist.LockApp
import com.example.childcontrol.headchild.HeadChildFragmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.provider.TedPermissionProvider.context


// Класс службы для блокировки приложений
class AppBlockerService : Service() {

    // Список для хранения  заблокированных приложений
    private var lockApps: List<LockApp>? = null


    // Идентификатор уведомления
    private val NOTIFICATION_ID = 1

    // Экземпляры FirebaseAuth и FirebaseDatabase для работы с базой данных Firebase
    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()

    // Репозиторий для работы с базой данных Firebase
    private val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)

    // Менеджер уведомлений и канал уведомлений
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel

    // Уведомление и его построитель
    private lateinit var notification: Notification
    private lateinit var builder: NotificationCompat.Builder

    // Менеджер статистики использования и текущее время
    private lateinit var usageStatsManager: UsageStatsManager
    private val timeNow = System.currentTimeMillis()

    // Статистика использования и приложение в фоновом режиме
    private var stats: MutableList<UsageStats> = mutableListOf()
    private var foregroundApp: UsageStats? = null

    // Идентификатор, имя и описание канала уведомлений
    private val notificationChannelId = "APP_BLOCKER_CHANNEL_ID"
    private val channelName = "Уведомления о блокировки приложений"
    private val channelDescription = "Notification for App Blocker Service"

    // Обработчик для выполнения кода в основном потоке
    private val handler = Handler(Looper.getMainLooper())

    // Runnable-объект для выполнения кода с определенной периодичностью
    private val runnableCode = object : Runnable {
        override fun run() {
            // Получаем список заблокированных приложений
            lockApps = headChildFragmentRepository.getAppBlockStatus()
                // Перебираем все заблокированные приложения
                lockApps?.forEach { lockApp ->
                    // Если приложение запущено
                    if (isAppRunning(lockApp.packageName)) {
                        Log.w("lockedapp", isAppRunning(lockApp.packageName).toString())
                        // Если приложение заблокировано, блокируем его
                        if (lockApp.lockedApp) {
                            blockApp(lockApp.packageName)
                            Log.w("lockedapp", "true")
                        } else {
                            // Если приложение разблокировано, разблокируем его
                            unlockApp(lockApp.packageName)
                            Log.w("lockedapp", "false")
                        }
                    }
                }

            // Запускаем код снова через 10 секунд
            handler.postDelayed(this, 10000)
        }
    }

    // Функция вызывается при старте службы
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Запускаем службу в фоновом режиме и выполняем код Runnable-объекта
        startForegroundService()
        handler.post(runnableCode)

        // Возвращаем START_STICKY, чтобы служба была перезапущена, если она будет уничтожена системой
        return START_STICKY
    }


    // Функция для запуска службы в фоновом режиме
    private fun startForegroundService() {
        // Создаем уведомление
        notification = createNotification()
        // Запускаем службу в фоновом режиме с уведомлением
        startForeground(NOTIFICATION_ID, notification)
    }

    // Функция для создания уведомления
    private fun createNotification(): Notification {
        // Создаем канал уведомлений
        notificationChannel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // Устанавливаем описание канала
        notificationChannel.description = channelDescription
        // Получаем менеджер уведомлений
        notificationManager = getSystemService(NotificationManager::class.java)
        // Создаем канал уведомлений
        notificationManager.createNotificationChannel(notificationChannel)
        // Создаем построитель уведомлений
        builder = NotificationCompat.Builder(this, notificationChannelId)

        // Возвращаем построенное уведомление
        return builder.build()
    }

    // Функция вызывается при привязке службы
    override fun onBind(intent: Intent?): IBinder? {
        return null // Служба не предоставляет привязку, поэтому возвращаем null
    }

    // Функция для проверки, запущено ли приложение
    private fun isAppRunning(packageName: String): Boolean {
        // Получаем менеджер статистики использования
        usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Запрашиваем статистику использования за последний час
        stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            timeNow - 1000 * 60 * 60,
            timeNow
        )

        // Получаем приложение, которое было в фоновом режиме последним
        foregroundApp = stats.maxByOrNull { it.lastTimeUsed }

        // Возвращаем true, если имя пакета приложения, которое было в фоновом режиме последним, совпадает с именем пакета, переданным в функцию
        // В противном случае возвращаем false
        return foregroundApp?.let { it.packageName == packageName } ?: false
    }


    // Функция для блокировки приложения
    private fun blockApp(packageName: String) {
        // Создаем намерение для запуска активности LockScreenDeviceActivity
        val intent = Intent(this, LockScreenActivity::class.java)
        // Добавляем имя пакета в намерение
        intent.putExtra("EXTRA_PACKAGE_NAME", packageName)
        // Добавляем флаги к намерению и запускаем активность
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    // Функция для разблокировки приложения
    private fun unlockApp(packageName: String) {
        // Создаем намерение для разблокировки приложения
        val intent = Intent("ACTION_UNLOCK_APP")
        // Добавляем имя пакета в намерение
        intent.putExtra("EXTRA_PACKAGE_NAME", packageName)
        // Отправляем широковещательное намерение
        sendBroadcast(intent)
    }

}

