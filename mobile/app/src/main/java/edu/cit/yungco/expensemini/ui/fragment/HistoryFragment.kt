package edu.cit.yungco.expensemini.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.models.Expense
import edu.cit.yungco.expensemini.network.models.ExpenseRequest
import edu.cit.yungco.expensemini.ui.adapter.HistoryExpenseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private lateinit var apiService: ExpenseApiService
    private lateinit var adapter: HistoryExpenseAdapter
    private var allExpenses: List<Expense> = emptyList()
    private var searchQuery = ""
    private var filterCategory = "All"

    private val categories = listOf("All", "Food", "Transport", "Personal", "School", "Other")
    private val categoryMap = mapOf(
        "Food" to 1, "Transport" to 2, "Personal" to 3, "School" to 4, "Other" to 5
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiClient.getExpenseService(requireContext())

        setupAdapter(view)
        setupSearch(view)
        setupFilter(view)
        loadExpenses(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { loadExpenses(it) }
    }

    private fun setupAdapter(view: View) {
        adapter = HistoryExpenseAdapter(
            onEdit = { expense -> showEditDialog(expense) },
            onDelete = { expense -> showDeleteDialog(expense) }
        )
        val rv = view.findViewById<RecyclerView>(R.id.rvExpenses)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }

    private fun setupSearch(view: View) {
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                applyFilters(view)
            }
        })
    }

    private fun setupFilter(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinnerFilter)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) {
                filterCategory = categories[pos]
                applyFilters(view)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun applyFilters(view: View) {
        var filtered = allExpenses

        if (filterCategory != "All") {
            filtered = filtered.filter { it.category == filterCategory }
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                (it.notes?.contains(searchQuery, ignoreCase = true) == true)
            }
        }

        adapter.updateData(filtered)
        view.findViewById<TextView>(R.id.tvExpenseCount).text = "${filtered.size} expense${if (filtered.size != 1) "s" else ""}"
    }

    private fun loadExpenses(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getExpenses()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        allExpenses = response.body()!!.data!!.sortedByDescending { it.expenseDate }
                        applyFilters(view)
                    }
                }
            } catch (e: Exception) {
                Log.e("HistoryFragment", "Error loading expenses", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading expenses", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showEditDialog(expense: Expense) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_expense, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etTitle = dialogView.findViewById<EditText>(R.id.etEditTitle)
        val etAmount = dialogView.findViewById<EditText>(R.id.etEditAmount)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerEditCategory)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveEdit)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelEdit)
        val btnClose = dialogView.findViewById<TextView>(R.id.btnCloseEdit)

        etTitle.setText(expense.title)
        etAmount.setText(expense.amount.toString())

        val editCategories = categories.drop(1)
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, editCategories)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = catAdapter

        val catIndex = editCategories.indexOf(expense.category ?: "Other")
        if (catIndex >= 0) spinnerCategory.setSelection(catIndex)

        btnClose.setOnClickListener { dialog.dismiss() }
        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()

            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = ExpenseRequest(
                title = title,
                amount = amount,
                categoryId = categoryMap[category],
                category = category,
                notes = expense.notes,
                expenseDate = expense.expenseDate
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiService.updateExpense(expense.getDisplayId(), request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Expense updated!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            view?.let { loadExpenses(it) }
                        } else {
                            Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(expense: Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete expense?")
            .setMessage("This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = apiService.deleteExpense(expense.getDisplayId())
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_SHORT).show()
                                view?.let { loadExpenses(it) }
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}