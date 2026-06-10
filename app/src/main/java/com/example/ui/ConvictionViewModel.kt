package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiManager
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ConvictionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConvictionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ConvictionRepository(db)
    private val gemini = GeminiManager()

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allHabits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorkouts: StateFlow<List<WorkoutLog>> = repository.allWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFoods: StateFlow<List<FoodLog>> = repository.allFoods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isModelLoading = MutableStateFlow(false)
    val isModelLoading: StateFlow<Boolean> = _isModelLoading.asStateFlow()

    private val _foodScanResult = MutableStateFlow<String?>(null)
    val foodScanResult: StateFlow<String?> = _foodScanResult.asStateFlow()

    init {
        viewModelScope.launch {
            // Ensure user stats exist
            repository.getOrCreateUserStats()
            
            // Seed default habits if empty
            allHabits.first { true } // wait for initial fetch or trigger
            if (allHabits.value.isEmpty()) {
                repository.addHabit("Sunrise Breath Meditation", "Mental")
                repository.addHabit("Drink 3 Liters of Water", "Nutrition")
                repository.addHabit("Log Physical Martial Drills", "Physical")
                repository.addHabit("No Cheap Dopamine / Fasting", "Discipline")
            }
        }
    }

    fun addHabit(name: String, category: String) {
        viewModelScope.launch {
            repository.addHabit(name, category)
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabit(habit, repository.getCurrentDateString())
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun logWorkout(title: String, category: String, durationMin: Int) {
        viewModelScope.launch {
            repository.logWorkout(title, category, durationMin)
        }
    }

    fun logFood(name: String, cals: Int, protein: Double, carbs: Double, fat: Double) {
        viewModelScope.launch {
            repository.logFood(name, cals, protein, carbs, fat)
        }
    }

    fun deleteFoodLog(log: FoodLog) {
        viewModelScope.launch {
            repository.deleteFoodLog(log)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            // Save user message
            repository.addChatMessage("user", text)
            _isModelLoading.value = true

            // Ask Gemini Sensei (incorporating existing message history)
            val response = gemini.askSensei(allMessages.value, text)
            
            // Save Sensei response
            repository.addChatMessage("sensei", response)
            _isModelLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
            repository.addChatMessage("sensei", "Welcome, Disciple. I am your Sensei. Ask me physical questions, combat mechanics, weekly workouts, math or logic problems, or daily self-improvement tips.")
        }
    }

    fun scanFoodImage(bitmap: Bitmap, description: String) {
        viewModelScope.launch {
            _isModelLoading.value = true
            _foodScanResult.value = "Analyzing food image for nutritional composition..."
            
            val promptDescr = if (description.isNotBlank()) description else "Identify this food plate."
            val response = gemini.analyzeFoodImage(bitmap, promptDescr)
            
            _foodScanResult.value = response
            _isModelLoading.value = false
        }
    }

    fun clearFoodScanResult() {
        _foodScanResult.value = null
    }

    // Manual XP administration for special tasks
    fun awardSpecialMentalXp(points: Int = 10) {
        viewModelScope.launch {
            repository.addXp("mental", points)
        }
    }
}
