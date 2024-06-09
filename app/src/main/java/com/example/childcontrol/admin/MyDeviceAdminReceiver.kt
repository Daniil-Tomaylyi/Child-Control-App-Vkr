package com.example.childcontrol.admin

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.util.Log


class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        // Device admin enabled

        Log.w("onEnabled", "yes")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        // Device admin disabled
        Log.w("onDisabled", "yes")

    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        Log.w("onDisableRequested", "yes")
        return "Отключение администратора устройства запрещено."

    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getAction().equals(DevicePolicyManager.ACTION_DEVICE_ADMIN_SERVICE)) {
            Log.w("ACTION_ADD_DEVICE_ADMIN", "yes")
        }
    }
}