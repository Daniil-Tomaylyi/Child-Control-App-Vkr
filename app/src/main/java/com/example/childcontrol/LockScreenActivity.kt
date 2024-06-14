package com.example.childcontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LockScreenActivity : AppCompatActivity() {
    private lateinit var unlockReceiver: BroadcastReceiver

    private lateinit var filter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        // Инициализация BroadcastReceiver
        unlockReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Если получено соответствующее намерение, завершить Activity
                if (intent?.action == "ACTION_UNLOCK_APP") {
                    finish()
                }
            }
        }

        // Создание фильтра для намерения и регистрация BroadcastReceiver
        filter = IntentFilter("ACTION_UNLOCK_APP")
        LocalBroadcastManager.getInstance(this).registerReceiver(unlockReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отмена регистрации BroadcastReceiver при уничтожении Activity
        LocalBroadcastManager.getInstance(this).unregisterReceiver(unlockReceiver)
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

