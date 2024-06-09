package com.example.childcontrol.headchild

import android.Manifest
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.childcontrol.AppBlockerService
import com.example.childcontrol.DeviceBlockerService
import com.example.childcontrol.R
import com.example.childcontrol.admin.MyDeviceAdminReceiver
import com.example.childcontrol.databinding.FragmentHeadChildBinding
import com.example.childcontrol.applist.lockApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HeadChildFragment : Fragment() {
    private lateinit var binding: FragmentHeadChildBinding
    private val desired_accuracy = 0.0
    private val minimal_time: Long = 1000
    private val minimal_distance = 1.0
    private val use_in_background = false
    private var locationManager: LocationManager? = null
    private var myLocationListener: LocationListener? = null
    private var myLocation: Point? = null
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_head_child, container, false)
        devicePolicyManager =
            activity?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        binding.buttonHeadChildTittleSettings.setOnClickListener() {
            it.findNavController().navigate(R.id.action_headChildFragment_to_settingsChildFragment)
        }
        componentName = ComponentName(requireActivity(), MyDeviceAdminReceiver::class.java)
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)
        val packageManager = requireActivity().packageManager
        val usageStatsManager =
            requireActivity().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val viewModelFactory =
            HeadChildViewModelFactory(
                headChildFragmentRepository,
                packageManager,
                usageStatsManager
            )
        val HeadChildViewModel =
            ViewModelProvider(this, viewModelFactory)[HeadChildViewModel::class.java]
        HeadChildViewModel.usageDevice.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.w("usageMinutes", it.usageMinutes.toString())
                binding.timeSpentDeviceValueText.text =
                    "часов: ${it.usageHours} минут: ${it.usageMinutes} "
            }
        })
        HeadChildViewModel.updateUsageDevice()
        if (devicePolicyManager.isAdminActive(componentName)) {
            if (Settings.canDrawOverlays(context)) {
                deviceLocker()
                appLocker()
            } else {
                checkPermissionsWindow()
            }
        } else {
            checkPermissionAdmin()
        }
        Log.w("isAdminActive", devicePolicyManager.isAdminActive(componentName).toString())
        checkPermissionStats()
        checkPermissions()
        HeadChildViewModel.updateAppList()

        return binding.root
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

    private fun checkPermissionsWindow() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireContext().packageName)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun appLocker() {
        val intent = Intent(context, AppBlockerService::class.java)
        context?.startForegroundService(intent)
    }

    private fun checkPermissionStats() {
        val appOps = activity?.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
        val mode = appOps!!.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), activity?.getPackageName()!!
        )
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }


    private fun deviceLocker() {
        val intent = Intent(context, DeviceBlockerService::class.java)
        context?.startForegroundService(intent)
    }


    private fun checkPermissionAdmin() {


        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Пожалуйста, разрешите приложению получить права администратора устройства"
        )

        startActivity(intent)

    }


    private fun checkPermissions() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    delay(3000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    val permissionlistener = object : PermissionListener {
                        override fun onPermissionGranted() {
                            Toast.makeText(
                                requireActivity(),
                                "Доступ разрешен",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onPermissionDenied(deniedPermissions: List<String>) {
                            Toast.makeText(
                                requireActivity(),
                                "Доступ запрещен\n" + deniedPermissions.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("Пожалуйста предоставьте доступ к местоположению")
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .check()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val headChildFragmentRepository = HeadChildFragmentRepository(mAuth, database)
        val packageManager = requireActivity().packageManager
        val usageStatsManager =
            requireActivity().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val viewModelFactory =
            HeadChildViewModelFactory(
                headChildFragmentRepository,
                packageManager,
                usageStatsManager
            )
        val HeadChildViewModel =
            ViewModelProvider(this, viewModelFactory)[HeadChildViewModel::class.java]

        MapKitFactory.initialize(this.requireContext());
        locationManager = MapKitFactory.getInstance().createLocationManager()
        myLocationListener = object : LocationListener {
            override fun onLocationUpdated(location: Location) {
                myLocation = location.position
                //  Log.w("loc", "my location - ${myLocation?.latitude},${myLocation?.longitude}")
                HeadChildViewModel.updateLocation(myLocation?.latitude, myLocation?.longitude)
            }

            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
                if (locationStatus == LocationStatus.NOT_AVAILABLE) {
                    //  Log.w("locavaible", "Местоположение недоступно")
                }
            }
        }
        subscribeToLocationUpdate()
    }


    override fun onStop() {
        super.onStop()
        locationManager?.unsubscribe(myLocationListener!!)
    }

    private fun subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager?.subscribeForLocationUpdates(
                desired_accuracy,
                minimal_time,
                minimal_distance,
                use_in_background,
                FilteringMode.OFF,
                myLocationListener!!
            )
        }
    }
}