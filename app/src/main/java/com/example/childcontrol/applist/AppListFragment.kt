package com.example.childcontrol.applist


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.childcontrol.databinding.FragmentAppListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AppListFragment : Fragment() {

    private lateinit var binding: FragmentAppListBinding
    private lateinit var adapter: AppListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            com.example.childcontrol.R.layout.fragment_app_list,
            container,
            false
        )
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val viewModelFactory = AppListViewModelFactory(mAuth, database)
        val appListViewModel =
            ViewModelProvider(this, viewModelFactory)[AppListViewModel::class.java]
        adapter = AppListAdapter(appListViewModel)
        binding.appListView.adapter = adapter
        binding.appListView.layoutManager = LinearLayoutManager(context)
        appListViewModel.appList.observe(viewLifecycleOwner, Observer {
            adapter.data = it ?: emptyList()
        })
        appListViewModel.getAppList()
        return binding.root
    }


}