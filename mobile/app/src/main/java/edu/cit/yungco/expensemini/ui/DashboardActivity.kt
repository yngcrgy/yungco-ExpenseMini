package edu.cit.yungco.expensemini.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.SessionManager
import edu.cit.yungco.expensemini.network.models.Expense
import edu.cit.yungco.expensemini.network.models.ExpenseRequest
import edu.cit.yungco.expensemini.ui.adapter.RecentExpenseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ExpenseApiService
    private lateinit var recentAdapter: RecentExpenseAdapter
    private val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)
        apiService = ApiClient.getExpenseService(this)

        setupRecyclerView()
        setupBottomNav()
        setupQuickAdd()
        setupClickListeners()

        loadDashboardData()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun setupRecyclerView() {
        recentAdapter = RecentExpenseAdapter()
        val rv = findViewById<RecyclerView>(R.id.rvRecentExpenses)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = recentAdapter
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_dashboard

        // Set active color
        bottomNav.itemIconTintList = getColorStateList(R.color.nav_active)
        bottomNav.itemTextColor = getColorStateList(R.color.nav_active)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_add -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    false
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    false
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        findViewById<TextView>(R.id.tvViewAll).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun setupQuickAdd() {
        val quickAddItems = listOf(
            Triple(R.id.btnQuickCoffee, "Coffee", 150.0),
            Triple(R.id.btnQuickTransport, "Transport", 50.0),
            Triple(R.id.btnQuickLunch, "Lunch", 120.0),
            Triple(R.id.btnQuickSnacks, "Snacks", 80.0)
        )

        for ((viewId, title, amount) in quickAddItems) {
            findViewById<LinearLayout>(viewId).setOnClickListener {
                quickAddExpense(title, amount)
            }
        }
    }

    private fun quickAddExpense(title: String, amount: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val categoryMap = mapOf(
                    "Coffee" to "Food",
                    "Transport" to "Transport",
                    "Lunch" to "Food",
                    "Snacks" to "Food"
                )
                val request = ExpenseRequest(
                    title = title,
                    amount = amount,
                    categoryId = null,
                    category = categoryMap[title] ?: "Other",
                    notes = "Quick add",
                    expenseDate = LocalDate.now().toString()
                )
                val response = apiService.createExpense(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@DashboardActivity, "$title added! ₱${amount.toInt()}", Toast.LENGTH_SHORT).show()
                        loadDashboardData()
                    } else {
                        Toast.makeText(this@DashboardActivity, "Failed to add expense", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadDashboardData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val summaryResponse = apiService.getDashboardSummary()
                val expensesResponse = apiService.getExpenses()

                withContext(Dispatchers.Main) {
                    if (summaryResponse.isSuccessful && summaryResponse.body()?.data != null) {
                        val summary = summaryResponse.body()!!.data!!
                        updateBudgetCard(summary.totalExpenses, summary.monthlyBudget, summary.remainingBudget)
                        updateStatistics(summary.avgDailySpending, summary.topCategory)
                    }

                    if (expensesResponse.isSuccessful && expensesResponse.body()?.data != null) {
                        val expenses = expensesResponse.body()!!.data!!
                        updateRecentExpenses(expenses)
                        updatePieChart(expenses)
                        updateBarChart(expenses)
                        updateHighestExpense(expenses)
                        updateSpendingInsight(expenses)
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardActivity", "Error loading data", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DashboardActivity, "Error loading dashboard", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateBudgetCard(totalExpenses: Double, monthlyBudget: Double, remaining: Double) {
        val tvTotalSpending = findViewById<TextView>(R.id.tvTotalSpending)
        val tvBudgetLimit = findViewById<TextView>(R.id.tvBudgetLimit)
        val tvBudgetStatus = findViewById<TextView>(R.id.tvBudgetStatus)
        val progressBudget = findViewById<ProgressBar>(R.id.progressBudget)

        tvTotalSpending.text = "₱${formatter.format(totalExpenses)}"
        tvBudgetLimit.text = " / ₱${formatter.format(monthlyBudget)}"

        if (monthlyBudget > 0) {
            val progress = ((totalExpenses / monthlyBudget) * 100).toInt().coerceAtMost(100)
            progressBudget.progress = progress

            if (totalExpenses > monthlyBudget) {
                val over = totalExpenses - monthlyBudget
                tvBudgetStatus.text = "₱${formatter.format(over)} over"
                tvBudgetStatus.setTextColor(getColor(R.color.warning))
                progressBudget.progressDrawable = getDrawable(R.drawable.progress_budget_exceeded)
            } else {
                tvBudgetStatus.text = "₱${formatter.format(remaining)} left"
                tvBudgetStatus.setTextColor(getColor(R.color.primary))
                progressBudget.progressDrawable = getDrawable(R.drawable.progress_budget)
            }
        }
    }

    private fun updateStatistics(avgDaily: Double, topCategory: String?) {
        findViewById<TextView>(R.id.tvAvgDaily).text = "₱${avgDaily.toInt()}"
        findViewById<TextView>(R.id.tvTopCategory).text = topCategory ?: "—"
    }

    private fun updateHighestExpense(expenses: List<Expense>) {
        val highest = expenses.maxByOrNull { it.amount }
        findViewById<TextView>(R.id.tvHighest).text = if (highest != null) "₱${highest.amount.toInt()}" else "₱0"
    }

    private fun updateSpendingInsight(expenses: List<Expense>) {
        // Simple insight: compare total with a placeholder for last month
        val tvInsight = findViewById<TextView>(R.id.tvSpendingInsight)
        val tvLastMonth = findViewById<TextView>(R.id.tvLastMonth)

        val total = expenses.sumOf { it.amount }
        tvInsight.text = "Spending this month: ₱${formatter.format(total)}"
        tvLastMonth.text = "Last month: ₱0.00"
    }

    private fun updateRecentExpenses(expenses: List<Expense>) {
        val recent = expenses.sortedByDescending { it.expenseDate }.take(5)
        recentAdapter.updateData(recent)
    }

    private fun updatePieChart(expenses: List<Expense>) {
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val categoryTotals = expenses.groupBy { it.category ?: "Other" }
            .mapValues { (_, exps) -> exps.sumOf { it.amount }.toFloat() }

        if (categoryTotals.isEmpty()) {
            pieChart.setNoDataText("No expenses yet")
            pieChart.invalidate()
            return
        }

        val entries = categoryTotals.map { (cat, total) ->
            PieEntry(total, cat)
        }

        val chartColors = listOf(
            Color.parseColor("#2F9E73"),
            Color.parseColor("#F59E0B"),
            Color.parseColor("#3B82F6"),
            Color.parseColor("#EF4444"),
            Color.parseColor("#8B5CF6")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = chartColors
            valueTextSize = 12f
            valueTextColor = Color.WHITE
            sliceSpace = 2f
        }

        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 45f
            transparentCircleRadius = 50f
            setHoleColor(Color.TRANSPARENT)
            legend.isEnabled = true
            legend.textSize = 12f
            setEntryLabelColor(Color.TRANSPARENT)
            animateY(800)
            invalidate()
        }
    }

    private fun updateBarChart(expenses: List<Expense>) {
        val barChart = findViewById<BarChart>(R.id.barChart)

        // Group expenses by week of month
        val weekTotals = FloatArray(4) { 0f }
        for (expense in expenses) {
            try {
                val date = LocalDate.parse(expense.expenseDate)
                val week = ((date.dayOfMonth - 1) / 7).coerceIn(0, 3)
                weekTotals[week] += expense.amount.toFloat()
            } catch (_: Exception) {
                // skip if date parsing fails
            }
        }

        val entries = weekTotals.mapIndexed { index, total ->
            BarEntry(index.toFloat(), total)
        }

        val dataSet = BarDataSet(entries, "Weekly").apply {
            color = Color.parseColor("#2F9E73")
            valueTextSize = 10f
            valueTextColor = Color.parseColor("#1F2937")
        }

        barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.6f }
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(listOf("W1", "W2", "W3", "W4"))
                granularity = 1f
                setDrawGridLines(false)
            }
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            animateY(800)
            invalidate()
        }
    }
}
