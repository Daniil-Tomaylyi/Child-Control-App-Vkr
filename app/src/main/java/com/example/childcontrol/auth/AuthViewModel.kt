package com.example.childcontrol.auth


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    // Объявление переменных для хранения данных почты и пароля
    var email = MutableLiveData<String?>()
    var pass = MutableLiveData<String?>()

    // Объявление переменных для отображения ошибок и ProgressDialog
    private var _showErrorMessageEvent = MutableLiveData<Boolean?>()
    val showErrorMessageEvent: LiveData<Boolean?>
        get() = _showErrorMessageEvent
    private var _showProgressDialog = MutableLiveData<Boolean?>()
    val showProgressDialog: LiveData<Boolean?>
        get() = _showProgressDialog

    // Переменная для хранения событий
    private var showEvents: Pair<Boolean?, Boolean?>? = null

    // Функция для входа в систему
    fun signIn() {
        viewModelScope.launch(Dispatchers.Main) {
            // Показываем прогресс
            _showProgressDialog.value = true
            if (email.value == null || pass.value == null) {
                // Если данные для входа отсутствуют, показываем сообщение об ошибке
                _showErrorMessageEvent.value = true
                _showProgressDialog.value = false
            } else {
                // Если данные для входа присутствуют, выполняем вход
                showEvents = repository.signIn(email.value!!, pass.value!!)
                _showErrorMessageEvent.value = showEvents!!.first
                _showProgressDialog.value = showEvents!!.second
            }
        }
    }

    // Функция для удаления приложения
    fun deleteApp() {
        viewModelScope.launch(Dispatchers.Main) {
            // Показываем прогресс
            _showProgressDialog.value = true
            if (email.value == null || pass.value == null) {
                // Если данные для входа отсутствуют, показываем сообщение об ошибке
                _showErrorMessageEvent.value = true
                _showProgressDialog.value = false
            } else {
                // Если данные для входа присутствуют, удаляем приложение
                repository.deleteApp()
                _showErrorMessageEvent.value = false
                _showProgressDialog.value = false
            }
        }
    }
}
