package com.nurfadiya.expensetracker.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nurfadiya.expensetracker.R
import com.nurfadiya.expensetracker.databinding.FragmentHistoryBinding
import com.nurfadiya.expensetracker.ui.adapter.TransactionAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onItemClick = { transaction ->
                val bundle = Bundle().apply {
                    putInt("transactionId", transaction.id)
                }
                findNavController().navigate(R.id.action_historyFragment_to_addTransactionFragment, bundle)
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
        binding.rvHistory.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.layoutEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
