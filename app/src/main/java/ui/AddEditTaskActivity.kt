package com.dark.appmanejementugasharian.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dark.appmanejementugasharian.R
import com.dark.appmanejementugasharian.data.TaskDatabase
import com.dark.appmanejementugasharian.databinding.ActivityAddEditTaskBinding
import com.dark.appmanejementugasharian.model.Task
import com.dark.appmanejementugasharian.utils.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var db: TaskDatabase
    private var editingTask: Task? = null
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)

        setupPrioritySpinner()
        setupDatePicker()
        checkEditMode()

        binding.btnSave.setOnClickListener { saveTask() }
    }

    private fun setupPrioritySpinner() {
        val priorities = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.tvDeadline.text = selectedDate.toString()
        binding.tvDeadline.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    binding.tvDeadline.text = selectedDate.toString()
                },
                selectedDate.year,
                selectedDate.monthValue - 1,
                selectedDate.dayOfMonth
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }
    }

    private fun checkEditMode() {
        editingTask = intent.getParcelableExtra("task")
        if (editingTask != null) {
            supportActionBar?.title = "Edit Tugas"
            binding.etTitle.setText(editingTask!!.title)
            binding.etDescription.setText(editingTask!!.description)
            selectedDate = java.util.Date(editingTask!!.deadline)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            binding.tvDeadline.text = selectedDate.toString()

            val priorityIndex = when (editingTask!!.priority) {
                "HIGH" -> 0
                "LOW" -> 2
                else -> 1
            }
            binding.spinnerPriority.setSelection(priorityIndex)
        }
    }

    private fun saveTask() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.etTitle.error = "Judul wajib diisi"
            return
        }

        if (selectedDate < LocalDate.now()) {
            Toast.makeText(this, "Deadline tidak boleh di masa lalu!", Toast.LENGTH_SHORT).show()
            return
        }

        val description = binding.etDescription.text.toString()
        val priority = when (binding.spinnerPriority.selectedItem.toString()) {
            "High" -> "HIGH"
            "Low" -> "LOW"
            else -> "MEDIUM"
        }

        val deadlineMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val task = if (editingTask == null) {
            Task(title = title, description = description, deadline = deadlineMillis, priority = priority)
        } else {
            editingTask!!.apply {
                this.title = title
                this.description = description
                this.deadline = deadlineMillis
                this.priority = priority
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (editingTask == null) {
                db.taskDao().insertTask(task)
            } else {
                db.taskDao().updateTask(task)
            }
            finish()
        }
    }
}