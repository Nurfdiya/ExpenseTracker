package com.nurfadiya.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Long,           // nominal dalam rupiah
    val categoryId: Int,        // ID kategori
    val note: String = "",      // catatan opsional
    val date: String            // format "yyyy-MM-dd"
)
