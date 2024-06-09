package com.example.childcontrol.parentmap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.LocationCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParentMapViewModel(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) :
    ViewModel() {
    private var viewModelJob = Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _locationChild = MutableLiveData<GeoLocation?>()
    private val userID = mAuth.currentUser?.uid
    private val ChildPositionRef = database.reference.child("Child position")
    private val geoFire: GeoFire = GeoFire(ChildPositionRef)
    val locationChild: LiveData<GeoLocation?> get() = _locationChild
    fun getLocation() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                Log.w("uid:", userID!!)
                geoFire.getLocation(userID, object : LocationCallback {
                    override fun onLocationResult(key: String?, location: GeoLocation?) {
                        if (location != null) {
                            _locationChild.value = location
                            Log.w("loc", "my location - ${location.latitude},${location.longitude}")
                            // Обработка полученного местоположения
                        } else {
                            _locationChild.value = null
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("locavaible", "Местоположение недоступно")
                    }
                })
            }
        }
    }
}