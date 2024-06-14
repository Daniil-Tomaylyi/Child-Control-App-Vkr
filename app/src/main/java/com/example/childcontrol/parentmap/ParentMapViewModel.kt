package com.example.childcontrol.parentmap


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ParentMapViewModel(private val repository: ParentMapRepository) :
    ViewModel() {

    // LiveData для хранения и обновления геолокации ребенка
    private var _locationChild = MutableLiveData<GeoLocation?>()
    val locationChild: LiveData<GeoLocation?> get() = _locationChild

    // Функция для получения геолокации ребенка
    fun getLocation() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getLocation {
                _locationChild.value = it
            }
        }
    }
}