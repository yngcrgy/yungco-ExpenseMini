package edu.cit.yungco.expensemini.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.SessionManager
import edu.cit.yungco.expensemini.network.models.BudgetRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ExpenseApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)
        apiService = ApiClient.getExpenseService(this)

        loadUserInfo()
        setupSaveButton()
        setupLogout()
        setupBottomNav()
    }

    private fun loadUserInfo() {
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val etName = findViewById<EditText>(R.id.etProfileName)
        val etEmail = findViewById<EditText>(R.id.etProfileEmail)

        tvUserName.text = sessionManager.getFullName()
        tvUserEmail.text = sessionManager.getEmail()
        etName.setText(sessionManager.getFullName())
        etEmail.setText(sessionManager.getEmail())
    }

    private fun setupSaveButton() {
        val btnSave = findViewById<Button>(R.id.btnSaveSettings)
        val etBudget = findViewById<EditText>(R.id.etBudget)

        btnSave.setOnClickListener {
            val budgetStr = etBudget.text.toString().trim()

            if (budgetStr.isEmpty()) {
                Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val budgetAmount = budgetStr.toDoubleOrNull()
            if (budgetAmount == null || budgetAmount <= 0) {
                Toast.makeText(this, "Please enter a valid budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            val calendar = Calendar.getInstance()
            val request = BudgetRequest(
                month = calendar.get(Calendar.MONTH) + 1,
                year = calendar.get(Calendar.YEAR),
                budgetLimit = budgetAmount
            )

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiService.setBudget(request)
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save Settings"

                        if (response.isSuccessful) {
                            Toast.makeText(this@ProfileActivity, "Settings saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ProfileActivity, "Failed to save settings", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileActivity", "Error saving settings", e)
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save Settings"
                        Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupLogout() {
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile
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
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}
