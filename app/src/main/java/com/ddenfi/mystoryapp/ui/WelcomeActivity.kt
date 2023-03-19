package com.ddenfi.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ddenfi.mystoryapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        playAnim()

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this@WelcomeActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener{
            val intent = Intent(this@WelcomeActivity,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun playAnim(){
        ObjectAnimator.ofFloat(binding.ivWelcome, View.TRANSLATION_X,-30f,30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome = ObjectAnimator.ofFloat(binding.tvWelcome,View.ALPHA,1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvAppDesc,View.ALPHA,1f).setDuration(500)
        val appName = ObjectAnimator.ofFloat(binding.tvAppName,View.ALPHA,1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin,View.ALPHA,1f).setDuration(500)
        val make = ObjectAnimator.ofFloat(binding.tvMake,View.ALPHA,1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnSignup,View.ALPHA,1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login,signup)
        }
        AnimatorSet().apply {
            playSequentially(welcome,desc,appName,make,together)
            start()
        }

    }
}