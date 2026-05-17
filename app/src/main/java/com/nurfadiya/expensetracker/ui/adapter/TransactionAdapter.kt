package com.nurfadiya.expensetracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nurfadiya.expensetracker.data.model.Transaction
import com.nurfadiya.expensetracker.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemDelete: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTransactionBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            val color = android.graphics.Color.parseColor(transaction.category.colorCode)
            binding.ivCategoryIcon.setImageResource(transaction.category.iconRes)
            binding.ivCategoryIcon.imageTintList = android.content.res.ColorStateList.valueOf(color)
            binding.tvCategory.text = transaction.category.displayName
            binding.tvNote.text     = transaction.note.ifEmpty { "Tanpa catatan" }
            binding.tvDate.text     = transaction.date
            binding.tvAmount.text   = "-${formatRupiah(transaction.amount)}"

            binding.root.setOnClickListener { onItemClick(transaction) }
            binding.btnDelete.setOnClickListener { onItemDelete(transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace(",00", "").replace("Rp", "Rp")
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(old: Transaction, new: Transaction) = old.id == new.id
        override fun areContentsTheSame(old: Transaction, new: Transaction) = old == new
    }
}
