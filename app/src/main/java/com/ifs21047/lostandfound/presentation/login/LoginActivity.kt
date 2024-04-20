package com.ifs21047.lostandfound.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ifs21047.lostandfound.databinding.ActivityLoginBinding
import com.ifs21047.lostfounds.data.pref.UserModel
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.presentation.ViewModelFactory
import com.ifs21047.lostfounds.presentation.login.LoginViewModel
import com.ifs21047.lostandfound.presentation.main.MainActivity
import com.ifs21047.lostandfound.presentation.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView(){
        showLoading(false)
    }

    private fun setupAction(){
        binding.apply {
            // Memberikan aksis jika text ke tampilan register dipilih
            tvLoginToRegister.setOnClickListener {
                openRegisterActivity()
            }

            // Memberikan aksi jika tombol Login dipilih
            btnLogin.setOnClickListener {
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }

                observeLogin(email, password)
            }
        }
    }

    private fun observeLogin(email: String, password: String){
        viewModel.login(
            email,
            password
        ).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }

                    is MyResult.Success -> {
                        showLoading(false)
                        lifecycleScope.launch {
                            viewModel.saveSession(UserModel(result.data.token))
                                .observe(this@LoginActivity) {
                                    openMainActivity()
                                }
                        }
                    }

                    is MyResult.Error -> {
                        showLoading(false)

                        AlertDialog.Builder(this).apply {
                            setTitle("Oh No!")
                            setMessage(result.error)
                            setPositiveButton("Oke") { _, _ -> }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isActivated = !isLoading
        binding.btnLogin.text = if (isLoading) "" else "Login"
    }

    private fun openRegisterActivity(){
        val intent = Intent(applicationContext, RegisterActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
        finish()
    }

    private fun openMainActivity(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}