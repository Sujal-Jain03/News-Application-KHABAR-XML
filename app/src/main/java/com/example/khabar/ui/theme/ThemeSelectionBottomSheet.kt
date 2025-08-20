package com.example.khabar.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.khabar.databinding.FragmentThemeSelectionBinding
import com.example.khabar.utils.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ThemeSelectionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentThemeSelectionBinding? = null
    private val binding get() = _binding!!

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupThemeOptions()
        observeCurrentTheme()
    }

    private fun setupThemeOptions() {
        binding.lightTheme.setOnClickListener {
            themeViewModel.setTheme(ThemeManager.ThemeMode.LIGHT)
            dismiss()
        }

        binding.darkTheme.setOnClickListener {
            themeViewModel.setTheme(ThemeManager.ThemeMode.DARK)
            dismiss()
        }

        binding.systemTheme.setOnClickListener {
            themeViewModel.setTheme(ThemeManager.ThemeMode.SYSTEM)
            dismiss()
        }
    }

    private fun observeCurrentTheme() {
        lifecycleScope.launch {
            themeViewModel.currentTheme.collect { theme ->
                updateThemeSelection(theme)
            }
        }
    }

    private fun updateThemeSelection(theme: ThemeManager.ThemeMode) {
        binding.lightTheme.isChecked = theme == ThemeManager.ThemeMode.LIGHT
        binding.darkTheme.isChecked = theme == ThemeManager.ThemeMode.DARK
        binding.systemTheme.isChecked = theme == ThemeManager.ThemeMode.SYSTEM
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
