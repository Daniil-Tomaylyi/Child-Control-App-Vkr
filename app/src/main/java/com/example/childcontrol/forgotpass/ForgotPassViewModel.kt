package com.example.childcontrol.forgotpass

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPassViewModel(private val mAuth: FirebaseAuth) : ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var _showErrorMessageEvent = MutableLiveData<Boolean?>()
    val showErrorMessageEvent: LiveData<Boolean?>
        get() = _showErrorMessageEvent
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val email = MutableLiveData<String?>()
    fun forgot_pass() {
        uiScope.launch {
            if (check_email()) {
                withContext(Dispatchers.IO) {
                    mAuth.sendPasswordResetEmail(email.value!!)

                }
                _showErrorMessageEvent.value = false
            } else {
                _showErrorMessageEvent.value = true
            }
        }
    }

    private fun check_email(): Boolean {
        return (email.value != null && email.value?.let {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        } == true)
    }
}