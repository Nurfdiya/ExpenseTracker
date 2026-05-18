package com.nurfadiya.expensetracker.data.model

import androidx.room.Embedded

data class CategorySummary(
    @Embedded val category: CategoryEntity,
    val total: Long
)
