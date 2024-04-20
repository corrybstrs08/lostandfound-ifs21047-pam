package com.ifs21047.lostandfound.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifs21047.delcomtodo.data.remote.response.DataUserResponse
import com.ifs21047.lostandfound.R
import com.ifs21047.lostandfound.databinding.ActivityProfileBinding
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.presentation.ViewModelFactory
import com.ifs21047.lostandfound.presentation.login.LoginActivity
import com.ifs21047.lostfounds.presentation.profile.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView(){
        showLoading(true)
        observeGetMe()
    }

    private fun setupAction(){
        binding.apply {
            ivProfileBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.llProfile.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun observeGetMe(){
        viewModel.getMe().observe(this){ result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }

                    is MyResult.Success -> {
                        showLoading(false)
                        loadProfileData(result.data)
                    }

                    is MyResult.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            applicationContext, result.error, Toast.LENGTH_LONG
                        ).show()
                        viewModel.logout()
                        openLoginActivity()
                    }
                }
            }
        }
    }

    private fun loadProfileData(profile: DataUserResponse){
        binding.apply {

            if(profile.user.photo != null){
                val urlImg = "https://public-api.delcom.org/${profile.user.photo}"
                Glide.with(this@ProfileActivity)
                    .load(urlImg)
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfile)
            }

            tvProfileName.text = profile.user.name
            tvProfileEmail.text = profile.user.email
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}