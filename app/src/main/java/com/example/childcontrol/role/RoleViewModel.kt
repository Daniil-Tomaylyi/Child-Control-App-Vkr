package com.example.childcontrol.role

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.childcontrol.addchild.ChildInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RoleViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) :
    ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _infoChild = MutableLiveData<ChildInfo?>()
    val infoChild: LiveData<ChildInfo?> get() = _infoChild
    private val userID = mAuth.currentUser?.uid
    private val childInfoRef = database.reference.child("Child Info").child(userID!!)
    fun getChildInfo() {
        childInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _infoChild.value = dataSnapshot.getValue(ChildInfo::class.java)
                Log.w("ChildInfo", _infoChild.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}