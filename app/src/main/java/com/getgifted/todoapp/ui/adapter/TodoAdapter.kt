package com.getgifted.todoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.getgifted.todoapp.data.model.Todo
import com.getgifted.todoapp.databinding.ItemLoadingBinding
import com.getgifted.todoapp.databinding.ItemTodoBinding

class TodoAdapter(
    private val onTodoClick: (Todo) -> Unit
) : ListAdapter<Todo, RecyclerView.ViewHolder>(TodoDiffCallback()) {

    private var isLoaderVisible = false

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    fun setLoaderVisible(visible: Boolean) {
        if (isLoaderVisible != visible) {
            isLoaderVisible = visible
            if (currentList.isNotEmpty()) {
                if (visible) {
                    notifyItemInserted(currentList.size)
                } else {
                    notifyItemRemoved(currentList.size)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LOADING) {
            val binding = ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            LoadingViewHolder(binding)
        } else {
            val binding = ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            TodoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TodoViewHolder) {
            val todo = getItem(position)
            holder.bind(todo)
        }
    }

    override fun getItemCount(): Int {
        // Only show loader if we have items AND loader is set to visible
        return if (currentList.isNotEmpty() && isLoaderVisible) {
            currentList.size + 1
        } else {
            currentList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Only show loader at the end if we have items AND loader is visible
        return if (isLoaderVisible && currentList.isNotEmpty() && position == currentList.size) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    inner class TodoViewHolder(
        private val binding: ItemTodoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < currentList.size) {
                    val todo = getItem(position)
                    onTodoClick(todo)
                }
            }
        }

        fun bind(todo: Todo) {
            binding.apply {
                todoTitle.text = todo.title
                todoId.text = "ID: ${todo.id}"
                
                if (todo.completed) {
                    completedIcon.visibility = android.view.View.VISIBLE
                } else {
                    completedIcon.visibility = android.view.View.GONE
                }
            }
        }
    }

    inner class LoadingViewHolder(
        binding: ItemLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class TodoDiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }
}
