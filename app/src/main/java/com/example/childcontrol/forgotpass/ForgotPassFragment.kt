package com.example.childcontrol.forgotpass

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
import com.example.childcontrol.databinding.FragmentForgotPassBinding
import com.google.firebase.auth.FirebaseAuth


// Фрагмент для функционала восстановления пароля
class ForgotPassFragment : Fragment() {
    private lateinit var binding: FragmentForgotPassBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var forgotPassRepository: ForgotPassRepository
    private lateinit var viewModelFactory: ForgotPassViewModelFactory
    private lateinit var forgotPassViewModel: ForgotPassViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_pass, container, false)
        mAuth = FirebaseAuth.getInstance()
        forgotPassRepository = ForgotPassRepository(mAuth)
        viewModelFactory = ForgotPassViewModelFactory(forgotPassRepository)
        forgotPassViewModel =
            ViewModelProvider(this, viewModelFactory)[ForgotPassViewModel::class.java]
        // Привязка ViewModel к макету
        binding.forgotPassViewModel = forgotPassViewModel
        // Наблюдение за событием ошибки и обновление интерфейса в соответствии с ним
        forgotPassViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true)
            // Если есть ошибка, показываем сообщение об ошибке
                binding.errorMsgForgotPass.visibility = View.VISIBLE
            else
            // Если ошибки нет, переходим к фрагменту авторизации
                this.findNavController()
                    .navigate(ForgotPassFragmentDirections.actionForgotPassFragmentToAuthFragment())
        })
        return binding.root
    }
}
