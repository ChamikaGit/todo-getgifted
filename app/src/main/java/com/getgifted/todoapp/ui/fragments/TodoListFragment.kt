package com.getgifted.todoapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getgifted.todoapp.R
import com.getgifted.todoapp.data.repository.TodoRepository
import com.getgifted.todoapp.databinding.FragmentTodoListBinding
import com.getgifted.todoapp.ui.adapter.TodoAdapter
import com.getgifted.todoapp.ui.viewmodel.TodoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodoListFragment : Fragment() {

    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoListViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "Todo List"
        }

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        // Load only once after UI created
        viewModel.fetchInitialData()
    }

    private var previousItemCount = 0

    private fun setupRecyclerView() {
        todoAdapter = TodoAdapter { todo ->
            val bundle = Bundle().apply {
                putInt("todoId", todo.id)
            }
            findNavController().navigate(R.id.action_todoList_to_todoDetail, bundle)
        }

        binding.recyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(context)

            // Pagination scroll listener
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy <= 0) return

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    Log.d("Pagination", "Last visible item = $lastVisibleItem")

                    val pageSize = TodoRepository.PAGE_SIZE

                    // User reached EXACT end of a page
                    val isEndOfPage = (lastVisibleItem + 1) % pageSize == 0

                    Log.d(
                        "Pagination",
                        "isEndOfPage = $isEndOfPage | totalLoaded = ${viewModel.todos.value.size}"
                    )

                    if (!viewModel.isLoading.value && !viewModel.isPaginationLoading.value && isEndOfPage && lastVisibleItem + 1 == viewModel.todos.value.size  // load for if reached to the last item
                    ) {
                        Log.d("Pagination", ">>> Loading NEXT page...")
                        viewModel.loadMoreTodos()
                    }
                }
            })

        }
    }

    private fun setupObservers() {
        // We could use a separate UI state class, but here I'm not using it.

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.todos.collect { todos ->
                        todoAdapter.submitList(todos)
                        // Update previous count to match the new list size so we don't auto‑load next page
                        previousItemCount = todos.size
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // Initial loading (center progress bar)
                        binding.progressBar.isVisible = isLoading && todoAdapter.itemCount == 0

                        // Swipe refresh loading
                        binding.swipeRefresh.isRefreshing =
                            isLoading && todoAdapter.itemCount > 0 && !viewModel.isPaginationLoading.value

                        // Pagination loading (adapter footer)
                        binding.recyclerView.post {
                            todoAdapter.setLoaderVisible(viewModel.isPaginationLoading.value)
                        }
                    }
                }

                launch {
                    viewModel.error.collect { error ->
                        if (error != null) {
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            binding.errorText.text = error
                            binding.errorText.isVisible = todoAdapter.itemCount == 0
                            viewModel.clearError()
                        } else {
                            binding.errorText.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            previousItemCount = 0
            viewModel.refreshTodos()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
