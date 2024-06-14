package com.example.childcontrol.settingsparent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentSettingsParentBinding


class SettingsParentFragment : Fragment() {

    private lateinit var binding: FragmentSettingsParentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings_parent, container, false)
        // Настройка обработчиков нажатия кнопок
        binding.buttonInfoAppParent.setOnClickListener {
            // Переход к фрагменту информации о приложении
            it.findNavController().navigate(R.id.action_settingsParentFragment_to_infoAppFragment)
        }
        binding.buttonSettingsTimeDevice.setOnClickListener {
            // Переход к фрагменту настроек времени устройства
            it.findNavController()
                .navigate(R.id.action_settingsParentFragment_to_deviceTimeSettingsFragment)
        }
        binding.buttonParentSettingsMap.setOnClickListener {
            // Переход к фрагменту карты родителя
            it.findNavController().navigate(R.id.action_settingsParentFragment_to_parentMapFragment)
        }
        binding.buttonParentSettingsReport.setOnClickListener {
            // Переход к фрагменту отчета родителя
            it.findNavController()
                .navigate(R.id.action_settingsParentFragment_to_headParentFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Установка обработчика изменения переключателя темы
        binding.changeThemeParentSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Если переключатель включен, устанавливаем ночной режим
                Log.w("Theme", "Theme_ChildControl_black")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Если переключатель выключен, устанавливаем дневной режим
                Log.w("Theme", "Theme_ChildControl_white")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}
