package com.example.childcontrol.applist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.childcontrol.headchild.AppInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppListViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) :
    ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _appList = MutableLiveData<List<AppInfo>?>()
    val appList: LiveData<List<AppInfo>?> get() = _appList
    private val userID = mAuth.currentUser?.uid
    private val appListRef = database.reference.child("appList").child(userID!!)
    private val lockAppListRef = database.reference.child("lockAppList").child(userID!!)
    fun getAppList() {
        appListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _appList.value =
                    dataSnapshot.children.mapNotNull { it.getValue(AppInfo::class.java) }
                Log.w("applist", "${_appList.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("devicelocstat", "Статус блокировки недоступен")
            }
        })
    }

    fun setLockApps(AppID: Int, lockApp: lockApp) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                lockAppListRef.child(AppID.toString()).setValue(lockApp)
            }
        }
    }
}