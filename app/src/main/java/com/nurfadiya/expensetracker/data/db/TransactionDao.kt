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

    @Query("""
        SELECT * FROM transactions 
        WHERE date LIKE :monthPrefix || '%' 
        ORDER BY date DESC
    """)
    fun getByMonth(monthPrefix: String): LiveData<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE date LIKE :monthPrefix || '%'
    """)
    fun getTotalByMonth(monthPrefix: String): LiveData<Long>

    @Query("""
        SELECT c.*, SUM(t.amount) as total 
        FROM transactions t
        INNER JOIN categories c ON t.categoryId = c.id
        WHERE t.date LIKE :monthPrefix || '%' 
        GROUP BY c.id
    """)
    fun getCategorySummary(monthPrefix: String): LiveData<List<CategorySummary>>

    @Query("""
        SELECT date, SUM(amount) as total 
        FROM transactions 
        WHERE date LIKE :monthPrefix || '%' 
        GROUP BY date 
        ORDER BY date ASC
    """)
    fun getDailyTotal(monthPrefix: String): LiveData<List<DailyTotal>>

    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY id DESC")
    fun getByDate(date: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' ORDER BY date DESC, id DESC")
    fun search(query: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Int): Transaction?
}
