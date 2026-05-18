package com.nurfadiya.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val displayName: String,
    val colorCode: String,
    val iconRes: Int,
    val isFixed: Boolean = false
)
