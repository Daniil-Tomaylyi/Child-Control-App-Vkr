package com.example.childcontrol.devicetimesettings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.childcontrol.R
import com.example.childcontrol.databinding.ItemDeviceBannedTimeBinding
import com.example.childcontrol.db.DeviceBannedTime
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class DeviceBannedTimeAdapter(private val DeviceTimeSettingsViewModel: DeviceTimeSettingsViewModel) :
    RecyclerView.Adapter<DeviceBannedTimeAdapter.DeviceBannedTimeViewHolder>() {
    var data: List<DeviceBannedTime> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    class DeviceBannedTimeViewHolder(val binding: ItemDeviceBannedTimeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceBannedTimeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDeviceBannedTimeBinding.inflate(inflater, parent, false)
        return DeviceBannedTimeViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DeviceBannedTimeViewHolder, position: Int) {
        val BannedTimelist = data[position]
        val startTimeHours = String.format("%02d", BannedTimelist.start_time_hours)
        val startTimeMinutes = String.format("%02d", BannedTimelist.start_time_minutes)
        val endTimeHours = String.format("%02d", BannedTimelist.end_time_hours)
        val endTimeMinutes = String.format("%02d", BannedTimelist.end_time_minutes)
        val context = holder.itemView.context
        with(holder.binding) {
            deviceBannedTimeText.text = context.getString(
                R.string.banned_time,
                startTimeHours,
                startTimeMinutes,
                endTimeHours,
                endTimeMinutes
            )
            // Удаление времени блокировки при нажатии на кнопку удаления
            deleteDeviceBannedTimeButton.setOnClickListener {
                DeviceTimeSettingsViewModel.delDeviceBannedTime(
                    BannedTimelist.uid,
                    BannedTimelist.id
                )
            }
            // Отображение диалога редактирования времени блокировки при нажатии на кнопку изменения
            changeDeviceBannedTimeButton.setOnClickListener {
                showEditStartTimePicker(
                    DeviceTimeSettingsViewModel,
                    BannedTimelist,
                    holder.itemView.findFragment<Fragment>().childFragmentManager
                )
            }
        }
    }
}

// Функция для отображения диалога выбора времени начала блокировки
private fun showEditStartTimePicker(
    deviceTimeSettingsViewModel: DeviceTimeSettingsViewModel,
    bannedTimeList: DeviceBannedTime,
    childFragmentManager: FragmentManager,
) {
    val startEditTimePicker = MaterialTimePicker.Builder()
        .setTitleText("Время начала блокировки")
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setHour(bannedTimeList.start_time_hours)
        .setMinute(bannedTimeList.start_time_minutes)
        .build()
    startEditTimePicker.show(childFragmentManager, "editStartTimePicker")
    startEditTimePicker.addOnPositiveButtonClickListener {
        showEditEndTimePicker(
            deviceTimeSettingsViewModel,
            startEditTimePicker.hour,
            startEditTimePicker.minute,
            bannedTimeList,
            childFragmentManager
        )
    }
}

// Функция для отображения диалога выбора времени окончания блокировки
fun showEditEndTimePicker(
    deviceTimeSettingsViewModel: DeviceTimeSettingsViewModel,
    startEditHour: Int,
    startEditMinute: Int,
    bannedTimeList: DeviceBannedTime,
    childFragmentManager: FragmentManager
) {
    val endEditTimePicker = MaterialTimePicker.Builder()
        .setTitleText("Установите время окончания блокировки")
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setHour(bannedTimeList.end_time_hours)
        .setMinute(bannedTimeList.end_time_minutes)
        .build()
    endEditTimePicker.show(childFragmentManager, "endEditTimePicker")
    endEditTimePicker.addOnPositiveButtonClickListener {
        val endEditHour = endEditTimePicker.hour
        val endEditMinute = endEditTimePicker.minute
        deviceTimeSettingsViewModel.updateDeviceBannedTime(
            bannedTimeList.id,
            bannedTimeList.uid,
            startEditHour,
            startEditMinute,
            endEditHour,
            endEditMinute
        )
    }
}

