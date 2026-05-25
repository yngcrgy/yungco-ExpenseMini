package edu.cit.yungco.expensemini.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.AdminApiService
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.ui.adapter.AdminUserAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminActivity : AppCompatActivity() {

    private lateinit var apiService: AdminApiService
    private lateinit var adapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        apiService = ApiClient.getAdminService(this)

        setupRecyclerView()
        loadUsers()
        loadStats()
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter { userId ->
            deleteUser(userId)
        }
        val rv = findViewById<RecyclerView>(R.id.rvAdminUsers)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun deleteUser(userId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteUser(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminActivity, "User deleted", Toast.LENGTH_SHORT).show()
                        loadUsers()
                        loadStats()
                    } else {
                        Toast.makeText(this@AdminActivity, "Failed to delete user", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadStats() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getStats()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val stats = response.body()!!.data!!
                        findViewById<TextView>(R.id.tvStatUsers).text = stats.total_users.toString()
                        findViewById<TextView>(R.id.tvStatExpenses).text = stats.total_expenses_count.toString()
                        findViewById<TextView>(R.id.tvStatVolume).text = "₱${String.format("%.2f", stats.total_system_money)}"
                    }
                }
            } catch (e: Exception) {
                // Ignore silent fail for stats
            }
        }
    }

    private fun loadUsers() {
        val progressBar = findViewById<ProgressBar>(R.id.progressAdmin)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getAllUsers()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.data != null) {
                        adapter.updateData(response.body()!!.data!!)
                    } else {
                        Toast.makeText(this@AdminActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
