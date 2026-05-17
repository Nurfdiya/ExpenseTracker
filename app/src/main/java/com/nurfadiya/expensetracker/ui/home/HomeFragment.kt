package com.nurfadiya.expensetracker.ui.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        setupUI()
        setupRecyclerView()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_home_to_addTransaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh budget every time user returns to home (e.g. from Budget tab)
        viewModel.refreshBudget()
    }

    private fun setupUI() {
        // Highlight "Keuangan" in neon color
        val fullText = "Ringkasan Keuangan"
        val spannable = SpannableString(fullText)
        val startIndex = fullText.indexOf("Keuangan")
        if (startIndex != -1) {
            spannable.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primary, null)),
                startIndex,
                startIndex + "Keuangan".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvHeaderTitle.text = spannable
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onItemClick = { transaction ->
                try {
                    val bundle = Bundle().apply {
                        putInt("transactionId", transaction.id)
                    }
                    findNavController().navigate(R.id.action_home_to_addTransaction, bundle)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            onItemDelete = { transaction ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Transaksi?")
                    .setMessage("Apakah Anda yakin ingin menghapus catatan pengeluaran ini?")
                    .setNegativeButton("Batal", null)
                    .setPositiveButton("Hapus") { _, _ ->
                        viewModel.deleteTransaction(transaction)
                    }
                    .show()
            }
        )
        binding.rvTransactions.adapter = adapter
    }

    private fun observeViewModel() {
        // List transaksi
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.cardCharts.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }

        // Observe Budget from SharedPrefs
        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            binding.tvBudget.text = "Budget: ${formatRupiah(budget)}"
            updatePercentage(viewModel.totalExpense.value ?: 0L, budget)
        }

        // Total pengeluaran
        viewModel.totalExpense.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = formatRupiah(total)
            val budget = viewModel.budget.value ?: 0L
            updatePercentage(total, budget)
            updateSpendingInsight(total, budget)
        }

        // PieChart — per kategori
        viewModel.categorySummary.observe(viewLifecycleOwner) { summaries ->
            if (summaries.isNotEmpty()) {
                val variableExpenses = summaries.filter { !it.category.isFixed }
                val mostSpent = variableExpenses.maxByOrNull { it.total }
                binding.tvMostSpentCategory.text = "Kategori terboros: ${mostSpent?.category?.displayName ?: "-"}"
            } else {
                binding.tvMostSpentCategory.text = "Kategori terboros: -"
            }

            val entries = summaries.map {
                PieEntry(it.total.toFloat(), "") // No text label on slice
            }
            
            val chartColors = summaries.map { 
                android.graphics.Color.parseColor(it.category.colorCode)
            }

            val dataSet = PieDataSet(entries, "").apply {
                colors = chartColors
                valueTextSize = 12f
                valueTextColor = android.graphics.Color.WHITE
                valueTypeface = android.graphics.Typeface.DEFAULT_BOLD
                sliceSpace = 6f
                setDrawValues(true)
                valueFormatter = com.github.mikephil.charting.formatter.PercentFormatter(binding.pieChart)
            }
            binding.pieChart.apply {
                data = PieData(dataSet)
                setUsePercentValues(true)
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 80f
                setTransparentCircleAlpha(0)
                setHoleColor(android.graphics.Color.TRANSPARENT)
                
                // Aesthetic Center Text
                centerText = "Pengeluaran\nKategori"
                setCenterTextColor(android.graphics.Color.WHITE)
                setCenterTextSize(14f)
                
                // Legend
                legend.isEnabled = true
                legend.textColor = android.graphics.Color.parseColor("#8B949E")
                legend.textSize = 10f
                legend.form = com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.yOffset = 12f
                legend.xEntrySpace = 16f
                
                animateY(1400, Easing.EaseInOutQuart)
                invalidate()
            }
        }

        // BarChart — per hari
        viewModel.dailyTotal.observe(viewLifecycleOwner) { dailyList ->
            if (dailyList.isNotEmpty()) {
                val total = dailyList.sumOf { it.total }
                val avg = total / dailyList.size
                binding.tvDailyAverage.text = "Rata-rata harian: ${formatRupiah(avg)}"
            } else {
                binding.tvDailyAverage.text = "Rata-rata harian: Rp0"
            }

            if (dailyList.isEmpty()) return@observe

            // Ensure unique dates and proper sorting
            val uniqueList = dailyList.distinctBy { it.date }.sortedBy { it.date }
            
            val entries = uniqueList.mapIndexed { index, daily ->
                BarEntry(index.toFloat(), daily.total.toFloat())
            }
            val labels = uniqueList.map { it.date.takeLast(2) }

            val dataSet = BarDataSet(entries, "Pengeluaran").apply {
                color = resources.getColor(R.color.primary, null)
                setDrawValues(false)
                highLightAlpha = 0
            }

            binding.barChart.apply {
                data = BarData(dataSet).apply {
                    barWidth = 0.5f
                }
                
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    textColor = resources.getColor(R.color.text_secondary, null)
                    granularity = 1f
                    labelCount = labels.size
                    yOffset = 5f
                }
                
                axisLeft.apply {
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    textColor = resources.getColor(R.color.text_secondary, null)
                    axisMinimum = 0f
                    labelCount = 3
                    xOffset = 10f
                }
                axisRight.isEnabled = false
                
                description.isEnabled = false
                legend.isEnabled = false
                setScaleEnabled(false)
                setPinchZoom(false)
                setDoubleTapToZoomEnabled(false)

                animateY(1000, Easing.EaseInOutQuart)
                invalidate()
            }
        }
    }

    private fun updatePercentage(total: Long, budget: Long) {
        val percent = if (budget > 0) (total.toFloat() / budget * 100).toInt() else 0
        binding.tvUsedPercent.text = "Digunakan: $percent%"
    }

    private fun updateSpendingInsight(total: Long, budget: Long) {
        if (budget <= 0) {
            binding.tvSpendingStatus.text = "Status: Budget belum diatur"
            binding.tvSpendingStatus.setTextColor(android.graphics.Color.WHITE)
            return
        }
        val percent = (total.toFloat() / budget * 100).toInt()
        when {
            percent < 50 -> {
                binding.tvSpendingStatus.text = "Status: Sangat Aman 🟢"
                binding.tvSpendingStatus.setTextColor(resources.getColor(R.color.primary, null))
            }
            percent < 80 -> {
                binding.tvSpendingStatus.text = "Status: Perlu Waspada 🟡"
                binding.tvSpendingStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B"))
            }
            else -> {
                binding.tvSpendingStatus.text = "Status: Hampir Melebihi! 🔴"
                binding.tvSpendingStatus.setTextColor(android.graphics.Color.parseColor("#FF7B72"))
            }
        }
    }

    private fun formatRupiah(amount: Long): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount).replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
