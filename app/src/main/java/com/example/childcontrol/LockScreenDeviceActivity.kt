package com.example.childcontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LockScreenDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen_device)

        // Регистрация BroadcastReceiver, который будет реагировать на намерение "ACTION_UNLOCK_DEVICE"
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Если получено соответствующее намерение и Activity еще не завершена, завершить Activity
                if (intent.action == "ACTION_UNLOCK_DEVICE") {
                    if (!isFinishing) {
                        finish()
                    }
                }
            }
        }, IntentFilter("ACTION_UNLOCK_DEVICE"))
    }

    override fun onUserLeaveHint() {
        // Перемещение задачи в фоновый режим, когда пользователь покидает приложение
        moveTaskToBack(true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Предотвращение действия по умолчанию для кнопки "Назад", чтобы пользователь не мог выйти из Activity
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
