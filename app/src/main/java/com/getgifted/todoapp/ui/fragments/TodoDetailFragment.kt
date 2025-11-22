package com.getgifted.todoapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import com.getgifted.todoapp.R
import com.getgifted.todoapp.databinding.FragmentTodoDetailBinding
import com.getgifted.todoapp.ui.viewmodel.TodoDetailViewModel

@AndroidEntryPoint
class TodoDetailFragment : Fragment() {

    private var _binding: FragmentTodoDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoDetailViewModel by viewModels()
    private var todoId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            todoId = it.getInt("todoId", -1)
        }

        setupListeners()
        setupObservers()

        // Enable back button on Activity's ActionBar
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Todo Details"
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
            )
        }

        if (todoId != -1) {
            viewModel.loadTodo(todoId)
        } else {
            Toast.makeText(context, "Invalid Todo ID", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    // setupViewModel removed as Hilt handles it

    private fun setupListeners() {
        binding.saveButton.setOnClickListener {
            val description = binding.descriptionInput.text.toString()
            viewModel.updateDescription(description)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.todo.collect { todo ->
                        if (todo != null) {
                            binding.apply {
                                todoId.text = todo.id.toString()
                                todoTitle.text = todo.title
                                userId.text = todo.userId.toString()
                                
                                // Only update text if it's different to avoid cursor jumping if we were typing (though here we only load once usually)
                                if (descriptionInput.text.toString() != todo.description) {
                                    descriptionInput.setText(todo.description)
                                }
                                
                                if (todo.completed) {
                                    completedIcon.visibility = View.VISIBLE
                                    completedText.text = getString(R.string.completed)
                                } else {
                                    completedIcon.visibility = View.GONE
                                    completedText.text = getString(R.string.not_completed)
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.isVisible = isLoading
                        binding.saveButton.isEnabled = !isLoading
                    }
                }
                
                launch {
                    viewModel.saveSuccess.collect { success ->
                        if (success) {
                            Toast.makeText(context, "Description saved", Toast.LENGTH_SHORT).show()
                            viewModel.resetSaveSuccess()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
