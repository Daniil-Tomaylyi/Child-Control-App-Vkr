package com.example.childcontrol.addchild

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddChildViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) :
    ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val userID = mAuth.currentUser?.uid
    private val ChildInfoRef = database.reference.child("Child Info").child(userID!!)
    var name = MutableLiveData<String?>()
    var yearBirth = MutableLiveData<String?>()
    private var _isReadyToNavigate = MutableLiveData<Boolean?>()
    val isReadyToNavigate: LiveData<Boolean?> get() = _isReadyToNavigate
    fun getChildInfo() {
        uiScope.launch {
            _isReadyToNavigate.value = true
            withContext(Dispatchers.IO) {
                ChildInfoRef.setValue(ChildInfo(name.value!!, yearBirth.value!!))
            }
        }
    }

}