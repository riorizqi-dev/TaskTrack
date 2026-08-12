package com.dark.appmanejementugasharian.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dark.appmanejementugasharian.R
import com.dark.appmanejementugasharian.data.TaskDatabase
import com.dark.appmanejementugasharian.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var db: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabase.getDatabase(this)
        setupRecyclerView()
        loadTasks()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditTaskActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter { task, isCompleted ->
            CoroutineScope(Dispatchers.IO).launch {
                task.isCompleted = isCompleted
                db.taskDao().updateTask(task)
                runOnUiThread { loadTasks() }
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.getTaskAt(position)
                CoroutineScope(Dispatchers.IO).launch {
                    db.taskDao().deleteTask(task)
                    runOnUiThread {
                        adapter.removeAt(position)
                        Toast.makeText(this@MainActivity, "Tugas dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun loadTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = db.taskDao().getAllTasks()
            runOnUiThread {
                adapter.submitList(tasks)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }
}