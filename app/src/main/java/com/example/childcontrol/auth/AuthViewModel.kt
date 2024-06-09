package com.example.childcontrol.auth


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.childcontrol.db.ChildDatabaseDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val mAuth: FirebaseAuth) : ViewModel() {

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

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


}