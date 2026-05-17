package com.nurfadiya.expensetracker.ui.budget

import android.graphics.Color
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
            val budget = viewModel.budget.value ?: 0L
            val pct = viewModel.calculatePercentage(expense, budget)

            binding.tvExpenseAmount.text = formatRupiah(expense)
            binding.tvBudgetAmount.text  = formatRupiah(budget)
            binding.progressBudget.progress = pct.coerceAtMost(100)

            // Warna progress sesuai kondisi
            val color = when {
                pct >= 90 -> Color.RED
                pct >= 70 -> Color.parseColor("#FFA500")
                else      -> Color.parseColor("#4CAF50")
            }
            binding.progressBudget.progressTintList =
                android.content.res.ColorStateList.valueOf(color)

            // Warning text
            binding.tvWarning.visibility = if (pct >= 90) View.VISIBLE else View.GONE
            binding.tvWarning.text = when {
                pct >= 100 -> "⚠️ Budget sudah habis!"
                pct >= 90  -> "⚠️ Budget hampir habis ($pct%)"
                else       -> ""
            }
        }

        viewModel.budget.observe(viewLifecycleOwner) {
            binding.tvBudgetAmount.text = formatRupiah(it)
        }
    }

    private fun formatRupiah(amount: Long): String =
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
