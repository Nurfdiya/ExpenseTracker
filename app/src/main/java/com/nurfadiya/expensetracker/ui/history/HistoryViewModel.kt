package com.nurfadiya.expensetracker.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.nurfadiya.expensetracker.data.db.AppDatabase
import com.nurfadiya.expensetracker.data.model.Transaction
import com.nurfadiya.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository(
        AppDatabase.getInstance(application).transactionDao()
    )

    private val searchQuery = MutableLiveData("")

    val transactions = searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            repository.getAll()
        } else {
            repository.search(query)
        }
    }

    fun search(query: String) {
        searchQuery.value = query
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}
