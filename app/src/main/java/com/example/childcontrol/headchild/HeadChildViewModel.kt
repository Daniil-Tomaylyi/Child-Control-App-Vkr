package com.example.childcontrol.headchild


import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class HeadChildViewModel(
    private val repository: HeadChildFragmentRepository, // Репозиторий
    private val packageManager: PackageManager, // Менеджер пакетов
    private val usageStatsManager: UsageStatsManager // Менеджер статистики использования
) : ViewModel() {
    // LiveData для хранения данных об использовании устройства
    private var _usageDevice = MutableLiveData<DeviceUsage?>()
    val usageDevice: LiveData<DeviceUsage?> get() = _usageDevice
    // Таймер для периодического обновления данных
    private val timer = Timer()

    // Функция для обновления списка приложений
    fun updateAppList() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                viewModelScope.launch(Dispatchers.Main) {
                    val appList = getAppList()
                    repository.updateAppList(appList)
                }
            }
        }, 0, 10000) // Запуск каждую минуту
    }

    // Функция для обновления данных об использовании устройства
    fun updateUsageDevice() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                viewModelScope.launch(Dispatchers.Main) {
                    _usageDevice.value = getUsageDevice()
                    repository.updateUsageDevice(usageDevice)
                }
            }
        }, 0, 10000) // Запуск каждую минуту
    }

    // Функция для получения данных об использовании устройства
    suspend fun getUsageDevice(): DeviceUsage = withContext(Dispatchers.IO) {
        val appList = getAppList()
        var usageHours = 0L
        var usageMinutes = 0L
        appList.forEach() { stat ->
            usageHours += stat.usageHours
            usageMinutes += stat.usageMinutes
        }
        usageHours += usageMinutes / 60
        usageMinutes %= 60
        DeviceUsage(usageHours, usageMinutes)
    }

    // Функция для получения списка приложений
    suspend fun getAppList(): List<AppInfo> = withContext(Dispatchers.IO) {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 3600 * 24
        val usageStats =
            usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        val usageMap = usageStats.associateBy { it.packageName }
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        apps.filter { app ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(app.packageName)
            val resolveInfo = packageManager.queryIntentActivities(intent, 0)
            // Проверяем, что приложение имеет иконку в меню
            resolveInfo.isNotEmpty() && app.packageName != "com.example.childcontrol"
        }
            .map { app ->
                val name = app.loadLabel(packageManager).toString()
                val packageName = app.packageName.toString()
                val icon = app.loadIcon(packageManager)
                val usageTime = usageMap[app.packageName]?.totalTimeInForeground ?: 0
                val usageHours = TimeUnit.MILLISECONDS.toHours(usageTime)
                val usageMinutes = TimeUnit.MILLISECONDS.toMinutes(usageTime) % 60
                AppInfo(packageName, name, drawableToBase64(icon), usageHours, usageMinutes)
            }
    }

    // Функция для преобразования Drawable в Base64
    private fun drawableToBase64(drawable: Drawable): String {
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            // Создаем Bitmap для не-BitmapDrawable типов
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}
