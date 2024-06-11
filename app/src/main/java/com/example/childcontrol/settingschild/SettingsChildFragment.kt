package com.example.childcontrol.settingschild

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentSettingsChildBinding

class SettingsChildFragment : Fragment() {
    private lateinit var binding: FragmentSettingsChildBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings_child, container, false)
        binding.buttonInfoAppChild.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingsChildFragment_to_infoAppFragment)
        }
        binding.buttonHeadChildSettingsTittle.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingsChildFragment_to_headChildFragment)
        }
        binding.buttonDeleteAppChild.setOnClickListener{
            it.findNavController().navigate(R.id.action_settingsChildFragment_to_authFragment, bundleOf("typeAuth" to "deleteapp"))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.changeThemeChildSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}