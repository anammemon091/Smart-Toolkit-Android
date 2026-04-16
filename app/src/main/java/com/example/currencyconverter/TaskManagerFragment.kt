package com.example.currencyconverter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.databinding.DialogAddTaskBinding
import com.example.currencyconverter.databinding.FragmentTaskManagerBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TaskManagerFragment : Fragment() {

    private var _binding: FragmentTaskManagerBinding? = null
    private val binding get() = _binding!!

    private var taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        loadTasks()
        updateEmptyState()

        binding.btnAddTask.setOnClickListener {
            showTaskDialog(null)
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            tasks = taskList,
            onTaskChecked = { _ ->
                saveTasks()
            },
            onDeleteClicked = { task ->
                taskList.remove(task)
                saveTasks()
                taskAdapter.notifyDataSetChanged()
                updateEmptyState()
            },
                    onTaskClicked = { task ->
                showTaskDialog(task) // Purana task bhej rahe hain edit ke liye
            }
        )

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun showTaskDialog(existingTask: Task?) {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)

        // Use default AlertDialog if CustomAlertDialog style is missing
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        var selectedDate = existingTask?.date ?: ""
        var selectedTime = existingTask?.time ?: ""

        val priorities = listOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, priorities)
        dialogBinding.spinnerPriority.adapter = priorityAdapter

        if (existingTask != null) {
            dialogBinding.etTaskTitle.setText(existingTask.title)
            dialogBinding.btnPickDate.text = existingTask.date
            dialogBinding.btnPickTime.text = existingTask.time
            dialogBinding.spinnerPriority.setSelection(priorities.indexOf(existingTask.priority))
        }

        dialogBinding.btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedDate = "$d/${m + 1}/$y"
                dialogBinding.btnPickDate.text = selectedDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        dialogBinding.btnPickTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, min ->
                selectedTime = String.format("%02d:%02d", h, min)
                dialogBinding.btnPickTime.text = selectedTime
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
        }

        dialogBinding.btnSaveTask.setOnClickListener {
            val title = dialogBinding.etTaskTitle.text.toString()
            val priority = dialogBinding.spinnerPriority.selectedItem.toString()

            if (title.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                if (existingTask == null) {
                    val newTask = Task(
                        id = System.currentTimeMillis().toString(),
                        title = title,
                        date = selectedDate,
                        time = selectedTime,
                        priority = priority,
                        isCompleted = false
                    )
                    taskList.add(0, newTask)
                } else {
                    existingTask.title = title
                    existingTask.date = selectedDate
                    existingTask.time = selectedTime
                    existingTask.priority = priority
                }

                saveTasks()
                taskAdapter.notifyDataSetChanged()
                updateEmptyState()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun saveTasks() {
        val sharedPreferences = requireActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(taskList)
        sharedPreferences.edit().putString("task_list", json).apply()
    }

    private fun loadTasks() {
        val sharedPreferences = requireActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("task_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            val loadedList: MutableList<Task> = Gson().fromJson(json, type)
            taskList.clear()
            taskList.addAll(loadedList)
        }
    }
    private fun updateEmptyState() {
        if (taskList.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.rvTasks.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.rvTasks.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}