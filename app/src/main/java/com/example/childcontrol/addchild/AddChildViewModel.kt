package com.example.childcontrol.addchild


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class AddChildViewModel(private val repository: AddChildRepository) :
    ViewModel() {
    // Объявление переменных для хранения имени и года рождения ребенка
    var name = MutableLiveData<String?>()
    var yearBirth = MutableLiveData<String?>()

    // Объявление переменной для отслеживания готовности к навигации
    private var _isReadyToNavigate = MutableLiveData<Boolean?>()
    val isReadyToNavigate: LiveData<Boolean?> get() = _isReadyToNavigate

    // Определение функции getChildInfo, которая асинхронно получает информацию о ребенке
    fun getChildInfo() {
        // Запуск корутины внутри области видимости ViewModel
        viewModelScope.launch(Dispatchers.Main) {
            // Установка значения _isReadyToNavigate в true
            _isReadyToNavigate.value = true

            // Получение информации о ребенке из репозитория
            repository.getInfo(name.value!!, yearBirth.value!!)
        }
    }
}
