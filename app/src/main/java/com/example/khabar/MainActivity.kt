package com.example.khabar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.khabar.ui.NewsScreens.NewsLandingPage

import com.example.khabar.data.repository.AuthRepository
import com.example.khabar.databinding.ActivityMainBinding

import com.example.khabar.ui.auth.SignupActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authRepository: AuthRepository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Now safe to call methods that use binding
        startSplashAnimations()

        // Check authentication without delay
        lifecycleScope.launch {
            checkAuthAndNavigate()
        }
    }
    private fun startSplashAnimations() {
        // Scale animation
        val scaleUpX = ObjectAnimator.ofFloat(binding.imageView, "scaleX", 0.5f, 1.1f, 1.0f)
        val scaleUpY = ObjectAnimator.ofFloat(binding.imageView, "scaleY", 0.5f, 1.1f, 1.0f)

        // Fade in animation
        val fadeIn = ObjectAnimator.ofFloat(binding.imageView, "alpha", 0f, 1f)

        // Rotation animation (optional)
        val rotate = ObjectAnimator.ofFloat(binding.imageView, "rotation", 0f, 360f)
        rotate.duration = 2000
        rotate.repeatCount = ObjectAnimator.INFINITE
        rotate.interpolator = AccelerateDecelerateInterpolator()

        // Combine animations
        val animatorSet = AnimatorSet()
        animatorSet.apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(scaleUpX, scaleUpY, fadeIn)
        }

        // Start animations
        animatorSet.start()
        rotate.start() // Continuous rotation
    }

    private suspend fun checkAuthAndNavigate() {
        val currentUser = authRepository.getCurrentUser()
        delay(2500)
        val intent = if(currentUser !=null){
            // User is logged in, go to NewsLandingPage
            Intent(this@MainActivity, NewsLandingPage::class.java)
        }else{
            //no User is logged in , go to signUpActivity
            Intent(this@MainActivity, SignupActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
