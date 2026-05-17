package com.nurfadiya.expensetracker.ui.home

import android.os.Bundle
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
            try {
                findNavController().navigate(R.id.action_home_to_addTransaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                sliceSpace = 3f
                valueTextColor = android.graphics.Color.WHITE
            }
            binding.pieChart.apply {
                data = PieData(dataSet)
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 50f
                setTransparentCircleAlpha(0)
                setHoleColor(android.graphics.Color.TRANSPARENT)
                
                legend.isEnabled = true
                legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
                
                animateY(1200, Easing.EaseInOutQuad)
                invalidate()
            }
        }

        // BarChart — per hari
        viewModel.dailyTotal.observe(viewLifecycleOwner) { dailyList ->
            if (dailyList.isEmpty()) return@observe

            val entries = dailyList.mapIndexed { index, daily ->
                BarEntry(index.toFloat(), daily.total.toFloat())
            }
            val labels = dailyList.map { it.date.takeLast(2) }

            val dataSet = BarDataSet(entries, "Pengeluaran").apply {
                color = resources.getColor(R.color.primary, null)
                valueTextSize = 10f
                setDrawValues(true)
                valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value > 0) "Rp ${value.toInt() / 1000}k" else ""
                    }
                }
            }

            binding.barChart.apply {
                data = BarData(dataSet)
                
                // Configure X Axis
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(labels)
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelCount = if (labels.size > 7) 7 else labels.size
                    textColor = resources.getColor(R.color.text_secondary, null)
                }
                
                // Configure Y Axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.LTGRAY
                    textColor = resources.getColor(R.color.text_secondary, null)
                    axisMinimum = 0f
                    // Reduce label count to avoid clutter
                    labelCount = 5
                }
                axisRight.isEnabled = false
                
                // Interaction
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                isDoubleTapToZoomEnabled = true
                
                description.isEnabled = false
                legend.isEnabled = false
                
                // Extra space for labels
                extraBottomOffset = 10f
                
                animateY(1000, Easing.EaseInOutQuad)
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
