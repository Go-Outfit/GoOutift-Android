package com.example.gooutfit_android.ui.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.gooutfit_android.R
import com.example.gooutfit_android.databinding.ActivityLoginBinding
import com.example.gooutfit_android.ui.auth.register.RegisterActivity
import com.example.gooutfit_android.ui.auth.viewmodel.ApiAuthConfig
import com.example.gooutfit_android.ui.auth.viewmodel.AuthRequest
import com.example.gooutfit_android.ui.auth.viewmodel.AuthResponse
import com.example.gooutfit_android.ui.auth.viewmodel.AuthViewModel
import com.example.gooutfit_android.ui.dashboard.DashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //        Validation
        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }

        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        passwordStream.subscribe {
            showPasswordAlert(it, "Password")
        }

        //        Auth
        auth = FirebaseAuth.getInstance()

        //        Button Enable True or False
        val invalidFieldsStream = Observable.combineLatest(
            emailStream,
            passwordStream
        ) { emailInvalid: Boolean, passwordInvalid: Boolean ->
            !emailInvalid && !passwordInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            if(isValid) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.custom_primary_1)
            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.custom_stroke)
            }
        }

        binding.tvHaventAccount.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener{
            login()
        }
    }

    private fun showPasswordAlert(isNotValid: Boolean, text: String){
        binding.etPassword.error = if(isNotValid) "Enter your password" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean){
        binding.etEmail.error = if (isNotValid) "Email is not valid" else null
    }

    private fun login(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.login(email, password)

        authViewModel.idToken.observe(this) { idToken ->
            if (!idToken.isNullOrEmpty()) {
                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}