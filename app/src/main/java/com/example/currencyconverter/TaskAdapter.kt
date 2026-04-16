package com.example.currencyconverter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskChecked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit // Naya parameter Edit ke liye
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTaskTitle)
        val dateTime: TextView = view.findViewById(R.id.tvTaskDateTime)
        val priorityIndicator: View = view.findViewById(R.id.priorityIndicator)
        val checkBox: CheckBox = view.findViewById(R.id.cbDone)
        val deleteBtn: ImageButton = view.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.title.text = task.title
        holder.dateTime.text = "${task.date} | ${task.time}"

        // Priority Color Logic
        val priorityColor = when (task.priority.lowercase()) {
            "high" -> Color.parseColor("#FF5252")
            "medium" -> Color.parseColor("#FFC107")
            "low" -> Color.parseColor("#4CAF50")
            else -> Color.parseColor("#B3FFFFFF")
        }
        holder.priorityIndicator.setBackgroundColor(priorityColor)

        // Checkbox Logic
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.isCompleted
        updateStrikeThrough(holder, task.isCompleted)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            onTaskChecked(task)
            updateStrikeThrough(holder, isChecked)
        }

        // --- EDIT FEATURE ---
        // Jab poore card par click ho toh dialog khule
        holder.itemView.setOnClickListener {
            onTaskClicked(task)
        }

        holder.deleteBtn.setOnClickListener {
            onDeleteClicked(task)
        }
    }

    private fun updateStrikeThrough(holder: TaskViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.title.alpha = 0.5f
            holder.dateTime.alpha = 0.5f
            holder.priorityIndicator.alpha = 0.3f
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.title.alpha = 1.0f
            holder.dateTime.alpha = 1.0f
            holder.priorityIndicator.alpha = 1.0f
        }
    }

    override fun getItemCount() = tasks.size
}