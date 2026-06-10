package com.example.data.repository

import com.example.data.database.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConvictionRepository(private val db: AppDatabase) {

    private val userStatsDao = db.userStatsDao()
    private val habitDao = db.habitDao()
    private val workoutLogDao = db.workoutLogDao()
    private val foodLogDao = db.foodLogDao()
    private val chatMessageDao = db.chatMessageDao()

    val userStats: Flow<UserStats?> = userStatsDao.getUserStatsFlow()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabitsFlow()
    val allWorkouts: Flow<List<WorkoutLog>> = workoutLogDao.getAllWorkoutLogsFlow()
    val allFoods: Flow<List<FoodLog>> = foodLogDao.getAllFoodLogsFlow()
    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllChatMessagesFlow()

    suspend fun getOrCreateUserStats(): UserStats {
        val existing = userStatsDao.getUserStatsDirect()
        if (existing != null) return existing
        val defaultStats = UserStats()
        userStatsDao.insertOrUpdateUserStats(defaultStats)
        return defaultStats
    }

    suspend fun addXp(category: String, amount: Int) {
        val stats = getOrCreateUserStats()
        
        var striking = stats.strikingXp
        var grappling = stats.grapplingXp
        var discipline = stats.disciplineXp
        var mental = stats.mentalXp
        var conditioning = stats.conditioningXp
        var nutrition = stats.nutritionXp

        when (category.lowercase()) {
            "striking", "boxing", "kickboxing", "muay thai", "kyokushin karate" -> {
                striking += amount
                conditioning += amount / 2
            }
            "grappling", "wrestling", "bjj" -> {
                grappling += amount
                conditioning += amount / 2
            }
            "mma" -> {
                grappling += amount / 2
                striking += amount / 2
                conditioning += amount / 2
            }
            "discipline", "habit", "habits" -> {
                discipline += amount
            }
            "mental", "toughness", "coach", "strength" -> {
                mental += amount
            }
            "conditioning", "workout", "physical" -> {
                conditioning += amount
            }
            "nutrition", "food" -> {
                nutrition += amount
            }
        }

        val addedToCategory = amount
        val newXp = stats.xp + addedToCategory
        // Simple rewarding leveling curve: Level = (newXp / 150) + 1
        val newLevel = (newXp / 150) + 1

        val updated = stats.copy(
            level = newLevel,
            xp = newXp,
            strikingXp = striking.coerceAtMost(100),
            grapplingXp = grappling.coerceAtMost(100),
            disciplineXp = discipline.coerceAtMost(100),
            mentalXp = mental.coerceAtMost(100),
            conditioningXp = conditioning.coerceAtMost(100),
            nutritionXp = nutrition.coerceAtMost(100)
        )
        userStatsDao.insertOrUpdateUserStats(updated)
    }

    suspend fun addHabit(name: String, category: String) {
        val habit = Habit(name = name, category = category)
        habitDao.insertHabit(habit)
    }

    suspend fun toggleHabit(habit: Habit, currentDateStr: String) {
        val isChecking = !habit.isCompletedToday
        var newStreak = habit.streak
        var xpReward = 0

        if (isChecking) {
            newStreak += 1
            xpReward = 15 // award 15 XP for completing habit
        } else {
            newStreak = (newStreak - 1).coerceAtLeast(0)
        }

        val updatedHabit = habit.copy(
            isCompletedToday = isChecking,
            streak = newStreak,
            lastCompletedDate = if (isChecking) currentDateStr else ""
        )
        habitDao.updateHabit(updatedHabit)

        if (xpReward > 0) {
            addXp("discipline", xpReward)
        }
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }

    suspend fun logWorkout(title: String, category: String, durationMin: Int) {
        // Higher intensity workout awards more XP
        val xpGained = durationMin + 15
        val log = WorkoutLog(
            title = title,
            category = category,
            durationMin = durationMin,
            xpGained = xpGained
        )
        workoutLogDao.insertWorkoutLog(log)
        
        // Award XP based on category
        addXp(category, xpGained)
    }

    suspend fun logFood(foodName: String, calories: Int, protein: Double, carbs: Double, fat: Double) {
        val log = FoodLog(
            foodName = foodName,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat
        )
        foodLogDao.insertFoodLog(log)
        addXp("nutrition", 15) // award 15 XP for logging food
    }

    suspend fun deleteFoodLog(log: FoodLog) {
        foodLogDao.deleteFoodLog(log)
    }

    suspend fun addChatMessage(sender: String, text: String) {
        val msg = ChatMessage(sender = sender, text = text)
        chatMessageDao.insertChatMessage(msg)
        if (sender == "user") {
            addXp("mental", 10) // Conversing with the coach boosts mental XP!
        }
    }

    suspend fun clearChatHistory() {
        chatMessageDao.clearChatHistory()
    }

    // Helper to format date
    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
