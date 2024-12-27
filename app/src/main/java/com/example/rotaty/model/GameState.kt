package com.example.rotaty.model

data class GameState(
    val targetText: String = "",
    val currentInput: String = "",
    val timeLeft: Int = 60,
    val isGameOver: Boolean = false,
    val isSuccess: Boolean = false,
    val correctChars: Int = 0,
    val lastCharCorrect: Boolean = true,
    val typingSpeed: Double = 0.0,  // знаков в минуту
    val accuracy: Double = 0.0,
    val totalKeystrokes: Int = 0
)

data class CharacterState(
    val char: Char,
    val isTyped: Boolean = false,
    val isCorrect: Boolean = false
)

data class TypingStats(
    val charsPerMinute: Double = 0.0,
    val accuracy: Double = 0.0,
    val correctChars: Int = 0,
    val totalChars: Int = 0
)