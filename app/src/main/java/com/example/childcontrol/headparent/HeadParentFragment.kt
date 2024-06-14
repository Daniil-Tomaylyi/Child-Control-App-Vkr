package com.example.childcontrol.headparent


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.childcontrol.headchild.AppInfo
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentHeadParentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import java.util.Calendar



class HeadParentFragment : Fragment() {
    private lateinit var binding: FragmentHeadParentBinding

    private lateinit var mapview: MapView

    private lateinit var mapObjectCollection: MapObjectCollection

    private lateinit var placemarkMapObject: PlacemarkMapObject

    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var headParentRepository: HeadParentRepository

    private lateinit var headParentviewModelFactory: HeadParentViewModelFactory

    private lateinit var headParentViewModel: HeadParentViewModel

    private var zoomValue: Float = 16.5f

    private var age: Int = 0

    private var startLocation: Point? = null

    private var marker: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Инициализация привязки данных
        binding = DataBindingUtil.inflate(
            inflater,
            com.example.childcontrol.R.layout.fragment_head_parent,
            container,
            false
        )

        // Инициализация карты
        mapview = binding.mapHeadParent

        // Обработчики нажатия кнопок для перехода к другим фрагментам
        binding.buttonHeadParentMap.setOnClickListener { view: View ->
            // Переход к фрагменту карты
            view.findNavController().navigate(R.id.action_headParentFragment_to_parentMapFragment)
        }
        binding.buttonLockApp.setOnClickListener {
            // Переход к фрагменту списка приложений
            it.findNavController().navigate(R.id.action_headParentFragment_to_appListFragment)
        }
        binding.buttonAddChildHeadParent.setOnClickListener() {
            // Переход к фрагменту добавления ребенка
            it.findNavController().navigate(
                R.id.action_headParentFragment_to_addChildFragment,
                bundleOf("role" to "parent")
            )
        }
        binding.buttonHeadParentSettings.setOnClickListener() {
            // Переход к фрагменту настроек родителя
            it.findNavController()
                .navigate(R.id.action_headParentFragment_to_settingsParentFragment)
        }

        // Инициализация FirebaseAuth, FirebaseDatabase и ViewModel
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        headParentRepository = HeadParentRepository(mAuth, database)
        headParentviewModelFactory = HeadParentViewModelFactory(headParentRepository)
        headParentViewModel =
            ViewModelProvider(this, headParentviewModelFactory)[HeadParentViewModel::class.java]
        binding.headParentViewModel = headParentViewModel

        // Наблюдение за изменениями в LiveData объектах ViewModel и обновление UI
        headParentViewModel.infoChild.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                // Если информация о ребенке доступна, скрываем кнопку добавления ребенка
                binding.buttonAddChildHeadParent.visibility = View.GONE
                // Вычисляем возраст ребенка
                age = Calendar.getInstance().get(Calendar.YEAR) - it.yearBirth.toInt()
                // Отображаем информацию о ребенке
                binding.textInfoChild.text = "${it.name} ${age} лет"
                binding.textInfoChild.visibility = View.VISIBLE
            }
        })
        // Получаем информацию о ребенке
        headParentViewModel.getChildInfo()
        headParentViewModel.locationChild.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                // Если доступна геолокация ребенка, перемещаемся к этой локации на карте
                startLocation = Point(it.latitude, it.longitude)
                moveToStartLocation(startLocation!!)
                // Устанавливаем маркер в этой локации
                setMarkerInStartLocation(startLocation!!)
            }
        })
        headParentViewModel.appList.observe(viewLifecycleOwner, Observer {
            // Сортируем список приложений по времени использования
            val sortedAppList =
                it?.sortedWith(compareByDescending<AppInfo> { it.usageHours }.thenByDescending { it.usageMinutes })
                    ?.take(3)
            // Отображаем три самых используемых приложения
            val textApps = listOf(binding.textAppStat1, binding.textAppStat2, binding.textAppStat3)
            if (sortedAppList != null) {
                sortedAppList.forEachIndexed { i, app ->
                    if (app.usageHours.toInt() != 0 || app.usageMinutes.toInt() != 0) {
                        textApps[i].text =
                            "${app.name} часов: ${app.usageHours} минут: ${app.usageMinutes} "
                    }
                }

            }
        })
        // Получаем список приложений
        headParentViewModel.getAppList()
        headParentViewModel.usageDevice.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                // Отображаем общее время использования устройства
                binding.timeSpentDeviceValueText.text =
                    "часов: ${it.usageHours} минут: ${it.usageMinutes}"
            }
        })
        // Получаем информацию об использовании устройства
        headParentViewModel.getDeviceUsage()
        // Устанавливаем обработчик переключателя блокировки устройства
        binding.switchlockdevice.setOnCheckedChangeListener { _, isChecked ->
            headParentViewModel.setLockDeviceState(isChecked)
        }
        // Получаем геолокацию ребенка
        headParentViewModel.getLocation()

        return binding.root

    }


    override fun onStop() {
        super.onStop()
        // Останавливаем карту при остановке фрагмента
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        // Запускаем карту при старте фрагмента
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    private fun moveToStartLocation(location: Point) {
        // Перемещаем камеру к указанной локации
        mapview.map.move(
            CameraPosition(location, zoomValue, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    private fun setMarkerInStartLocation(location: Point) {
        // Устанавливаем маркер в указанной локации
        marker = R.drawable.ic_pin_black // Добавляем ссылку на картинку
        mapObjectCollection =
            mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        placemarkMapObject = mapObjectCollection.addPlacemark(
            location,
            ImageProvider.fromResource(this.requireContext(), marker)
        ) // Добавляем метку со значком
        placemarkMapObject.opacity = 0.5f // Устанавливаем прозрачность метке
        placemarkMapObject.setText("Ваш ребенок") // Устанавливаем текст сверху метки
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Предотвращаем переход к предыдущему фрагменту
                requireActivity().supportFragmentManager.popBackStack(
                    R.id.roleFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
