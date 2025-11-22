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

        // Navigate to TodoListFragment after delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) { // Check if fragment is still attached
                findNavController().navigate(R.id.action_splash_to_todoList)
            }
        }, splashDelayMillis)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Restore the action bar when leaving splash screen
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        _binding = null
    }
}
