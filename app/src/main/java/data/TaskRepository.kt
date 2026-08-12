package com.dark.appmanejementugasharian.data

import com.dark.appmanejementugasharian.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun getAllTasks() = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}