package com.example.childcontrol.reg

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) :
    ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val email = MutableLiveData<String?>()
    val pass = MutableLiveData<String?>()
    private var _showErrorMessageEvent = MutableLiveData<Boolean?>()
    val showErrorMessageEvent: LiveData<Boolean?>
        get() = _showErrorMessageEvent
    private var _showProgressDialog = MutableLiveData<Boolean?>()
    val showProgressDialog: LiveData<Boolean?>
        get() = _showProgressDialog
    private var firebaseUserID = MutableLiveData<String>()
    private var refUsers = MutableLiveData<DatabaseReference>()
    private var userHashMap = HashMap<String, Any>()
    val passwordPattern = Regex("(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}")
    fun Sign_up() {
        _showProgressDialog.value = true
        uiScope.launch {
            if (check_reg_data()) {
                _showErrorMessageEvent.value = false
                _showProgressDialog.value = false
                insert(email.value!!, pass.value!!)
            } else {
                _showErrorMessageEvent.value = true
                _showProgressDialog.value = false
            }
        }
    }

    private fun check_reg_data(): Boolean {
        return (email.value != "" && pass.value != "" && email.value?.let {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        } == true && pass.value?.matches(passwordPattern) == true)
    }

    private suspend fun insert(email: String, pass: String) {
        withContext(Dispatchers.IO) {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener() {
                firebaseUserID.value = mAuth.currentUser?.uid
                refUsers.value = database.reference.child("Users").child(
                    firebaseUserID.value!!
                )
                userHashMap["uid"] = firebaseUserID.value!!
                userHashMap["email"] = email
                refUsers.value?.updateChildren(userHashMap)
            }
        }
    }
}