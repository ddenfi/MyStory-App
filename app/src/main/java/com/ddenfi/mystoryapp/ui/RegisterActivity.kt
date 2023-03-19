package com.ddenfi.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ddenfi.mystoryapp.databinding.ActivityRegisterBinding
import com.ddenfi.mystoryapp.utils.Results
import com.ddenfi.mystoryapp.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel:RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.cbShowPass.setOnCheckedChangeListener { _, b ->
            if (b) {
                val textLength = binding.etCustomPassword.length()
                binding.apply {
                    etCustomPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    etCustomPassword.setSelection(textLength)
                    etCustomPassword.error  = if (textLength < 6) {
                        "Must 6 character or more!"
                    } else null
                }

            } else {
                val textLength = binding.etCustomPassword.length()
                binding.apply {
                    etCustomPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    etCustomPassword.setSelection(textLength)
                    etCustomPassword.error  = if (textLength < 6) {
                        "Must 6 character or more!"
                    } else null
                }
            }
        }

        playAnim()

        binding.btnSignup.setOnClickListener {
            val mEmail = binding.etEmail.text.toString()
            val mName = binding.etName.text.toString()
            val mPass = binding.etCustomPassword.text.toString()
            viewModel.register(email = mEmail, name = mName, pass = mPass)
                .observe(this@RegisterActivity) {
                    when (it) {
                        is Results.Loading -> showLoading()
                        is Results.Error -> showError(it.message)
                        is Results.Success -> showSuccess()
                    }
                }
        }
    }

    private fun showError(error: String?) {
        binding.pbBlock.visibility = View.GONE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess() {
        startActivity(Intent(this, LoginActivity::class.java))
        binding.pbBlock.visibility = View.GONE
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        binding.pbBlock.visibility = View.VISIBLE
    }

    @SuppressLint("Recycle")
    private fun playAnim() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tittle = ObjectAnimator.ofFloat(binding.tvTittle, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.etCustomPassword, View.ALPHA, 1f).setDuration(500)
        val checkbox = ObjectAnimator.ofFloat(binding.cbShowPass, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(name, email, pass, checkbox)
        }
        AnimatorSet().apply {
            playSequentially(tittle,signup,together)
            start()
        }

    }
}