package com.example.childcontrol.headchild

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.childcontrol.Service.AppBlockerService
import com.example.childcontrol.Service.DeviceBlockerService
import com.example.childcontrol.Service.LocationService
import com.example.childcontrol.R
import com.example.childcontrol.admin.MyDeviceAdminReceiver
import com.example.childcontrol.databinding.FragmentHeadChildBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HeadChildFragment : Fragment() {
    private lateinit var binding: FragmentHeadChildBinding
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName
    private lateinit var notificationManager: NotificationManager
    private lateinit var appOps: AppOpsManager

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
        notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        checkPermissions()
        HeadChildViewModel.updateAppList()
        HeadChildViewModel.usageDevice.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.w("usageMinutes", it.usageMinutes.toString())
                binding.timeSpentDeviceValueText.text =
                    "часов: ${it.usageHours} минут: ${it.usageMinutes} "
            }
        })
        HeadChildViewModel.updateUsageDevice()
        deviceLocker()
        appLocker()
        locationUpdater()
        return binding.root
    }

    private fun locationUpdater() {
        val intent = Intent(requireActivity(), LocationService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        requireActivity().startForegroundService(intent)
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


    private fun appLocker() {
        val intent = Intent(requireActivity(), AppBlockerService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        requireActivity().startForegroundService(intent)
    }

    private fun deviceLocker() {
        val intent = Intent(requireActivity(), DeviceBlockerService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        requireActivity().startForegroundService(intent)
    }


    private fun checkPermissionsWindow() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireContext().packageName)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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
    private fun checkPermissions() = CoroutineScope(Dispatchers.Main).launch{
        appOps = requireActivity().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), requireActivity().packageName
        )

        if (!devicePolicyManager.isAdminActive(componentName)) {
            checkPermissionAdmin()
        }
        delay(10000)
        if (!Settings.canDrawOverlays(context)) {
            checkPermissionsWindow()
        }
        delay(10000)
        if (!notificationManager.areNotificationsEnabled()) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            context?.startActivity(intent)
        }
        delay(10000)
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermissionsLocation()
        }
        delay(10000)
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    private fun checkPermissionsLocation() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    delay(3000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    val permissionlistener = object : PermissionListener {
                        override fun onPermissionGranted() {

                        }

                        override fun onPermissionDenied(deniedPermissions: List<String>) {

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
}