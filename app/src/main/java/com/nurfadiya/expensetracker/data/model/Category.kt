package com.nurfadiya.expensetracker.data.model

import com.nurfadiya.expensetracker.R

enum class Category(val displayName: String, val colorCode: String, val iconRes: Int) {
    FOOD("Makan & Minum", "#C1FF72", com.nurfadiya.expensetracker.R.drawable.ic_food),
    TRANSPORT("Transport", "#4ade80", com.nurfadiya.expensetracker.R.drawable.ic_transport),
    SHOPPING("Belanja", "#6366F1", com.nurfadiya.expensetracker.R.drawable.ic_shopping),
    ENTERTAINMENT("Hiburan", "#F59E0B", com.nurfadiya.expensetracker.R.drawable.ic_entertainment),
    HEALTH("Kesehatan", "#FF7B72", com.nurfadiya.expensetracker.R.drawable.ic_health),
    OTHER("Lainnya", "#8B949E", com.nurfadiya.expensetracker.R.drawable.ic_other)
}
