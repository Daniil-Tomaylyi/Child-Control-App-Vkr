package com.example.childcontrol.tittle


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentTittleBinding

class TittleFragment : Fragment() {

    private lateinit var binding: FragmentTittleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки данных
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tittle, container, false)
        // Установка слушателя нажатия на кнопку "Создать заголовок"
        binding.buttonCreateTittle.setOnClickListener { view: View ->
            // Переход к фрагменту регистрации
            view.findNavController().navigate(R.id.action_tittleFragment_to_regFragment)
        }
        // Установка слушателя нажатия на кнопку "Авторизация"
        binding.buttonAuthTittle.setOnClickListener { view: View ->
            // Переход к фрагменту авторизации с передачей параметра "typeAuth" равного "auth"
            view.findNavController().navigate(
                R.id.action_tittleFragment_to_authFragment,
                bundleOf("typeAuth" to "auth")
            )
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
                    R.id.startFragment,
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


