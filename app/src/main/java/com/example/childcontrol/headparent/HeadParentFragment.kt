package com.example.childcontrol.headparent


import android.os.Bundle
import android.util.Log
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
import kotlin.math.log


class HeadParentFragment : Fragment() {
    private lateinit var binding: FragmentHeadParentBinding
    private lateinit var mapview: MapView
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placemarkMapObject: PlacemarkMapObject
    private var zoomValue: Float = 16.5f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            com.example.childcontrol.R.layout.fragment_head_parent,
            container,
            false
        )


        mapview = binding.mapHeadParent
        binding.buttonHeadParentMap.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_headParentFragment_to_parentMapFragment)
        }
        binding.buttonLockApp.setOnClickListener {
            it.findNavController().navigate(R.id.action_headParentFragment_to_appListFragment)
        }
        binding.buttonAddChildHeadParent.setOnClickListener() {
            it.findNavController().navigate(
                R.id.action_headParentFragment_to_addChildFragment,
                bundleOf("role" to "parent")
            )
        }
        binding.buttonHeadParentSettings.setOnClickListener() {
            it.findNavController()
                .navigate(R.id.action_headParentFragment_to_settingsParentFragment)
        }
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val HeadParentviewModelFactory = HeadParentViewModelFactory(mAuth, database)
        val HeadParentViewModel =
            ViewModelProvider(this, HeadParentviewModelFactory)[HeadParentViewModel::class.java]
        binding.headParentViewModel = HeadParentViewModel
        Log.w("statusswitchlock", HeadParentViewModel.DeviceLock.value.toString())
        HeadParentViewModel.infoChild.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.buttonAddChildHeadParent.visibility = View.GONE
                val age = Calendar.getInstance().get(Calendar.YEAR) - it.yearBirth.toInt()
                binding.textInfoChild.text = "${it.name} ${age} лет"
                binding.textInfoChild.visibility = View.VISIBLE
            }
        })
        HeadParentViewModel.getChildInfo()
        HeadParentViewModel.locationChild.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val startLocation = Point(it.latitude, it.longitude)
                moveToStartLocation(startLocation)
                setMarkerInStartLocation(startLocation)
            }
        })
        HeadParentViewModel.appList.observe(viewLifecycleOwner, Observer {
            val sortedAppList =
                it?.sortedWith(compareByDescending<AppInfo> { it.usageHours }.thenByDescending { it.usageMinutes })
                    ?.take(3)
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
        HeadParentViewModel.getAppList()
        HeadParentViewModel.usageDevice.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.timeSpentDeviceValueText.text =
                    "часов: ${it.usageHours} минут: ${it.usageMinutes}"
            }
        })
        HeadParentViewModel.getDeviceUsage()
        binding.switchlockdevice.setOnCheckedChangeListener { _, isChecked ->
            HeadParentViewModel.setLockDeviceState(isChecked)
        }
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