package com.nurfadiya.expensetracker.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nurfadiya.expensetracker.R
import com.nurfadiya.expensetracker.data.model.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(private val database: AppDatabase) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.categoryDao()
            val defaultCategories = listOf(
                CategoryEntity(0, "Makan & Minum", "#C1FF72", R.drawable.ic_food, true),
                CategoryEntity(0, "Transport", "#00D2FF", R.drawable.ic_transport, true),
                CategoryEntity(0, "Belanja", "#FF6B6B", R.drawable.ic_shopping, true),
                CategoryEntity(0, "Hiburan", "#FFD700", R.drawable.ic_entertainment, true),
                CategoryEntity(0, "Kesehatan", "#A78BFF", R.drawable.ic_health, true),
                CategoryEntity(0, "Cicilan", "#FF85FF", R.drawable.ic_other, true),
                CategoryEntity(0, "Lainnya", "#8B949E", R.drawable.ic_other, true)
            )
            for (category in defaultCategories) {
                dao.insert(category)
            }
        }
    }
}
