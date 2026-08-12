package com.dark.appmanejementugasharian.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    var title: String,
    var description: String = "",
    var deadline: Long,
    var priority: String = "MEDIUM",
    var isCompleted: Boolean = false
)