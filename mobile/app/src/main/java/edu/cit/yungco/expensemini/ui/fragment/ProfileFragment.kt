package edu.cit.yungco.expensemini.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.SessionManager
import edu.cit.yungco.expensemini.network.models.BudgetRequest
import edu.cit.yungco.expensemini.ui.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ExpenseApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        apiService = ApiClient.getExpenseService(requireContext())

        loadUserInfo(view)
        setupSaveButton(view)
        setupLogout(view)
    }

    private fun loadUserInfo(view: View) {
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val etName = view.findViewById<EditText>(R.id.etProfileName)
        val etEmail = view.findViewById<EditText>(R.id.etProfileEmail)

        tvUserName.text = sessionManager.getFullName()
        tvUserEmail.text = sessionManager.getEmail()
        etName.setText(sessionManager.getFullName())
        etEmail.setText(sessionManager.getEmail())
    }

    private fun setupSaveButton(view: View) {
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)
        val etBudget = view.findViewById<EditText>(R.id.etBudget)

        btnSave.setOnClickListener {
            val budgetStr = etBudget.text.toString().trim()

            if (budgetStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val budgetAmount = budgetStr.toDoubleOrNull()
            if (budgetAmount == null || budgetAmount <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid budget", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to save settings", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileFragment", "Error saving settings", e)
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save Settings"
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val btnResetBudget = view.findViewById<Button>(R.id.btnResetBudget)
        btnResetBudget.setOnClickListener {
            btnResetBudget.isEnabled = false
            btnResetBudget.text = "Resetting..."
            
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiService.resetBudget()
                    withContext(Dispatchers.Main) {
                        btnResetBudget.isEnabled = true
                        btnResetBudget.text = "Reset Monthly Budget"
                        
                        if (response.isSuccessful) {
                            etBudget.setText("")
                            Toast.makeText(requireContext(), "Budget reset successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to reset budget", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileFragment", "Error resetting budget", e)
                    withContext(Dispatchers.Main) {
                        btnResetBudget.isEnabled = true
                        btnResetBudget.text = "Reset Monthly Budget"
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupLogout(view: View) {
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }
}