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
import com.example.childcontrol.BuildConfig
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentParentMapBinding
import com.example.childcontrol.headparent.HeadParentViewModel
import com.example.childcontrol.headparent.HeadParentViewModelFactory
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
    private var zoomValue: Float = 16.5f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_parent_map, container, false)
        binding.buttonParentMapReport.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_parentMapFragment_to_headParentFragment)
        }
        binding.buttonParentMapSettings.setOnClickListener {
            it.findNavController().navigate(R.id.action_parentMapFragment_to_settingsParentFragment)
        }
        mapview = binding.mapParent
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val viewModelFactory = HeadParentViewModelFactory(mAuth, database)
        val HeadParentViewModel =
            ViewModelProvider(this, viewModelFactory)[HeadParentViewModel::class.java]
        HeadParentViewModel.locationChild.observe(viewLifecycleOwner, Observer { location ->
            val startLocation = Point(location!!.latitude, location.longitude)
            moveToStartLocation(startLocation)
            setMarkerInStartLocation(startLocation)
        })
        HeadParentViewModel.getLocation()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    private fun moveToStartLocation(Location: Point) {
        mapview.map.move(
            CameraPosition(Location, zoomValue, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    private fun setMarkerInStartLocation(Location: Point) {
        val marker = R.drawable.ic_pin_black // Добавляем ссылку на картинку
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