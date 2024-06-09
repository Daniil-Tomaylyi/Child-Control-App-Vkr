package com.example.childcontrol.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceBannedTime(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo("StartTimeHours")
    var start_time_hours: Int = 0,
    @ColumnInfo("StartTimeMinutes")
    var start_time_minutes: Int = 0,
    @ColumnInfo("EndTimeHours")
    var end_time_hours: Int = 0,
    @ColumnInfo("EndTimeMinutes")
    var end_time_minutes: Int = 0,
    @ColumnInfo("uid")
    val uid: String = ""
)
