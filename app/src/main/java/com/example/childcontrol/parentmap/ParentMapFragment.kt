package com.example.childcontrol.parentmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentParentMapBinding
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

class ParentMapFragment : Fragment() {
    private lateinit var binding: FragmentParentMapBinding

    private lateinit var mapview: MapView

    private lateinit var mapObjectCollection: MapObjectCollection

    private lateinit var placemarkMapObject: PlacemarkMapObject

    private lateinit var mAuth: FirebaseAuth

    private lateinit var parentMapRepository: ParentMapRepository

    private lateinit var parentMapViewModelFactory: ParentMapViewModelFactory

    private lateinit var parentMapViewModel: ParentMapViewModel

    private lateinit var database: FirebaseDatabase

    private var zoomValue: Float = 16.5f

    private var startLocation: Point? = null

    private var marker: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки данных
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_parent_map, container, false)
        // Настройка обработчиков нажатия кнопок
        binding.buttonParentMapReport.setOnClickListener { view: View ->
            // Переход к фрагменту headParentFragment при нажатии на buttonParentMapReport
            view.findNavController().navigate(R.id.action_parentMapFragment_to_headParentFragment)
        }
        binding.buttonParentMapSettings.setOnClickListener {
            // Переход к фрагменту settingsParentFragment при нажатии на buttonParentMapSettings
            it.findNavController().navigate(R.id.action_parentMapFragment_to_settingsParentFragment)
        }
        // Инициализация карты
        mapview = binding.mapParent
        // Инициализация Firebase Auth и Database
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // Инициализация репозитория и ViewModel
        parentMapRepository = ParentMapRepository(mAuth, database)
        parentMapViewModelFactory = ParentMapViewModelFactory(parentMapRepository)
        parentMapViewModel =
            ViewModelProvider(this, parentMapViewModelFactory)[ParentMapViewModel::class.java]
        // Наблюдение за местоположением ребенка
        parentMapViewModel.locationChild.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                // Если местоположение ребенка получено, перемещаемся туда и устанавливаем маркер
                startLocation = Point(it.latitude, it.longitude)
                moveToStartLocation(startLocation!!)
                setMarkerInStartLocation(startLocation!!)
            }
        })
        // Получение местоположения ребенка
        parentMapViewModel.getLocation()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        // Остановка карты и MapKit при остановке фрагмента
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        // Запуск MapKit и карты при старте фрагмента
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    private fun moveToStartLocation(Location: Point) {
        // Перемещение карты к указанной точке с анимацией
        mapview.map.move(
            CameraPosition(Location, zoomValue, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    private fun setMarkerInStartLocation(Location: Point) {
        // Установка маркера в указанной точке
        marker = R.drawable.ic_pin_black // Добавляем ссылку на картинку
        mapObjectCollection =
            mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        placemarkMapObject = mapObjectCollection.addPlacemark(
            Location,
            ImageProvider.fromResource(this.requireContext(), marker)
        ) // Добавляем метку со значком
        placemarkMapObject.opacity = 0.5f // Устанавливаем прозрачность метке
        placemarkMapObject.setText("Ваш ребенок") // Устанавливаем текст сверху метки
    }


}