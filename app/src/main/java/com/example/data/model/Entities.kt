package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val name: String = "Disciple",
    val level: Int = 1,
    val xp: Int = 0,
    val strikingXp: Int = 10,
    val grapplingXp: Int = 10,
    val disciplineXp: Int = 10,
    val mentalXp: Int = 10,
    val conditioningXp: Int = 10,
    val nutritionXp: Int = 10
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Mental", "Discipline", "Physical", "Nutrition"
    val streak: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: String = "" // "YYYY-MM-DD"
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "MMA", "Boxing", "Kickboxing", "Muay Thai", "Wrestling", "BJJ", "Kyokushin Karate", "Conditioning"
    val durationMin: Int,
    val xpGained: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodName: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "sensei"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
