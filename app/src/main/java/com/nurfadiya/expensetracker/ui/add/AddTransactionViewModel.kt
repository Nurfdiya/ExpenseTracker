package com.nurfadiya.expensetracker.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nurfadiya.expensetracker.data.db.AppDatabase
import com.nurfadiya.expensetracker.data.model.CategoryEntity
import com.nurfadiya.expensetracker.data.model.Transaction
import com.nurfadiya.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository(
        AppDatabase.getInstance(application).transactionDao()
    )

    val amount   = MutableLiveData("")
    val note     = MutableLiveData("")
    val categoryId = MutableLiveData<Int>() // ID Kategori
    val date     = MutableLiveData(getTodayDate())
    val isSaved  = MutableLiveData(false)
    val errorMsg = MutableLiveData<String?>()

    // Mode edit — load data transaksi existing
    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val transaction = repository.getById(id) ?: return@launch
            amount.value   = transaction.amount.toString()
            note.value     = transaction.note
            categoryId.value = transaction.categoryId
            date.value     = transaction.date
        }
    }

    fun save(editId: Int? = null) {
        val amountVal = amount.value?.toLongOrNull()
        if (amountVal == null || amountVal <= 0) {
            errorMsg.value = "Nominal tidak valid"
            return
        }

        val transaction = Transaction(
            id         = editId ?: 0,
            amount     = amountVal,
            categoryId = categoryId.value ?: 1, // Default ID 1
            note       = note.value ?: "",
            date       = date.value ?: getTodayDate()
        )

        viewModelScope.launch {
            if (editId != null) repository.update(transaction)
            else repository.insert(transaction)
            isSaved.value = true
        }
    }

    private fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
