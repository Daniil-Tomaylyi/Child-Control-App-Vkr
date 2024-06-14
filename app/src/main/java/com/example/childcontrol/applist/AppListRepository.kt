package com.example.childcontrol.applist


import com.example.childcontrol.headchild.AppInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AppListRepository(private val mAuth: FirebaseAuth, private val database: FirebaseDatabase) {
    // Получение идентификатора текущего пользователя
    private val userID = mAuth.currentUser?.uid

    // Ссылки на списки приложений и заблокированных приложений в базе данных Firebase
    private val appListRef = database.reference.child("appList").child(userID!!)
    private val lockAppListRef = database.reference.child("lockAppList").child(userID!!)

    // Локальный список приложений
    private var appList: List<AppInfo>? = null

    // Функция для получения списка приложений из базы данных Firebase
    fun getAppList(callback: (List<AppInfo>?) -> Unit)  {
        appListRef.addValueEventListener(object : ValueEventListener {
            // Обработка изменений данных в базе данных Firebase
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Преобразование данных из Firebase в список объектов AppInfo
                appList = dataSnapshot.children.mapNotNull { it.getValue(AppInfo::class.java) }
                callback(appList)
            }

            // Обработка отмены запроса к базе данных Firebase
            override fun onCancelled(error: DatabaseError) {
            }
        })
        // Возвращение списка приложений
    }

    // Функция для установки заблокированных приложений в базе данных Firebase
    suspend fun setLockApps(appID: Int, lockApp: Any) {
        // Выполнение операции ввода-вывода в отдельном потоке
        withContext(Dispatchers.IO) {
            // Установка значения заблокированного приложения в базе данных Firebase
            lockAppListRef.child(appID.toString()).setValue(lockApp)
        }
    }
}
