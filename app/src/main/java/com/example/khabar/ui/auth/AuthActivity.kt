package com.example.khabar.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.khabar.R
import com.example.khabar.databinding.ActivityAuthBinding
import com.example.khabar.ui.NewsScreens.NewsLandingPage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@Suppress("DEPRECATION")
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
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
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
        observeAuthState()
    }

    private fun validateInput(
        email: String,
        password: String,
    ): Boolean {
        if (email.isBlank() ||
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            binding.emailInput.error = "Valid email is required"
            return false
        }
        if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    private fun setupClickListeners() {
        binding.authButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (validateInput(email, password)) {
                viewModel.signIn(email, password)
            }
        }

        binding.toggleButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.googleSignInButton.setOnClickListener {
            startGoogleSignIn()
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.authButton.isEnabled = false
                        binding.errorText.visibility = View.GONE
                    }
                    is AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.authButton.isEnabled = true
                        startActivity(Intent(this@AuthActivity, NewsLandingPage::class.java))
                        finish()
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.authButton.isEnabled = true
                        binding.errorText.apply {
                            text = state.message
                            visibility = View.VISIBLE
                        }
                    }
                    is AuthState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.authButton.isEnabled = true
                        binding.errorText.visibility = View.GONE
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
