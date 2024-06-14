package com.example.childcontrol.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ChildDatabaseDao {
    @Insert
    fun insertChild(child: Child)

    @Insert
    fun insertBannedTime(deviceBannedTime: DeviceBannedTime)

    @Insert
    fun insertLocalAppList(localAppList: LocalAppList)

    @Query("Update LocalAppList Set UsageHours =:usageHours, UsageMinutes =:usageMinutes where uid =:uid and id =:id")
    fun updateLocalAppList(usageHours: Long, usageMinutes: Long, uid: String, id: Long)

    @Query("Update DeviceBannedTime Set StartTimeHours =:startTimeHours, StartTimeMinutes =:startTimeMinutes, EndTimeHours =:endTimeHours, EndTimeMinutes =:endTimeMinutes where uid =:uid and id =:id")
    fun updateBannedTime(
        startTimeHours: Int,
        startTimeMinutes: Int,
        endTimeHours: Int,
        endTimeMinutes: Int,
        uid: String,
        id: Long
    )

    @Query("Select * from Child where uid =:uid")
    fun getChild(uid: String): Child

    @Query("Select * from DeviceBannedTime where uid =:uid")
    fun getBannedTime(uid: String): List<DeviceBannedTime>

    @Query("Select * from localAppList where uid =:uid Order by (usageHours * 60 + usageMinutes) desc limit 3")
    fun getlocalapplist(uid: String): LocalAppList

    @Query("Delete from Child where uid =:uid")
    fun deleteChild(uid: String)

    @Query("Delete from DeviceBannedTime where uid =:uid and id=:id")
    fun deleteBannedTime(uid: String, id: Long)
}