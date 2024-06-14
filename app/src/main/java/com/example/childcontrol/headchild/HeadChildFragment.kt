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
    // Объявление переменных для различных сервисов и компонентов
    private lateinit var binding: FragmentHeadChildBinding

    private lateinit var devicePolicyManager: DevicePolicyManager

    private lateinit var componentName: ComponentName

    private lateinit var notificationManager: NotificationManager

    private lateinit var appOps: AppOpsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_head_child, container, false)

        // Получение сервиса управления политикой устройства
        devicePolicyManager =
            activity?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        // Установка слушателя кликов для кнопки
        binding.buttonHeadChildTittleSettings.setOnClickListener() {
            // Переход к фрагменту настроек при нажатии на кнопку
            it.findNavController().navigate(R.id.action_headChildFragment_to_settingsChildFragment)
        }

        componentName = ComponentName(requireActivity(), MyDeviceAdminReceiver::class.java)

        // Получение сервиса управления уведомлениями
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

        // Проверка разрешений
        checkPermissions()

        // Обновление списка приложений
        HeadChildViewModel.updateAppList()

        // Наблюдение за использованием устройства
        HeadChildViewModel.usageDevice.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                // Запись статистики использования в журнал
                Log.w("usageMinutes", it.usageMinutes.toString())

                // Обновление текста с информацией о времени использования устройства
                binding.timeSpentDeviceValueText.text =
                    "часов: ${it.usageHours} минут: ${it.usageMinutes} "
            }
        })

        // Обновление статистики использования устройства
        HeadChildViewModel.updateUsageDevice()

        // Запуск сервисов блокировки устройства и приложений
        deviceLocker()
        appLocker()

        // Обновление местоположения
        locationUpdater()

        return binding.root
    }

    // Метод для обновления местоположения
    private fun locationUpdater() {
        // Создание намерения для запуска сервиса
        val intent = Intent(requireActivity(), LocationService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        // Запуск сервиса в переднем плане
        requireActivity().startForegroundService(intent)
    }

    // Метод, вызываемый после создания представления фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Создание обратного вызова для обработки нажатия кнопки "Назад"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Предотвращение перехода к предыдущему фрагменту
                requireActivity().supportFragmentManager.popBackStack(
                    R.id.roleFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                // Завершение активности
                requireActivity().finish()
            }
        }
        // Добавление обратного вызова к диспетчеру нажатия кнопки "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    // Метод для запуска сервиса блокировки приложений
    private fun appLocker() {
        // Создание намерения для запуска сервиса
        val intent = Intent(requireActivity(), AppBlockerService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        // Запуск сервиса в переднем плане
        requireActivity().startForegroundService(intent)
    }

    // Метод для запуска сервиса блокировки устройства
    private fun deviceLocker() {
        // Создание намерения для запуска сервиса
        val intent = Intent(requireActivity(), DeviceBlockerService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        // Запуск сервиса в переднем плане
        requireActivity().startForegroundService(intent)
    }

    // Методы для проверки различных разрешений
    private fun checkPermissionsWindow() {
        // Создание намерения для открытия настроек разрешений окна
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + requireContext().packageName)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Запуск активности
        startActivity(intent)
    }

    private fun checkPermissionAdmin() {
        // Создание намерения для получения прав администратора устройства
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Пожалуйста, разрешите приложению получить права администратора устройства"
        )
        // Запуск активности
        startActivity(intent)
    }

    // Метод для проверки всех необходимых разрешений
    private fun checkPermissions() = CoroutineScope(Dispatchers.Main).launch{
        // Получение сервиса операций приложения
        appOps = requireActivity().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        // Проверка разрешения на получение статистики использования
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), requireActivity().packageName
        )

        // Проверка активности администратора устройства
        if (!devicePolicyManager.isAdminActive(componentName)) {
            checkPermissionAdmin()
        }
        delay(10000)
        // Проверка разрешения на отображение поверх других приложений
        if (!Settings.canDrawOverlays(context)) {
            checkPermissionsWindow()
        }
        delay(10000)
        // Проверка разрешения на получение уведомлений
        if (!notificationManager.areNotificationsEnabled()) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
            context?.startActivity(intent)
        }
        delay(10000)
        // Проверка разрешения на доступ к точному местоположению
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermissionsLocation()
        }
        delay(10000)
        // Проверка разрешения на доступ к статистике использования
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    // Метод для проверки разрешения на доступ к местоположению
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

                    // Запрос разрешения на доступ к местоположению
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