package com.nurfadiya.expensetracker.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nurfadiya.expensetracker.data.model.Category
import com.nurfadiya.expensetracker.databinding.FragmentAddTransactionBinding
import java.util.Calendar

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModels()
    private val args: AddTransactionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupDatePicker()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Mode edit jika ada ID
        val editId = if (args.transactionId != -1) args.transactionId else null
        editId?.let {
            viewModel.loadTransaction(it)
            binding.btnSave.text = "Update"
        }

        binding.btnSave.setOnClickListener {
            viewModel.amount.value = binding.etAmount.text.toString()
            viewModel.note.value = binding.etNote.text.toString()
            viewModel.save(editId)
        }
    }

    private fun setupCategorySpinner() {
        val categories = Category.values()
        val labels = categories.map { it.displayName }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            labels
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?, position: Int, id: Long
                ) {
                    viewModel.category.value = categories[position]
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun setupDatePicker() {
        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val dateStr = "%04d-%02d-%02d".format(year, month + 1, day)
                    viewModel.date.value = dateStr
                    binding.btnPickDate.text = dateStr
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun observeViewModel() {
        viewModel.date.observe(viewLifecycleOwner) {
            binding.btnPickDate.text = it
        }

        viewModel.isSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(context, "Tersimpan!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        viewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
