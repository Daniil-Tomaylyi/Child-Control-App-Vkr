package com.example.childcontrol.reg

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegRepository(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) {
    // Инициализация переменных для хранения идентификатора пользователя и ссылки на базу данных
    private var firebaseUserID = MutableLiveData<String>()
    private var refUsers = MutableLiveData<DatabaseReference>()

    // Инициализация HashMap для хранения информации о пользователе
    private var userHashMap = HashMap<String, Any>()

    // Функция для создания нового пользователя
    suspend fun insert(email: String, pass: String) {
        withContext(Dispatchers.IO) {
            // Создание нового пользователя с помощью FirebaseAuth
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener() {
                // Получение идентификатора нового пользователя
                firebaseUserID.value = mAuth.currentUser?.uid
                // Получение ссылки на узел нового пользователя в базе данных
                refUsers.value = database.reference.child("Users").child(
                    firebaseUserID.value!!
                )
                // Добавление информации о новом пользователе в HashMap
                userHashMap["uid"] = firebaseUserID.value!!
                userHashMap["email"] = email
                // Обновление информации о новом пользователе в базе данных
                refUsers.value?.updateChildren(userHashMap)
            }
        }
    }
}
