package com.example.childcontrol.devicetimesettings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentDeviceTimeSettingsBinding
import com.example.childcontrol.db.ChildDatabase
import com.example.childcontrol.db.ChildDatabaseDao
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class DeviceTimeSettingsFragment : Fragment() {
    private lateinit var binding: FragmentDeviceTimeSettingsBinding

    private lateinit var adapter: DeviceBannedTimeAdapter

    private lateinit var startTimePicker: MaterialTimePicker

    private lateinit var endTimePicker: MaterialTimePicker

    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var deviceTimeSettingsRepository: DeviceTimeSettingsRepository

    private lateinit var deviceTimeSettingsViewModelFactory: DeviceTimeSettingsViewModelFactory

    private lateinit var deviceTimeSettingsViewModel: DeviceTimeSettingsViewModel

    private lateinit var childDatabase: ChildDatabaseDao

    private lateinit var arrayAdapter: ArrayAdapter<String>

    private val limitHours = (0..23).map { "$it ч" }

    private var endHour: Int = 0

    private var endMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_device_time_settings, container, false
        )
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        childDatabase = ChildDatabase.getInstance(requireContext()).childDatabaseDao
        deviceTimeSettingsRepository = DeviceTimeSettingsRepository(mAuth, database, childDatabase)
        deviceTimeSettingsViewModelFactory =
            DeviceTimeSettingsViewModelFactory(deviceTimeSettingsRepository)
        deviceTimeSettingsViewModel = ViewModelProvider(
            this,
            deviceTimeSettingsViewModelFactory
        )[DeviceTimeSettingsViewModel::class.java]
        binding.settingsTimeDeviceCancelButton.setOnClickListener() {
            it.findNavController()
                .navigate(R.id.action_deviceTimeSettingsFragment_to_settingsParentFragment)
        }
        binding.addDeviceBannedTimeButton.setOnClickListener {
            showStartTimePicker(deviceTimeSettingsViewModel)
        }
        adapter = DeviceBannedTimeAdapter(deviceTimeSettingsViewModel)
        arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, limitHours)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.apply {
            limitTimeDeviceSpinner.adapter = arrayAdapter
            deviceBannedTimeRecyclerView.adapter = adapter
            deviceBannedTimeRecyclerView.layoutManager = LinearLayoutManager(context)
        }
        // Наблюдение за изменениями в bannedTime и обновление адаптера при изменении данных
        deviceTimeSettingsViewModel.bannedTime.observe(viewLifecycleOwner, Observer {
            if (it!!.all { item -> item != null }) {
                Log.w("BannedTimelist", it.size.toString())
                adapter.data = it
            }
        })
        // Получение запрещенного времени устройства
        deviceTimeSettingsViewModel.getDeviceBannedTime()
        // Сохранение настроек времени устройства и переход к другому фрагменту при нажатии кнопки сохранения
        binding.settingsTimeDeviceSaveButton.setOnClickListener {
            deviceTimeSettingsViewModel.saveTimeSettings(binding.limitTimeDeviceSpinner.selectedItem.toString())
            it.findNavController()
                .navigate(R.id.action_deviceTimeSettingsFragment_to_settingsParentFragment)
        }
        return binding.root
    }

    // Функция для отображения выбора времени начала блокировки
    private fun showStartTimePicker(deviceTimeSettingsViewModel: DeviceTimeSettingsViewModel) {
        startTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Установите время начала блокировки")
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .build()
        startTimePicker.show(childFragmentManager, "startTimePicker")
        startTimePicker.addOnPositiveButtonClickListener {
            showEndTimePicker(
                deviceTimeSettingsViewModel,
                startTimePicker.hour,
                startTimePicker.minute
            )
        }
    }

    // Функция для отображения выбора времени окончания блокировки
    private fun showEndTimePicker(
        deviceTimeSettingsViewModel: DeviceTimeSettingsViewModel,
        startHour: Int,
        startMinute: Int
    ) {
        endTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Установите время окончания блокировки")
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .build()
        endTimePicker.show(childFragmentManager, "endTimePicker")
        endTimePicker.addOnPositiveButtonClickListener {
            endHour = endTimePicker.hour
            endMinute = endTimePicker.minute
            deviceTimeSettingsViewModel.setDeviceBannedTime(
                startHour,
                startMinute,
                endHour,
                endMinute
            )
        }
    }



}