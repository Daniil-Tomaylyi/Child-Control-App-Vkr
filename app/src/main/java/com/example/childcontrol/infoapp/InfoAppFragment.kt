package com.example.childcontrol.infoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentInfoAppBinding


class InfoAppFragment : Fragment() {
    // Инициализация привязки данных
    private lateinit var binding: FragmentInfoAppBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Раздувания макета для этого фрагмента
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_info_app, container, false
        )
        // Установка обработчика нажатия на элементы меню в тулбаре
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // При нажатии на кнопку "назад" возвращаемся к предыдущему фрагменту
                R.id.action_back -> {
                    requireActivity().supportFragmentManager.popBackStack()
                    true
                }
                // Если нажата другая кнопка, не выполняем никаких действий
                else -> false
            }
        }

        // Возвращаем корневой элемент привязки данных в качестве представления фрагмента
        return binding.root
    }
}
