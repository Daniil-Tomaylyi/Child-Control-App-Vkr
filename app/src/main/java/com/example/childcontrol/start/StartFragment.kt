package com.example.childcontrol.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentStartBinding


class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    // Переопределение функции onCreateView, которая вызывается для создания иерархии представлений, связанных с фрагментом.
    override fun onCreateView(
        inflater: LayoutInflater, // Объект LayoutInflater, который можно использовать для создания представлений из XML макета
        container: ViewGroup?, // Родительское представление, к которому будет прикреплено представление фрагмента
        savedInstanceState: Bundle? // Если не равно null, фрагмент восстанавливается из предыдущего сохраненного состояния
    ): View {
        // Инициализация привязки данных, используя DataBindingUtil для раздувания макета fragment_start
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)

        // Установка слушателя нажатия кнопки StartButton
        binding.StartButton.setOnClickListener { view: View ->
            // При нажатии на кнопку, осуществляется навигация к tittleFragment
            view.findNavController().navigate(R.id.action_startFragment_to_tittleFragment)
        }

        // Возвращение корневого представления для текущего макета (с привязкой данных)
        return binding.root
    }

}