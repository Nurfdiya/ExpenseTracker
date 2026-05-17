package com.nurfadiya.expensetracker.ui.budget

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nurfadiya.expensetracker.data.db.AppDatabase
import com.nurfadiya.expensetracker.data.repository.TransactionRepository
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    private val repository = TransactionRepository(
        AppDatabase.getInstance(application).transactionDao()
    )

    val budget = MutableLiveData(loadBudget())

    val totalExpense = repository.getTotalByMonth(getCurrentMonthPrefix())

    fun saveBudget(amount: Long) {
        prefs.edit().putLong("monthly_budget", amount).apply()
        budget.value = amount
    }

    fun loadBudget(): Long = prefs.getLong("monthly_budget", 0L)

    fun calculatePercentage(expense: Long, budgetAmount: Long): Int {
        if (budgetAmount <= 0) return 0
        return ((expense.toFloat() / budgetAmount) * 100).toInt()
    }

    private fun getCurrentMonthPrefix(): String =
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
}
