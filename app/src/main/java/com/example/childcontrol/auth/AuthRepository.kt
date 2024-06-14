package com.example.childcontrol.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) {
    // Получение идентификатора текущего пользователя
    private val userID = mAuth.currentUser?.uid

    // Получение ссылки на базу данных
    private val DBRef = database.reference

    // Список данных в базе данных
    private val listDataDB = listOf(
        "Child Info",
        "Child position",
        "Device is locked",
        "appList",
        "deviceUsage",
        "lockAppList"
    )

    // Переменные для отображения ошибок и прогресса
    private var showErrorMessageEvent: Boolean? = false
    private var showProgressDialog: Boolean? = false

    // Функция для входа в систему
    suspend fun signIn(email: String, pass: String): Pair<Boolean?, Boolean?> {
        withContext(Dispatchers.IO) {
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener() {
                    if (it.isSuccessful) {
                        // Если вход успешен, скрываем сообщение об ошибке и ProgressDialog
                        showErrorMessageEvent = false
                        showProgressDialog = false
                    } else {
                        // Если вход не удался, показываем сообщение об ошибке и скрываем ProgressDialog
                        showErrorMessageEvent = true
                        showProgressDialog = false
                    }
                }
        }
        // Возвращаем пару значений для отображения ошибки и ProgressDialog
        return Pair(showErrorMessageEvent, showProgressDialog)
    }

    // Функция для удаления приложения
    suspend fun deleteApp() {
        withContext(Dispatchers.IO) {
            for (data in listDataDB) {
                // Удаляем все данные пользователя из базы данных
                DBRef.child(data).child(userID!!).removeValue()
            }
            // Выходим из системы
            mAuth.signOut()
        }
    }
}
