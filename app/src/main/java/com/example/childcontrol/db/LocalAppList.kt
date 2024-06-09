package com.example.childcontrol.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalAppList(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo("IconApp")
    val iconApp: String = "",
    @ColumnInfo("NameApp")
    val nameApp: String = "",
    @ColumnInfo("PackageName")
    val packageName: String = "",
    @ColumnInfo("UsageHours")
    var usageHours: Long = 0L,
    @ColumnInfo("UsageMinutes")
    var usageMinutes: Long = 0L,
    @ColumnInfo("uid")
    val uid: String = ""
)
