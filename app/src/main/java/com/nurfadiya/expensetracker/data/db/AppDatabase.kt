package com.nurfadiya.expensetracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nurfadiya.expensetracker.R
import com.nurfadiya.expensetracker.data.model.Category
import com.nurfadiya.expensetracker.data.model.CategoryEntity
import com.nurfadiya.expensetracker.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(name: String): Category = try {
        Category.valueOf(name)
    } catch (e: Exception) {
        Category.OTHER
    }
}

@Database(entities = [Transaction::class, CategoryEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = INSTANCE?.categoryDao() ?: return@launch
                            val defaultCategories = listOf(
                                CategoryEntity(0, "Makan & Minum", "#C1FF72", R.drawable.ic_food, true),
                                CategoryEntity(0, "Transport", "#00D2FF", R.drawable.ic_transport, true),
                                CategoryEntity(0, "Belanja", "#FF6B6B", R.drawable.ic_shopping, true),
                                CategoryEntity(0, "Hiburan", "#FFD700", R.drawable.ic_entertainment, true),
                                CategoryEntity(0, "Kesehatan", "#A78BFF", R.drawable.ic_health, true),
                                CategoryEntity(0, "Cicilan", "#FF85FF", R.drawable.ic_other, true),
                                CategoryEntity(0, "Lainnya", "#8B949E", R.drawable.ic_other, true)
                            )
                            defaultCategories.forEach { dao.insert(it) }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                db.also { INSTANCE = it }
            }
        }
    }
}
