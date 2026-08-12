package com.dark.appmanejementugasharian.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dark.appmanejementugasharian.R
import com.dark.appmanejementugasharian.model.Task
import com.dark.appmanejementugasharian.utils.Priority
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskAdapter(
    private val onTaskStatusChanged: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    fun getTaskAt(position: Int) = getItem(position)

    fun removeAt(position: Int) {
        currentList.toMutableList().apply {
            removeAt(position)
            submitList(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), onTaskStatusChanged)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)

        fun bind(task: Task, onStatusChanged: (Task, Boolean) -> Unit) {
            tvTitle.text = task.title
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
            val date = Instant.ofEpochMilli(task.deadline)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)
            tvDeadline.text = date

            val priority = Priority.fromString(task.priority)
            tvPriority.text = priority.name
            tvPriority.setTextColor(priority.getColor())

            cbCompleted.isChecked = task.isCompleted
            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onStatusChanged(task, isChecked)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}