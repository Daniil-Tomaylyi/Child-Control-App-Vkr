package com.example.childcontrol.reg


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentDialogBinding
import com.example.childcontrol.databinding.FragmentRegBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegFragment : Fragment() {

    private lateinit var binding: FragmentRegBinding

    private lateinit var dialogBinding: FragmentDialogBinding
    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var regRepository: RegRepository

    private lateinit var regViewModelFactory: RegViewModelFactory

    private lateinit var regViewModel: RegViewModel
    private val delay = 500
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки данных для фрагмента регистрации
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reg, container, false)
        // Инициализация привязки данных для диалогового окна
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_dialog,
            null,
            false
        )
        // Установка текста для диалогового окна
        dialogBinding.progressText = "Идет регистрация\nпожалуйста подождите"
        // Создание диалогового окна
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        // Инициализация Firebase Auth и Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // Инициализация репозитория и ViewModel
        regRepository = RegRepository(mAuth, database)
        regViewModelFactory = RegViewModelFactory(regRepository)
        regViewModel = ViewModelProvider(this, regViewModelFactory)[RegViewModel::class.java]
        // Привязка ViewModel к binding
        binding.regViewModel = regViewModel
        // Наблюдение за состоянием диалогового окна
        regViewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            if (it == true)
            // Если нужно показать диалоговое окно, показываем его
                dialog.show()
            else
            // Если нужно скрыть диалоговое окно, скрываем его с задержкой
                Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, delay.toLong())
        })
        // Наблюдение за сообщениями об ошибках
        regViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                // Если есть сообщение об ошибке, показываем его
                binding.regErrorMessage.visibility = View.VISIBLE
            } else {
                // Если ошибки нет, переходим к фрагменту аутентификации
                this.findNavController()
                    .navigate(RegFragmentDirections.actionRegFragmentToAuthFragment())
            }
        })
        // Возвращаем корневой элемент привязки данных
        return binding.root
    }

}