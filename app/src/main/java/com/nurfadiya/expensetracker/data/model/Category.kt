package com.nurfadiya.expensetracker.data.model

import com.nurfadiya.expensetracker.R

enum class Category(
    val displayName: String,
    val colorCode: String,
    val iconRes: Int,
    val isFixed: Boolean = false
) {
    FOOD("Makan & Minum", "#C1FF72", R.drawable.ic_food, false),
    TRANSPORT("Transport", "#00D2FF", R.drawable.ic_transport, false),
    SHOPPING("Belanja", "#FF6B6B", R.drawable.ic_shopping, false),
    ENTERTAINMENT("Hiburan", "#FFD700", R.drawable.ic_entertainment, false),
    HEALTH("Kesehatan", "#A78BFF", R.drawable.ic_health, false),
    CICILAN("Cicilan", "#FF85FF", R.drawable.ic_other, true),
    OTHER("Lainnya", "#8B949E", R.drawable.ic_other, false)
}
