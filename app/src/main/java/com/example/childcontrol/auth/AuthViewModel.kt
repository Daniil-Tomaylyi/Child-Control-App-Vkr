package com.example.childcontrol.auth


import android.util.Log
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

class AuthViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) : ViewModel() {

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val userID = mAuth.currentUser?.uid
    private val DBRef = database.reference
    private val listDataDB = listOf<String>("Child Info","Child position","Device is locked","appList","deviceUsage","lockAppList")
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var email = MutableLiveData<String?>()
    var pass = MutableLiveData<String?>()
    private var _showErrorMessageEvent = MutableLiveData<Boolean?>()
    val showErrorMessageEvent: LiveData<Boolean?>
        get() = _showErrorMessageEvent
    private var _showProgressDialog = MutableLiveData<Boolean?>()
    val showProgressDialog: LiveData<Boolean?>
        get() = _showProgressDialog



    fun sign_in() {
        uiScope.launch {
            _showProgressDialog.value = true
            if (email.value == null || pass.value == null) {
                _showErrorMessageEvent.value = true
                _showProgressDialog.value = false
            } else {
                withContext(Dispatchers.IO) {
                    mAuth.signInWithEmailAndPassword(email.value!!, pass.value!!)
                        .addOnCompleteListener() {
                            if (it.isSuccessful) {
                                _showErrorMessageEvent.value = false
                                _showProgressDialog.value = false
                            } else {
                                _showErrorMessageEvent.value = true
                                _showProgressDialog.value = false
                            }


                        }
                }
            }

        }
    }
    fun delete_app(){
        uiScope.launch {
            _showProgressDialog.value = true
            if (email.value == null || pass.value == null) {
                _showErrorMessageEvent.value = true
                _showProgressDialog.value = false
            } else {
                withContext(Dispatchers.IO) {
                    for (data in listDataDB){
                        DBRef.child(data).child(userID!!).removeValue()
                    }
                    mAuth.signOut()
                }
                _showErrorMessageEvent.value = false
                _showProgressDialog.value = false
            }

        }
    }


}