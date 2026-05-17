package com.nurfadiya.expensetracker.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nurfadiya.expensetracker.databinding.FragmentBudgetBinding
import java.text.NumberFormat
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan budget tersimpan
        val savedBudget = viewModel.loadBudget()
        if (savedBudget > 0) {
            binding.etBudget.setText(savedBudget.toString())
        }

        observeViewModel()

        binding.btnSaveBudget.setOnClickListener {
            val amount = binding.etBudget.text.toString().toLongOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Masukkan budget yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveBudget(amount)
            Toast.makeText(context, "Budget tersimpan!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            updateUI(expense, viewModel.budget.value ?: 0L)
        }

        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            updateUI(viewModel.totalExpense.value ?: 0L, budget)
        }
    }

    private fun updateUI(expense: Long, budget: Long) {
        val pct = viewModel.calculatePercentage(expense, budget)
        val remaining = (budget - expense).coerceAtLeast(0L)

        binding.tvExpenseAmount.text = formatRupiah(expense)
        binding.tvRemainingBudget.text = formatRupiah(remaining)
        binding.tvUsedPct.text = "$pct%"
        binding.progressCircle.progress = pct.coerceAtMost(100)
    }

    private fun formatRupiah(amount: Long): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            .format(amount)
            .replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
