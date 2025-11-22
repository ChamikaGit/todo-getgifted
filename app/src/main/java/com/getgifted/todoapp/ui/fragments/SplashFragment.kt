package com.getgifted.todoapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.getgifted.todoapp.R
import com.getgifted.todoapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val splashDelayMillis = 3000L // 3 seconds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the action bar for splash screen
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        // Enable full-screen mode with colored status bar
        setupFullScreenMode()

        // Navigate to TodoListFragment after delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { // Check if fragment is still attached
                findNavController().navigate(R.id.action_splash_to_todoList)
            }
        }, splashDelayMillis)
    }

    private fun setupFullScreenMode() {
        activity?.window?.let { window ->
            // Enable edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            // Set status bar color to match splash background
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.splash_background)
            
            // Set navigation bar color to match splash background
            window.navigationBarColor = ContextCompat.getColor(requireContext(), R.color.splash_background)
            
            // Make status bar icons light colored (for dark background)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
            }
            
            // Make navigation bar icons light colored
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightNavigationBars = false
            }
        }
        
        // Apply window insets to make content go edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            // Don't apply any padding - let content go edge-to-edge
            insets
        }
    }

    private fun restoreSystemUI() {
        activity?.window?.let { window ->
            // Restore normal window behavior
            WindowCompat.setDecorFitsSystemWindows(window, true)
            
            // Reset status bar color to default (you may need to adjust this to match your theme)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
            }
            
            // Reset navigation bar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.navigationBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore the action bar when leaving splash screen
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        // Restore system UI
        restoreSystemUI()
        _binding = null
    }
}
