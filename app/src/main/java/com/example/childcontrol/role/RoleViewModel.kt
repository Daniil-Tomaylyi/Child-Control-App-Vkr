package com.example.childcontrol.role


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.childcontrol.addchild.ChildInfo

class RoleViewModel(private val repository: RoleRepository) : ViewModel() {
    // MutableLiveData для хранения информации о ребенке
    private var _infoChild = MutableLiveData<ChildInfo?>()
    // LiveData для предоставления информации о ребенке внешним классам
    val infoChild: LiveData<ChildInfo?> get() = _infoChild

    // Функция для получения информации о ребенке из репозитория
    fun getChildInfo() {
        // Вызов функции getChildInfo из репозитория
        repository.getChildInfo {
            // Обновление _infoChild значением, полученным из репозитория
            _infoChild.value = it
        }
    }
}
