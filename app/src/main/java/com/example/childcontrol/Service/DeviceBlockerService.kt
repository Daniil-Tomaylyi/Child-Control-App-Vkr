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
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.childcontrol.LockScreenDeviceActivity
import com.example.childcontrol.R
import com.example.childcontrol.db.DeviceBannedTime
import com.example.childcontrol.headchild.DeviceUsage
import com.example.childcontrol.headchild.HeadChildFragmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalTime


// Класс службы для блокировки устройства
class DeviceBlockerService() : Service() {

    // Идентификатор уведомления
    private val NOTIFICATION_ID = 2

    // Переменные для хранения текущего и нового состояния блокировки
    private var isLock: Boolean? = false
    private var newIsLock: Boolean? = false

    // Переменные для хранения текущего и нового лимита времени использования устройства
    private var limitTimeDevice: Int? = null
    private var newLimitTimeDevice: Int? = null

    // Списки для хранения текущих и новых временных интервалов, в течение которых устройство должно быть заблокировано
    private var bannedTimesDevice: List<DeviceBannedTime>? = null
    private var newBannedTimesDevice: List<DeviceBannedTime>? = null

    // Текущее время на устройстве
    private var currentTimeDevice = LocalTime.now()

    // Время использования устройства
    private var deviceUsageTime: DeviceUsage? = null

    // Менеджер уведомлений и канал уведомлений
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel

    // Уведомление и его построитель
    private lateinit var notification: Notification
    private lateinit var builder: NotificationCompat.Builder

    // Экземпляры FirebaseAuth и FirebaseDatabase для работы с базой данных Firebase
    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // Репозиторий для работы с базой данных Firebase
    private val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)

    // Идентификатор, имя и описание канала уведомлений
    private val notificationChannelId = "DEVICE_BLOCKER_CHANNEL_ID"
    private val channelName = "Уведомления о блокировки устройства"
    private val channelDescription = "Notification for Device Blocker Service"

    // Обработчик для выполнения кода в основном потоке
    private val handler = Handler(Looper.getMainLooper())

    // Runnable-объекты для выполнения кода с определенной периодичностью
    private val runnableCode = object : Runnable {
        override fun run() {
            // Получаем статус блокировки устройства
            isLock = headChildFragmentRepository.getDeviceBlockStatus()

            // Выводим в лог текущий статус блокировки
            Log.w("islock", isLock.toString())

            // Если статус блокировки изменился, блокируем или разблокируем устройство
            if (newIsLock != isLock) {
                if (isLock == true) {
                    lockDevice()
                    newIsLock = isLock
                } else {
                    unlockDevice()
                    newIsLock = isLock
                }
            }

            // Запускаем код снова через 10 секунд
            handler.postDelayed(this, 10000)
        }
    }

    // Runnable-объект для проверки лимита времени использования устройства каждый час
    private val runnableCodeHourDevice = object : Runnable {
        override fun run() {
            // Получаем лимит времени использования устройства
            headChildFragmentRepository.getDeviceTimeLimitsLockStatus { limitTimeDeviceValue ->
                limitTimeDevice = limitTimeDeviceValue?.replace(" ч", "")?.toInt() ?: 0

                // Получаем текущее время использования устройства
                deviceUsageTime = headChildFragmentRepository.getDeviceUsage()
                if (limitTimeDevice != 0) {
                    // Если новый лимит времени отличается от текущего
                    if (newLimitTimeDevice != limitTimeDevice) {
                        // Если текущее время использования равно лимиту, блокируем устройство
                        if (deviceUsageTime!!.usageHours.toInt() == limitTimeDevice) {
                            lockDevice()
                            newLimitTimeDevice = limitTimeDevice
                        }
                        // Если текущее время использования равно 0, разблокируем устройство
                        if (deviceUsageTime!!.usageHours.toInt() == 0) {
                            unlockDevice()
                            newLimitTimeDevice = limitTimeDevice
                        }
                        // Если до лимита остался 1 час, создаем уведомление
                        if (deviceUsageTime!!.usageHours.toInt() == limitTimeDevice!! - 1) {
                            createNotifyUsageTimeout()
                        }
                    }
                }
            }
            // Запускаем код снова через 1 час
            handler.postDelayed(this, 3600000)
        }
    }

    // Runnable-объект для проверки запрещенного времени использования устройства каждую минуту
    private val runnableBannedTimeDevice = object : Runnable {
        override fun run() {
            // Получаем список запрещенных времен
            bannedTimesDevice = headChildFragmentRepository.getBannedTimeDevice()
            // Получаем текущее время
            currentTimeDevice = LocalTime.now()
            // Перебираем все запрещенные времена
            bannedTimesDevice?.forEach { bannedTime ->
                newBannedTimesDevice?.forEach { newBannedTimeDevice ->
                    // Если новое запрещенное время отличается от текущего
                    if (newBannedTimeDevice != bannedTime) {
                        // Если текущее время равно времени начала запрета, блокируем устройство
                        if (bannedTime.start_time_hours == currentTimeDevice.hour) {
                            if (bannedTime.start_time_minutes == currentTimeDevice.minute) {
                                lockDevice()
                                newBannedTimeDevice.start_time_hours = bannedTime.start_time_hours
                                newBannedTimeDevice.start_time_minutes =
                                    bannedTime.start_time_minutes
                            }
                        }
                        // Если текущее время равно времени окончания запрета, разблокируем устройство
                        if (bannedTime.end_time_hours == currentTimeDevice.hour) {
                            if (bannedTime.end_time_minutes == currentTimeDevice.minute) {
                                unlockDevice()
                                newBannedTimeDevice.end_time_hours = bannedTime.end_time_hours
                                newBannedTimeDevice.end_time_minutes = bannedTime.end_time_minutes
                            }
                        }
                    }
                }
            }
            // Запускаем код снова через 1 минуту
            handler.postDelayed(this, 60000)
        }
    }


    // Функция вызывается при старте службы
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Запускаем службу в фоновом режиме и выполняем код Runnable-объектов
        startForegroundService()
        handler.post(runnableCode)
        handler.post(runnableCodeHourDevice)
        handler.post(runnableBannedTimeDevice)

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

    // Функция для создания уведомления о скором истечении лимита времени использования устройства
    private fun createNotifyUsageTimeout() {
        // Получаем менеджер уведомлений
        notificationManager = getSystemService(NotificationManager::class.java)
        // Создаем канал уведомлений
        notificationChannel = NotificationChannel(
            "DEVICE_BLOCKER_CHANNEL_ID",
            "Уведомления о блокировки устройства",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // Создаем канал уведомлений
        notificationManager.createNotificationChannel(notificationChannel)

        // Создаем построитель уведомлений
        builder = NotificationCompat.Builder(this, "DEVICE_BLOCKER_CHANNEL_ID")
            .setSmallIcon(R.drawable.free_icon_notification_3239952)
            .setContentTitle("Устройство скоро будет заблокировано")
            .setContentText("Cкоро вы израсходуете весь ваш лимит пользования устройством выделенный на день")
        // Отправляем уведомление
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    // Функция для разблокировки устройства
    private fun unlockDevice() {
        // Создаем намерение для разблокировки устройства
        val intent = Intent("ACTION_UNLOCK_DEVICE")
        // Отправляем широковещательное намерение
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // Функция для блокировки устройства
    private fun lockDevice() {
        // Создаем намерение для запуска активности LockScreenDeviceActivity
        val intent = Intent(this, LockScreenDeviceActivity::class.java)
        Log.w("LockScreenDeviceActivity", "start")

        // Добавляем флаги к намерению и запускаем активность
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    // Функция вызывается при привязке службы
    override fun onBind(intent: Intent?): IBinder? {
        return null // Служба не предоставляет привязку, поэтому возвращаем null
    }

}