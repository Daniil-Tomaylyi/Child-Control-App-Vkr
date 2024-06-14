package com.example.childcontrol.reg

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegViewModel(private val repository: RegRepository) :
    ViewModel() {
    // Инициализация переменных для хранения email и пароля
    val email = MutableLiveData<String?>()
    val pass = MutableLiveData<String?>()

    // Инициализация переменных для управления отображением сообщений об ошибках и ProgressDialog
    private var _showErrorMessageEvent = MutableLiveData<Boolean?>()
    val showErrorMessageEvent: LiveData<Boolean?>
        get() = _showErrorMessageEvent
    private var _showProgressDialog = MutableLiveData<Boolean?>()
    val showProgressDialog: LiveData<Boolean?>
        get() = _showProgressDialog

    // Регулярное выражение для проверки пароля
    private val passwordPattern = Regex("(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}")

    // Функция для регистрации пользователя
    fun Sign_up() {
        _showProgressDialog.value = true
        viewModelScope.launch(Dispatchers.Main) {
            // Проверка введенных данных
            if (check_reg_data()) {
                // Если данные корректны, производим регистрацию
                _showErrorMessageEvent.value = false
                _showProgressDialog.value = false
                repository.insert(email.value!!, pass.value!!)
            } else {
                // Если данные некорректны, показываем сообщение об ошибке
                _showErrorMessageEvent.value = true
                _showProgressDialog.value = false
            }
        }
    }

    // Функция для проверки введенных данных
    private fun check_reg_data(): Boolean {
        return (email.value != "" && pass.value != "" && email.value?.let {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        } == true && pass.value?.matches(passwordPattern) == true)
    }
}
