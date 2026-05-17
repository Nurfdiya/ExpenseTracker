package com.nurfadiya.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.nurfadiya.expensetracker.data.db.TransactionDao
import com.nurfadiya.expensetracker.data.model.*

class TransactionRepository(private val dao: TransactionDao) {

    fun getByMonth(monthPrefix: String): LiveData<List<Transaction>> =
        dao.getByMonth(monthPrefix)

    fun getTotalByMonth(monthPrefix: String): LiveData<Long> =
        dao.getTotalByMonth(monthPrefix)

    fun getCategorySummary(monthPrefix: String): LiveData<List<CategorySummary>> =
        dao.getCategorySummary(monthPrefix)

    fun getDailyTotal(monthPrefix: String): LiveData<List<DailyTotal>> =
        dao.getDailyTotal(monthPrefix)

    suspend fun insert(transaction: Transaction) = dao.insert(transaction)

    suspend fun update(transaction: Transaction) = dao.update(transaction)

    suspend fun delete(transaction: Transaction) = dao.delete(transaction)

    suspend fun getById(id: Int): Transaction? = dao.getById(id)
}
