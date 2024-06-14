package com.example.childcontrol.devicetimesettings

import com.example.childcontrol.db.ChildDatabaseDao
import com.example.childcontrol.db.DeviceBannedTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceTimeSettingsRepository(
    private val mAuth: FirebaseAuth, // Объект для работы с Firebase Authentication
    private val database: FirebaseDatabase, // Объект для работы с Firebase Database
    private val childDatabase: ChildDatabaseDao // Объект для работы с локальной базой данных
) {
    private val userID = mAuth.currentUser!!.uid // Получение ID текущего пользователя
    private val DeviceLockRef = // Ссылка на узел в Firebase Database, где хранятся настройки времени блокировки устройства
        database.reference.child("Device is locked").child(userID).child("Time limits")

    // Функция для сохранения настроек времени устройства в Firebase Database
    suspend fun saveSettings(limitTimeDevice: String, bannedTime: List<DeviceBannedTime>?) {
        withContext(Dispatchers.IO) {
            DeviceLockRef.child("Limit time device").setValue(limitTimeDevice)
            DeviceLockRef.child("Device banned time").setValue(bannedTime)
        }
    }

    // Функция для добавления времени блокировки устройства в локальную базу данных
    suspend fun insertTime(startHour: Int, startMinutes: Int, endHour: Int, endMinutes: Int) {
        withContext(Dispatchers.IO) {
            childDatabase.insertBannedTime(
                DeviceBannedTime(
                    start_time_hours = startHour,
                    start_time_minutes = startMinutes,
                    end_time_hours = endHour,
                    end_time_minutes = endMinutes,
                    uid = userID
                )
            )
        }
    }

    // Функция для получения времени блокировки устройства из локальной базы данных
    suspend fun getTime(): List<DeviceBannedTime> {
        return withContext(Dispatchers.IO) {
            childDatabase.getBannedTime(userID)
        }
    }

    // Функция для удаления времени блокировки устройства из локальной базы данных
    suspend fun delTime(uid: String, id: Long) {
        withContext(Dispatchers.IO) {
            childDatabase.deleteBannedTime(uid, id)
        }
    }

    // Функция для обновления времени блокировки устройства в локальной базе данных
    suspend fun updateTime(
        startEditHour: Int,
        startEditMinute: Int,
        endEditHour: Int,
        endEditMinute: Int,
        uid: String,
        id: Long
    ) {
        withContext(Dispatchers.IO) {
            childDatabase.updateBannedTime(
                startEditHour,
                startEditMinute,
                endEditHour,
                endEditMinute,
                uid,
                id
            )
        }
    }
}
