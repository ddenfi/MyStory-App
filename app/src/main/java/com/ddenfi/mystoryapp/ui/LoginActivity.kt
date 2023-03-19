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
import com.ddenfi.mystoryapp.databinding.ActivityLoginBinding
import com.ddenfi.mystoryapp.utils.Results
import com.ddenfi.mystoryapp.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        playAnim()

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

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etCustomPassword.text.toString()
            viewModel.login(email, pass).observe(this@LoginActivity) {
                when (it) {
                    is Results.Loading -> showLoading()
                    is Results.Error -> showError(it.message)
                    is Results.Success -> {
                        showSuccess()
                        it.data.let { loginResponse ->
                            CoroutineScope(Dispatchers.IO).launch {
                                loginResponse?.loginResult?.token?.let { it1 ->
                                    viewModel.saveTokenAuth(
                                        it1
                                    )
                                    viewModel.saveLoginState(true)
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun showError(error: String?) {
        binding.pbBlock.visibility = View.GONE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess() {
        binding.pbBlock.visibility = View.GONE
        Toast.makeText(application, "Success", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        binding.pbBlock.visibility = View.VISIBLE
    }

    @SuppressLint("Recycle")
    private fun playAnim() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvLoginTittle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.etCustomPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val cb = ObjectAnimator.ofFloat(binding.cbShowPass, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(email, pass, cb)
        }
        AnimatorSet().apply {
            playSequentially(title, login, together)
            start()
        }

    }

}