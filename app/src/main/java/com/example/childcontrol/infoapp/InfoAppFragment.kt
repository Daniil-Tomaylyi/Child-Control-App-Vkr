package com.example.childcontrol.infoapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentInfoAppBinding


class InfoAppFragment : Fragment() {
    private lateinit var binding: FragmentInfoAppBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_info_app, container, false
        )
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_back -> {
                    requireActivity().supportFragmentManager.popBackStack()
                    true
                }

                else -> false
            }
        }

        return binding.root
    }


}