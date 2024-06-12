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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
        binding.buttonAuthCreate.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_authFragment_to_regFragment)
        }
        binding.passRec.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_authFragment_to_forgotPassFragment)
        }
        val userDeleteApp = requireArguments().getString("typeAuth")
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_dialog,
            null,
            false
        )
        devicePolicyManager =
            activity?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(requireActivity(), MyDeviceAdminReceiver::class.java)
        val intentAppBlockerService = Intent(requireActivity(), AppBlockerService::class.java)
        val intentDeviceBlockerService = Intent(requireActivity(), DeviceBlockerService::class.java)
        val intentLocationService = Intent(requireActivity(), LocationService::class.java)
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val viewModelFactory = AuthViewModelFactory(mAuth,database)
        val delay = 500
        val AuthViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        binding.authViewModel = AuthViewModel
        if (userDeleteApp == "deleteapp") {
            binding.passRec.visibility = View.GONE
            dialogBinding.progressText = "Удаление\nпожалуйста подождите"
            binding.buttonAuthCreate.visibility = View.GONE
        }
        else {
            dialogBinding.progressText = "Авторизация\nпожалуйста подождите"
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()


        AuthViewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            if (it == true)
                dialog.show()
            else
                Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, delay.toLong())
        })
        binding.buttonLoginAuth.setOnClickListener{
            if (userDeleteApp == "deleteapp"){
                AuthViewModel.delete_app()
            }
            else{
                AuthViewModel.sign_in()
            }
        }


        AuthViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true)
                binding.errorMsgAuth.visibility = View.VISIBLE
            else {
                if (userDeleteApp == "deleteapp") {
                    requireActivity().stopService(intentAppBlockerService)
                    requireActivity().stopService(intentDeviceBlockerService)
                    requireActivity().stopService(intentLocationService)
                    devicePolicyManager.removeActiveAdmin(componentName)
                    val packageName = "com.example.childcontrol"
                    val intent = Intent(Intent.ACTION_DELETE)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } else {
                    this.findNavController()
                        .navigate(AuthFragmentDirections.actionAuthFragmentToRoleFragment())

                }
            }
        })
        return binding.root

    }
}