package com.ddenfi.mystoryapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import com.ddenfi.mystoryapp.databinding.ActivitySplashScreenBinding
import com.ddenfi.mystoryapp.utils.ConstantValue
import com.ddenfi.mystoryapp.viewmodel.SplashScreenViewModel

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Handler().postDelayed({
            moveTo()
        },ConstantValue.SPLASH_SCREEN_TIME)
    }

    private fun moveTo(){
        val intentToMainActivity = Intent(this@SplashScreenActivity, MainActivity::class.java)
        val intentToWelcome = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
        viewModel.getLoginState().observe(this@SplashScreenActivity) { isLogin ->
            if (isLogin) {
                startActivity(intentToMainActivity)
            } else {
                startActivity(intentToWelcome)
            }
        }
        finish()
    }
}