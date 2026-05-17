package com.nurfadiya.expensetracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nurfadiya.expensetracker.data.model.CategorySummary
import com.nurfadiya.expensetracker.data.model.DailyTotal
import com.nurfadiya.expensetracker.data.model.Transaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    // Semua transaksi bulan ini, urut terbaru
    // monthPrefix contoh: "2026-05"
    @Query("""
        SELECT * FROM transactions 
        WHERE date LIKE :monthPrefix || '%' 
        ORDER BY date DESC
    """)
    fun getByMonth(monthPrefix: String): LiveData<List<Transaction>>

    // Total pengeluaran bulan ini
    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE date LIKE :monthPrefix || '%'
    """)
    fun getTotalByMonth(monthPrefix: String): LiveData<Long>

    // Total per kategori bulan ini (untuk PieChart)
    @Query("""
        SELECT category, SUM(amount) as total 
        FROM transactions 
        WHERE date LIKE :monthPrefix || '%' 
        GROUP BY category
    """)
    fun getCategorySummary(monthPrefix: String): LiveData<List<CategorySummary>>

    // Total per hari bulan ini (untuk BarChart)
    @Query("""
        SELECT date, SUM(amount) as total 
        FROM transactions 
        WHERE date LIKE :monthPrefix || '%' 
        GROUP BY date 
        ORDER BY date ASC
    """)
    fun getDailyTotal(monthPrefix: String): LiveData<List<DailyTotal>>

    // Transaksi by ID (untuk edit)
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): Transaction?
}
