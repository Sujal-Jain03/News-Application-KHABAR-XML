package com.example.khabar.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.khabar.R
import com.example.khabar.databinding.ActivitySignupBinding
import com.example.khabar.ui.NewsScreens.NewsLandingPage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.Boolean
@Suppress("DEPRECATION")
@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private val googleSignInClient : GoogleSignInClient by lazy {
        val gso: GoogleSignInOptions = Builder(DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    private val sigInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result->
        if(result.resultCode==RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try{
                val account = task.getResult(ApiException::class.java)
                viewModel.signInWithGoogle(account)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.signUpButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val phoneNumber = binding.phoneInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            if (validateInputs(fullName, email, phoneNumber, password, confirmPassword)) {
                viewModel.signUp(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    profilePictureUrl = null,
                )
            }
        }

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        binding.googleSignUpButton.setOnClickListener {
            startGoogleSignIn()
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        if (fullName.isEmpty()) {
            binding.fullNameInput.error = "Full name is required"
            return false
        }
        if (email.isBlank() ||
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            binding.emailInput.error = "Valid email is required"
            return false
        }
        if (phoneNumber.isBlank()) {
            binding.phoneInput.error = "Phone number is required"
            return false
        }
        if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 8 characters"
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordInput.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.signUpButton.isEnabled = false
                    }
                    is AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this@SignupActivity, NewsLandingPage::class.java))
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signUpButton.isEnabled = true
                        Toast.makeText(this@SignupActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is AuthState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signUpButton.isEnabled = true
                    }
                }
            }
        }
    }
    private fun startGoogleSignIn(){
        val signInIntent =googleSignInClient.signInIntent
        sigInLauncher.launch(signInIntent)
    }
}
