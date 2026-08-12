package com.dark.appmanejementugasharian.data

import androidx.room.*
import com.dark.appmanejementugasharian.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_table ORDER BY isCompleted ASC, CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, deadline ASC")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}