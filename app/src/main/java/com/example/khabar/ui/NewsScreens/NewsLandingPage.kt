package com.example.khabar.ui.NewsScreens

import android.content.Intent
import com.example.khabar.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.khabar.databinding.ActivityNewsLandingPageBinding
import com.example.khabar.ui.NewsScreens.viewmodel.NewsViewModel
import com.example.khabar.ui.auth.AuthActivity
import com.example.khabar.ui.auth.AuthViewModel
import com.example.khabar.utils.ResourceState
import com.example.khabar.utils.ThemeManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsLandingPage : AppCompatActivity() {
    private lateinit var themeManager: ThemeManager
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var binding: ActivityNewsLandingPageBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var newsAdapter: NewsPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeManager = ThemeManager.getInstance(this)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)

        setupAdapter()
        observeNewsUpdate()
        setupNavigationDrawer()
        setupBackPressHandler()
        observeCurrentUser()
        authViewModel.fetchCurrentUser()
    }

    private fun setupNavigationDrawer(){
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.theme -> {
                    showThemeSelectionDialog()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.Aboutus -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.feedback -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.rules -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.share -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.logout -> {
                    authViewModel.signOut()
                    startActivity(Intent(this, AuthActivity::class.java))
                    true
                }
                else -> false
            }
        }
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            findViewById(R.id.topAppBar),
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                finish()
            }
        }
    }
    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Light", "Dark", "System Default")
        val currentTheme = themeManager.getSavedTheme().value

        AlertDialog.Builder(this)
            .setTitle("Choose Theme")
            .setSingleChoiceItems(themes, currentTheme) { dialog, which ->
                val selectedTheme = ThemeManager.ThemeMode.fromValue(which)
                themeManager.setTheme(selectedTheme)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun observeCurrentUser(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                authViewModel.currentUser.collect{ user ->
                    user?.let{
                        val headerView = navigationView.getHeaderView(0)
                        val profileImage = headerView.findViewById<CircleImageView>(R.id.profileImage)
                        if (!user.profilePictureUrl.isNullOrEmpty()){
                            Glide.with(this@NewsLandingPage)
                                .load(user.profilePictureUrl)
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .circleCrop()
                                .dontTransform()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(profileImage)

                        }
                        else{
                            profileImage.setImageResource(R.drawable.default_profile)

                        }
                        headerView.findViewById<TextView>(R.id.name).text = user.fullName

                    }
                }
            }
        }
    }
    private fun setupAdapter() {
        newsAdapter = NewsPageAdapter()
        binding.newsPager.adapter = newsAdapter
        binding.shimmerView.startShimmer()
    }

    private fun observeNewsUpdate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.news.collect { state ->
                    when (state) {
                        is ResourceState.Loading -> {
                            Log.d("NewsLandingPage", "Loading news..")
                            showShimmer()
                        }
                        is ResourceState.Success -> {
                            Log.d("NewsLandingPage", "Received ${state.data.articles.size} articles")
                            hideShimmer()
                            newsAdapter.setArticles(state.data.articles)
                        }
                        is ResourceState.Error -> {
                            Log.e("NewsLandingPage", "Error: ${state.error}")
                            hideShimmer()
                        }
                    }
                }
            }
        }
    }

    private fun showShimmer() {
        binding.shimmerView.visibility = View.VISIBLE
        binding.newsPager.visibility = View.GONE
        binding.shimmerView.startShimmer()
    }

    private fun hideShimmer() {
        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility = View.GONE
        binding.newsPager.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.shimmerView.stopShimmer()
    }
}
