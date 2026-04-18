package edu.cit.yungco.expensemini.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.models.ExpenseRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var apiService: ExpenseApiService
    private var selectedDate: String = LocalDate.now().toString()

    private val categories = listOf("Food", "Transport", "Personal", "School", "Other")
    private val categoryMap = mapOf(
        "Food" to 1,
        "Transport" to 2,
        "Personal" to 3,
        "School" to 4,
        "Other" to 5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        apiService = ApiClient.getExpenseService(this)

        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()
        setupBottomNav()
    }

    private fun setupCategorySpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupDatePicker() {
        val tvDate = findViewById<TextView>(R.id.tvDate)
        tvDate.text = selectedDate

        tvDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                tvDate.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupSaveButton() {
        val btnSave = findViewById<Button>(R.id.btnSave)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val etNotes = findViewById<EditText>(R.id.etNotes)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val notes = etNotes.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            val request = ExpenseRequest(
                title = title,
                amount = amount,
                categoryId = categoryMap[category],
                category = category,
                notes = notes.ifEmpty { null },
                expenseDate = selectedDate
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiService.createExpense(request)
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save Expense"

                        if (response.isSuccessful) {
                            Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@AddExpenseActivity, "Failed to save expense", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AddExpense", "Error saving", e)
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save Expense"
                        Toast.makeText(this@AddExpenseActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_add
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }
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
