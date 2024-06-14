package com.example.childcontrol

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var navController: NavController

    private lateinit var editor: Editor

    private lateinit var sharedPreferencesRoles: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferencesRoles = getSharedPreferences("MyRoles", Context.MODE_PRIVATE)
        navController = findNavController(R.id.NavHostFragment)

        // В зависимости от роли пользователя, переходим к соответствующему фрагменту
        when (sharedPreferencesRoles.getString("role", "")) {
            "parent" -> navController.navigate(R.id.headParentFragment)
            "child" -> navController.navigate(R.id.headChildFragment)
            "addChild" -> navController.navigate(R.id.addChildFragment)
            else -> {
                FirebaseAuth.getInstance().currentUser?.let {
                    // Если пользователь авторизован, переходим к фрагменту роли
                    navController.navigate(R.id.roleFragment)
                } ?: run {
                    if (sharedPreferences.getBoolean("isFirstRun", true)) {
                        // Если это первый запуск, отображаем фрагмент приветствия
                        navController.navigate(R.id.startFragment)
                        // Устанавливаем флаг первого запуска в false
                        editor = sharedPreferences.edit()
                        editor.putBoolean("isFirstRun", false)
                        editor.apply()
                    } else {
                        // Если это не первый запуск, отображаем другой фрагмент
                        navController.navigate(R.id.tittleFragment)
                    }
                }
            }
        }
    }
}
