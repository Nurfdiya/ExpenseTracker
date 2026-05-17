package com.nurfadiya.expensetracker.data.model

enum class Category(val displayName: String, val emoji: String) {
    FOOD("Makan & Minum", "🍜"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Belanja", "🛍️"),
    ENTERTAINMENT("Hiburan", "🎮"),
    HEALTH("Kesehatan", "💊"),
    OTHER("Lainnya", "📦")
}
