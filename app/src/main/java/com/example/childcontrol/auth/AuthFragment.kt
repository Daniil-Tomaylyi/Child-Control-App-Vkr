package com.example.childcontrol.auth

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.Service.AppBlockerService
import com.example.childcontrol.Service.DeviceBlockerService
import com.example.childcontrol.R
import com.example.childcontrol.Service.LocationService
import com.example.childcontrol.admin.MyDeviceAdminReceiver
import com.example.childcontrol.databinding.FragmentAuthBinding
import com.example.childcontrol.databinding.FragmentDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private lateinit var devicePolicyManager: DevicePolicyManager

    private lateinit var componentName: ComponentName

    private lateinit var dialogBinding: FragmentDialogBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var authRepository: AuthRepository

    private lateinit var intentAppBlockerService: Intent

    private lateinit var intentDeviceBlockerService: Intent

    private lateinit var intentLocationService: Intent

    private lateinit var intent: Intent

    private lateinit var viewModelFactory: AuthViewModelFactory

    private val delay = 500

    private lateinit var authViewModel: AuthViewModel

    private var userDeleteApp: String? = null

    private var packageName: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
        binding.buttonAuthCreate.setOnClickListener { view: View ->
            // Переход к фрагменту регистрации
            view.findNavController().navigate(R.id.action_authFragment_to_regFragment)
        }
        binding.passRec.setOnClickListener { view: View ->
            // Переход к фрагменту восстановления пароля
            view.findNavController().navigate(R.id.action_authFragment_to_forgotPassFragment)
        }
        // Получение типа авторизации
        userDeleteApp = requireArguments().getString("typeAuth")
        // Инициализация привязки данных для диалогового окна
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_dialog,
            null,
            false
        )
        // Инициализация менеджера политики устройства и компонента
        devicePolicyManager =
            activity?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(requireActivity(), MyDeviceAdminReceiver::class.java)
        // Инициализация намерений для служб
        intentAppBlockerService = Intent(requireActivity(), AppBlockerService::class.java)
        intentDeviceBlockerService = Intent(requireActivity(), DeviceBlockerService::class.java)
        intentLocationService = Intent(requireActivity(), LocationService::class.java)
        // Инициализация Firebase Auth и базы данных
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // Инициализация репозитория и модели представления
        authRepository = AuthRepository(mAuth, database)
        viewModelFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        binding.authViewModel = authViewModel
        // Настройка интерфейса в зависимости от типа авторизации
        if (userDeleteApp == "deleteapp") {
            binding.passRec.visibility = View.GONE
            dialogBinding.progressText = "Удаление\nпожалуйста подождите"
            binding.buttonAuthCreate.visibility = View.GONE
        } else {
            dialogBinding.progressText = "Авторизация\nпожалуйста подождите"
        }
        // Создание диалогового окна
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        // Наблюдение за изменениями в модели представления
        authViewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            if (it == true)
                dialog.show()
            else
                Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, delay.toLong())
        })
        binding.buttonLoginAuth.setOnClickListener {
            if (userDeleteApp == "deleteapp") {
                authViewModel.deleteApp()
            } else {
                authViewModel.signIn()
            }
        }
        authViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true)
                binding.errorMsgAuth.visibility = View.VISIBLE
            else {
                if (userDeleteApp == "deleteapp") {
                    // Остановка служб и удаление приложения
                    requireActivity().stopService(intentAppBlockerService)
                    requireActivity().stopService(intentDeviceBlockerService)
                    requireActivity().stopService(intentLocationService)
                    devicePolicyManager.removeActiveAdmin(componentName)
                    packageName = "com.example.childcontrol"
                    intent = Intent(Intent.ACTION_DELETE)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } else {
                    // Переход к следующему фрагменту
                    this.findNavController()
                        .navigate(AuthFragmentDirections.actionAuthFragmentToRoleFragment())
                }
            }
        })
        return binding.root
    }

}