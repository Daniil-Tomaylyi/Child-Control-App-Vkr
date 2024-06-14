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

    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var appListRepository: AppListRepository

    private lateinit var viewModelFactory: AppListViewModelFactory

    private lateinit var appListViewModel: AppListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            com.example.childcontrol.R.layout.fragment_app_list,
            container,
            false
        )
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        appListRepository = AppListRepository(mAuth, database)
        viewModelFactory = AppListViewModelFactory(appListRepository)
        appListViewModel =
            ViewModelProvider(this, viewModelFactory)[AppListViewModel::class.java]
        // Создание адаптера и установка его для RecyclerView
        adapter = AppListAdapter(appListViewModel)
        binding.appListView.adapter = adapter
        // Установка LayoutManager для RecyclerView
        binding.appListView.layoutManager = LinearLayoutManager(context)
        // Наблюдение за данными в ViewModel и обновление адаптера при изменении данных
        appListViewModel.appList.observe(viewLifecycleOwner, Observer {
            adapter.data = it ?: emptyList()
        })
        // Получение списка приложений
        appListViewModel.getAppList()
        // Возвращение корневого элемента привязки данных
        return binding.root
    }


}