package com.example.childcontrol.forgotpass

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Класс репозитория для функционала восстановления пароля
class ForgotPassRepository(private val mAuth: FirebaseAuth) {

    // Функция для отправки запроса на сброс пароля
    suspend fun forgot_pass(email: MutableLiveData<String?>) {
        withContext(Dispatchers.IO) {
            // Отправляем запрос на сброс пароля на указанный email
            mAuth.sendPasswordResetEmail(email.value!!)
        }
    }
}
