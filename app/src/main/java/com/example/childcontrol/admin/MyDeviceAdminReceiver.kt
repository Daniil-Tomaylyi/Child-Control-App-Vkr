package com.example.childcontrol.admin

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName

import android.content.Context
import android.content.Intent
import android.util.Log



class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    private val OUR_SECURE_ADMIN_PASSWORD = "12345"
    override fun onEnabled(context: Context, intent: Intent) {
        // Device admin enabled
        super.onEnabled(context, intent)
        // Получение экземпляра DevicePolicyManager
        Log.w("onEnabled", "Попытка изменения настроек администратора")

    }



    override fun onDisabled(context: Context, intent: Intent) {
        // Device admin disabled
        super.onDisabled(context, intent)
        Log.w("onDisabled", "Попытка изменения настроек администратора")

    }
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        super.onDisableRequested(context, intent)
        Log.w("onDisableRequested", "Попытка изменения настроек администратора")
        val localComponentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
        val localDevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (localDevicePolicyManager.isAdminActive(localComponentName)) {
            localDevicePolicyManager.setPasswordQuality(localComponentName, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC)
        }
        // сброс пароля пользователя
        localDevicePolicyManager.resetPassword(OUR_SECURE_ADMIN_PASSWORD, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
        // блокировка устройства
        localDevicePolicyManager.lockNow()

        return "Вы уверены, что хотите отключить администратора устройства?"
    }

}