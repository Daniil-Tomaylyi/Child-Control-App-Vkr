package com.example.childcontrol.admin


import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log


class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    // Объявление переменной для хранения безопасного пароля администратора
    private val OUR_SECURE_ADMIN_PASSWORD = "12345"

    // Функция, вызываемая при включении администратора устройства
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }

    // Функция, вызываемая при отключении администратора устройства
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }

    // Функция, вызываемая при запросе на отключение администратора устройства
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {

        return super.onDisableRequested(context, intent)
    }
}
