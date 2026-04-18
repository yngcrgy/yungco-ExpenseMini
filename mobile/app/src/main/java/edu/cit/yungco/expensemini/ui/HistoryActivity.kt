package edu.cit.yungco.expensemini.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.models.Expense
import edu.cit.yungco.expensemini.network.models.ExpenseRequest
import edu.cit.yungco.expensemini.ui.adapter.HistoryExpenseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var apiService: ExpenseApiService
    private lateinit var adapter: HistoryExpenseAdapter
    private var allExpenses: List<Expense> = emptyList()
    private var searchQuery = ""
    private var filterCategory = "All"

    private val categories = listOf("All", "Food", "Transport", "Personal", "School", "Other")
    private val categoryMap = mapOf(
        "Food" to 1, "Transport" to 2, "Personal" to 3, "School" to 4, "Other" to 5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        apiService = ApiClient.getExpenseService(this)

        setupAdapter()
        setupSearch()
        setupFilter()
        setupBottomNav()
        loadExpenses()
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun setupAdapter() {
        adapter = HistoryExpenseAdapter(
            onEdit = { expense -> showEditDialog(expense) },
            onDelete = { expense -> showDeleteDialog(expense) }
        )
        val rv = findViewById<RecyclerView>(R.id.rvExpenses)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                applyFilters()
            }
        })
    }

    private fun setupFilter() {
        val spinner = findViewById<Spinner>(R.id.spinnerFilter)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                filterCategory = categories[pos]
                applyFilters()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun applyFilters() {
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
        findViewById<TextView>(R.id.tvExpenseCount).text = "${filtered.size} expense${if (filtered.size != 1) "s" else ""}"
    }

    private fun loadExpenses() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getExpenses()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        allExpenses = response.body()!!.data!!.sortedByDescending { it.expenseDate }
                        applyFilters()
                    }
                }
            } catch (e: Exception) {
                Log.e("HistoryActivity", "Error loading expenses", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HistoryActivity, "Error loading expenses", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showEditDialog(expense: Expense) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_expense, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etTitle = dialogView.findViewById<EditText>(R.id.etEditTitle)
        val etAmount = dialogView.findViewById<EditText>(R.id.etEditAmount)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerEditCategory)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveEdit)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelEdit)
        val btnClose = dialogView.findViewById<TextView>(R.id.btnCloseEdit)

        // Pre-fill values
        etTitle.setText(expense.title)
        etAmount.setText(expense.amount.toString())

        val editCategories = categories.drop(1) // Remove "All"
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, editCategories)
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
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@HistoryActivity, "Expense updated!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadExpenses()
                        } else {
                            Toast.makeText(this@HistoryActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HistoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(expense: Expense) {
        AlertDialog.Builder(this)
            .setTitle("Delete expense?")
            .setMessage("This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = apiService.deleteExpense(expense.getDisplayId())
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@HistoryActivity, "Expense deleted", Toast.LENGTH_SHORT).show()
                                loadExpenses()
                            } else {
                                Toast.makeText(this@HistoryActivity, "Failed to delete", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@HistoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_history
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
