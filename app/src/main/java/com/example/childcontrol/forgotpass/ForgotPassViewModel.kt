package com.example.childcontrol.forgotpass

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Класс ViewModel для функционала восстановления пароля
class ForgotPassViewModel(private val repository: ForgotPassRepository) : ViewModel() {

    // Событие для отображения сообщения об ошибке
    private var _showErrorMessageEvent = MutableLiveData<Boolean?>()
    // LiveData для события ошибки, доступная только для чтения
    val showErrorMessageEvent: LiveData<Boolean?>
        get() = _showErrorMessageEvent
    // LiveData для хранения введенного пользователем email
    val email = MutableLiveData<String?>()

    // Функция для запуска процесса восстановления пароля
    fun forgot_pass() {
        viewModelScope.launch(Dispatchers.Main) {
            // Проверяем валидность email
            if (check_email()) {
                // Если email валидный, запускаем процесс восстановления пароля
                repository.forgot_pass(email)
                // Сбрасываем флаг ошибки
                _showErrorMessageEvent.value = false
            } else {
                // Если email невалидный, устанавливаем флаг ошибки
                _showErrorMessageEvent.value = true
            }
        }
    }

    // Функция для проверки валидности email
    private fun check_email(): Boolean {
        // Проверяем, что email не null и соответствует шаблону EMAIL_ADDRESS
        return (email.value != null && email.value?.let {
            Patterns.EMAIL_ADDRESS.matcher(
                it
            ).matches()
        } == true)
    }
}
