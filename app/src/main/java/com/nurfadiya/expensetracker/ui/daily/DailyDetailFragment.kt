package com.nurfadiya.expensetracker.ui.daily

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nurfadiya.expensetracker.R
import com.nurfadiya.expensetracker.databinding.FragmentDailyDetailBinding
import com.nurfadiya.expensetracker.ui.adapter.TransactionAdapter
import com.nurfadiya.expensetracker.ui.home.HomeViewModel

class DailyDetailFragment : Fragment(R.layout.fragment_daily_detail) {

    private var _binding: FragmentDailyDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DailyDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDailyDetailBinding.bind(view)

        val date = DailyDetailFragmentArgs.fromBundle(requireArguments()).selectedDate
        binding.tvDateTitle.text = getString(R.string.laporan_harian_format, date)
        
        val adapter = TransactionAdapter(
            onItemClick = { transaction ->
                val bundle = Bundle().apply { putInt("transactionId", transaction.id) }
                findNavController().navigate(R.id.action_home_to_addTransaction, bundle)
            },
            onItemDelete = { /* Opsional: tambah logika delete */ }
        )
        binding.rvDailyTransactions.adapter = adapter

        viewModel.getTransactionsByDate(date).observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}