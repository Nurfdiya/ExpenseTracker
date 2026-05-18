package com.nurfadiya.expensetracker.ui.daily

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nurfadiya.expensetracker.data.db.AppDatabase
import com.nurfadiya.expensetracker.data.model.Transaction
import com.nurfadiya.expensetracker.data.repository.TransactionRepository

class DailyDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TransactionRepository(
        AppDatabase.getInstance(application).transactionDao()
    )

    fun getTransactionsByDate(date: String): LiveData<List<Transaction>> {
        return repository.getByDate(date)
    }
}