package com.example.childcontrol.role

import com.example.childcontrol.addchild.ChildInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RoleRepository(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) {
    // Получение идентификатора текущего пользователя
    private val userID = mAuth.currentUser?.uid

    // Получение ссылки на информацию о ребенке в базе данных Firebase
    private val childInfoRef = database.reference.child("Child Info").child(userID!!)

    // Переменная для хранения информации о ребенке
    private var infoChild: ChildInfo? = null

    // Функция для получения информации о ребенке
    fun getChildInfo(callback: (ChildInfo?) -> Unit) {
        // Добавление слушателя для изменений в базе данных
        childInfoRef.addValueEventListener(object : ValueEventListener {
            // Функция, вызываемая при изменении данных
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Получение информации о ребенке из dataSnapshot
                infoChild = dataSnapshot.getValue(ChildInfo::class.java)
                // Вызов callback функции с полученной информацией
                callback(infoChild)
            }

            // Функция, вызываемая при отмене запроса
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
