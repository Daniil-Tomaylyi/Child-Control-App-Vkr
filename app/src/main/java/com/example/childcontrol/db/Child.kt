package com.example.childcontrol.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Child(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "NameChild")
    var name_child: String,
    @ColumnInfo(name = "YearBirthday")
    var year_birthday: Short,
    @ColumnInfo(name = "TimeDevice")
    var time_device: Long = System.currentTimeMillis(),
    @ColumnInfo("uid")
    val uid: String = ""
)