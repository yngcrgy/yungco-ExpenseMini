package edu.cit.yungco.expensemini.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.models.ExpenseRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar

class AddExpenseFragment : Fragment() {

    private lateinit var apiService: ExpenseApiService
    private var selectedDate: String = LocalDate.now().toString()

    private var categories = mutableListOf("Food", "Transport", "Personal", "School", "Other")
    private var categoryMap = mutableMapOf(
        "Food" to 1,
        "Transport" to 2,
        "Personal" to 3,
        "School" to 4,
        "Other" to 5
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiClient.getExpenseService(requireContext())

        setupCategorySpinner(view)
        setupDatePicker(view)
        setupSaveButton(view)
        
        fetchCategories(view)
    }

    private fun fetchCategories(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCategories()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val fetchedCats = response.body()!!.data!!
                        if (fetchedCats.isNotEmpty()) {
                            categories.clear()
                            categoryMap.clear()
                            for (c in fetchedCats) {
                                categories.add(c.name)
                                categoryMap[c.name] = c.id
                            }
                            setupCategorySpinner(view)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AddExpense", "Error fetching categories", e)
            }
        }
    }

    private fun setupCategorySpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinnerCategory)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupDatePicker(view: View) {
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        tvDate.text = selectedDate

        tvDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                tvDate.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupSaveButton(view: View) {
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val notes = etNotes.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(requireContext(), "Expense saved!", Toast.LENGTH_SHORT).show()
                            // Clear fields
                            etTitle.setText("")
                            etAmount.setText("")
                            etNotes.setText("")
                        } else {
                            Toast.makeText(requireContext(), "Failed to save expense", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AddExpense", "Error saving", e)
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save Expense"
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}