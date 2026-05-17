package com.nurfadiya.expensetracker.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.nurfadiya.expensetracker.data.db.AppDatabase
import com.nurfadiya.expensetracker.data.model.Transaction
import com.nurfadiya.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository(
        AppDatabase.getInstance(application).transactionDao()
    )

    // Bulan aktif default = bulan ini (format "yyyy-MM")
    val currentMonth = MutableLiveData(getCurrentMonthPrefix())

    // LiveData reaktif — otomatis update saat currentMonth berubah
    val transactions = currentMonth.switchMap { repository.getByMonth(it) }
    val totalExpense = currentMonth.switchMap { repository.getTotalByMonth(it) }
    val categorySummary = currentMonth.switchMap { repository.getCategorySummary(it) }
    val dailyTotal = currentMonth.switchMap { repository.getDailyTotal(it) }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.delete(transaction) }
    }

    fun setMonth(monthPrefix: String) {
        currentMonth.value = monthPrefix
    }

    private fun getCurrentMonthPrefix(): String {
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }
}
