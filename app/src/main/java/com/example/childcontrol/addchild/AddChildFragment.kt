package com.example.childcontrol.addchild


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentAddChildBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddChildFragment : Fragment() {

    private lateinit var binding: FragmentAddChildBinding

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private var userRole: String? = null

    private lateinit var addChildRepository: AddChildRepository

    private lateinit var viewModelFactory: AddChildViewModelFactory

    private lateinit var addChildViewModel: AddChildViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Привязка данных к макету фрагмента
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_child, container, false)

        // Получение роли пользователя из аргументов
        userRole = requireArguments().getString("role")

        // Инициализация SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyRoles", Context.MODE_PRIVATE)

        // Инициализация FirebaseAuth и FirebaseDatabase
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // Создание репозитория для добавления ребенка, используя FirebaseAuth и FirebaseDatabase
        addChildRepository = AddChildRepository(mAuth, database)
        // Создание фабрики ViewModel с помощью FirebaseAuth и FirebaseDatabase
        viewModelFactory = AddChildViewModelFactory(addChildRepository)

        // Создание экземпляра ViewModel
        addChildViewModel =
            ViewModelProvider(this, viewModelFactory)[AddChildViewModel::class.java]

        // Привязка ViewModel к макету
        binding.addChildViewModel = addChildViewModel

        // Наблюдение за изменением имени ребенка в ViewModel
        addChildViewModel.name.observe(viewLifecycleOwner, Observer { nameChild ->
            // Если имя ребенка пустое или null, кнопка добавления ребенка становится неактивной
            if (nameChild.isNullOrBlank()) {
                binding.buttonAddChild.isEnabled = false
            } else {
                // Если имя ребенка не пустое, наблюдаем за изменением года рождения ребенка
                addChildViewModel.yearBirth.observe(viewLifecycleOwner, Observer { birthYearChild ->
                    // Если год рождения ребенка не пустой, кнопка добавления ребенка становится активной
                    if (!birthYearChild.isNullOrBlank()) {
                        binding.buttonAddChild.isEnabled = true
                    } else {
                        // Если год рождения ребенка пустой, кнопка добавления ребенка становится неактивной
                        binding.buttonAddChild.isEnabled = false
                    }
                })
            }
        })

        // Наблюдение за готовностью перехода в ViewModel
        addChildViewModel.isReadyToNavigate.observe(viewLifecycleOwner, Observer {
            // Если готовы к переходу
            if (it == true) {
                // Если роль пользователя - "родитель", переходим к HeadParentFragment
                if (userRole == "parent") {
                    this.findNavController()
                        .navigate(AddChildFragmentDirections.actionAddChildFragmentToHeadParentFragment())
                }
                // Если роль пользователя - "ребенок", переходим к HeadChildFragment
                else if (userRole == "child") {
                    this.findNavController()
                        .navigate(AddChildFragmentDirections.actionAddChildFragmentToHeadChildFragment())
                }
            }
        })

        // Возвращаем корневое представление привязки данных
        return binding.root
    }
}
