package com.example.childcontrol.settingsparent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentSettingsParentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsParentFragment : Fragment() {

    private lateinit var binding: FragmentSettingsParentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings_parent, container, false)
        binding.buttonInfoAppParent.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingsParentFragment_to_infoAppFragment)
        }
        binding.buttonSettingsTimeDevice.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_settingsParentFragment_to_deviceTimeSettingsFragment)
        }
        binding.buttonParentSettingsMap.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingsParentFragment_to_parentMapFragment)
        }
        binding.buttonParentSettingsReport.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_settingsParentFragment_to_headParentFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.changeThemeParentSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.w("Theme", "Theme_ChildControl_black")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            } else {
                Log.w("Theme", "Theme_ChildControl_white")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}