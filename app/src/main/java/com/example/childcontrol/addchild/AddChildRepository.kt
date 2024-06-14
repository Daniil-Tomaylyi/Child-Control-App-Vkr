package com.example.childcontrol.addchild


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AddChildRepository(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) {
    // Получение идентификатора текущего пользователя
    private val userID = mAuth.currentUser?.uid

    // Получение ссылки на информацию о ребенке в базе данных
    private val childInfoRef = database.reference.child("Child Info").child(userID!!)

    // Определение функции getInfo, которая асинхронно получает информацию о ребенке
    suspend fun getInfo(name: String, yearBirth: String) {
        // Выполнение операции ввода-вывода в отдельном потоке
        withContext(Dispatchers.IO) {
            // Установка значения ChildInfo в базе данных
            childInfoRef.setValue(ChildInfo(name, yearBirth))
        }
    }
}
