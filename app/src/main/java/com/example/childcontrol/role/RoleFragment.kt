package com.example.childcontrol.role

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentRoleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RoleFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: FragmentRoleBinding

    private lateinit var editor: SharedPreferences.Editor

    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var roleRepository: RoleRepository

    private lateinit var roleViewModelFactory: RoleViewModelFactory

    private lateinit var roleViewModel: RoleViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки данных
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_role, container, false)
        // Получение ссылки на SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyRoles", Context.MODE_PRIVATE)
        // Получение редактора SharedPreferences
        editor = sharedPreferences.edit()

        // Установка слушателя нажатия на кнопку "Родитель"
        binding.parentButton.setOnClickListener { view: View ->
            // Переход к фрагменту "Главный родитель"
            view.findNavController().navigate(R.id.action_roleFragment_to_headParentFragment)
            // Сохранение роли "Родитель" в SharedPreferences
            editor.putString("role", "parent")
            editor.apply()
        }
        // Инициализация FirebaseAuth и FirebaseDatabase
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // Создание репозитория и ViewModel
        roleRepository = RoleRepository(mAuth, database)
        roleViewModelFactory = RoleViewModelFactory(roleRepository)
        roleViewModel = ViewModelProvider(this, roleViewModelFactory)[RoleViewModel::class.java]
        // Привязка ViewModel к привязке данных
        binding.roleViewModel = roleViewModel
        // Наблюдение за изменениями в информации о ребенке
        roleViewModel.infoChild.observe(viewLifecycleOwner, Observer {
            // Если информация о ребенке не пуста, переход к фрагменту "Главный ребенок"
            if (it != null) {
                this.findNavController()
                    .navigate(RoleFragmentDirections.actionRoleFragmentToHeadChildFragment())
                // Сохранение роли "Ребенок" в SharedPreferences
                editor.putString("role", "child")
                editor.apply()
            } else {
                // Если информация о ребенке пуста, переход к фрагменту "Добавить ребенка"
                this.findNavController().navigate(
                    R.id.action_roleFragment_to_addChildFragment,
                    bundleOf("role" to "child")
                )
                // Сохранение роли "Добавить ребенка" в SharedPreferences
                editor.putString("role", "addChild")
                editor.apply()
            }
        })
        // Установка слушателя нажатия на кнопку "Ребенок"
        binding.childButton.setOnClickListener { view: View ->
            // Переход к фрагменту "Главный ребенок"
            view.findNavController().navigate(R.id.action_roleFragment_to_headChildFragment)
        }
        // Возврат корневого элемента привязки данных
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Создание callback для обработки нажатия кнопки "Назад"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Предотвращение перехода к предыдущему фрагменту
                requireActivity().supportFragmentManager.popBackStack(
                    R.id.authFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                // Завершение активности
                requireActivity().finish()
            }
        }
        // Добавление callback к диспетчеру нажатия кнопки "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
