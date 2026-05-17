package com.nurfadiya.expensetracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.nurfadiya.expensetracker.R
import com.nurfadiya.expensetracker.databinding.FragmentHomeBinding
import com.nurfadiya.expensetracker.ui.adapter.TransactionAdapter
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addTransaction)
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onItemClick = { transaction ->
                val action = HomeFragmentDirections
                    .actionHomeToAddTransaction(transaction.id)
                findNavController().navigate(action)
            },
            onItemDelete = { transaction ->
                viewModel.deleteTransaction(transaction)
            }
        )
        binding.rvTransactions.adapter = adapter
    }

    private fun observeViewModel() {
        // List transaksi
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // Total pengeluaran
        viewModel.totalExpense.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = formatRupiah(total)
        }

        // PieChart — per kategori
        viewModel.categorySummary.observe(viewLifecycleOwner) { summaries ->
            val entries = summaries.map {
                PieEntry(it.total.toFloat(), it.category.emoji)
            }
            val dataSet = PieDataSet(entries, "").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 12f
            }
            binding.pieChart.apply {
                data = PieData(dataSet)
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 40f
                animateY(800)
                invalidate()
            }
        }

        // BarChart — per hari
        viewModel.dailyTotal.observe(viewLifecycleOwner) { dailyList ->
            val entries = dailyList.mapIndexed { index, daily ->
                BarEntry(index.toFloat(), daily.total.toFloat())
            }
            val labels = dailyList.map { it.date.takeLast(2) }

            val dataSet = BarDataSet(entries, "Pengeluaran Harian").apply {
                color = resources.getColor(R.color.purple_500, null)
                valueTextSize = 10f
            }

            binding.barChart.apply {
                data = BarData(dataSet)
                xAxis.valueFormatter =
                    com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
                description.isEnabled = false
                animateY(800)
                invalidate()
            }
        }
    }

    private fun formatRupiah(amount: Long): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
