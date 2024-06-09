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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class DeviceTimeSettingsFragment : Fragment() {
    private lateinit var binding: FragmentDeviceTimeSettingsBinding
    private val limitHours = (0..23).map { "$it ч" }
    private lateinit var adapter: DeviceBannedTimeAdapter
    private lateinit var startTimePicker: MaterialTimePicker
    private lateinit var endTimePicker: MaterialTimePicker
    private var endHour: Int = 0
    private var endMinute: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_device_time_settings, container, false
        )
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val childDatabase = ChildDatabase.getInstance(requireContext()).childDatabaseDao
        val deviceTimeSettingsViewModelFactory =
            DeviceTimeSettingsViewModelFactory(mAuth, database, childDatabase)
        val deviceTimeSettingsViewModel = ViewModelProvider(
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
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, limitHours)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.apply {
            limitTimeDeviceSpinner.adapter = arrayAdapter
            deviceBannedTimeRecyclerView.adapter = adapter
            deviceBannedTimeRecyclerView.layoutManager = LinearLayoutManager(context)
        }
        deviceTimeSettingsViewModel.bannedTime.observe(viewLifecycleOwner, Observer {
            if (it!!.all { item -> item != null }) {
                Log.w("BannedTimelist", it.size.toString())
                adapter.data = it
            }
        })
        deviceTimeSettingsViewModel.getDeviceBannedTime()
        binding.settingsTimeDeviceSaveButton.setOnClickListener {
            deviceTimeSettingsViewModel.saveTimeSettings(binding.limitTimeDeviceSpinner.selectedItem.toString())
            it.findNavController()
                .navigate(R.id.action_deviceTimeSettingsFragment_to_settingsParentFragment)
        }
        return binding.root
    }

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