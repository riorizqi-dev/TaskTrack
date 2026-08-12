package com.dark.appmanejementugasharian.utils

enum class Priority {
    HIGH, MEDIUM, LOW;

    companion object {
        fun fromString(value: String): Priority {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                MEDIUM
            }
        }
    }

    fun getColor(): Int = when (this) {
        HIGH -> 0xFFFF0000.toInt()
        MEDIUM -> 0xFFFFA500.toInt()
        LOW -> 0xFF00FF00.toInt()
    }
}