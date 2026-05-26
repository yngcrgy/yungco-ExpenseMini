package edu.cit.yungco.expensemini.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.ExpenseApiService
import edu.cit.yungco.expensemini.network.SessionManager
import edu.cit.yungco.expensemini.network.models.Expense
import edu.cit.yungco.expensemini.network.models.ExpenseRequest
import edu.cit.yungco.expensemini.ui.AdminActivity
import edu.cit.yungco.expensemini.ui.MainActivity
import edu.cit.yungco.expensemini.ui.adapter.RecentExpenseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ExpenseApiService
    private lateinit var recentAdapter: RecentExpenseAdapter
    private val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        apiService = ApiClient.getExpenseService(requireContext())

        setupRecyclerView(view)
        setupQuickAdd(view)
        setupClickListeners(view)

        loadDashboardData(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { loadDashboardData(it) }
    }

    private fun setupRecyclerView(view: View) {
        recentAdapter = RecentExpenseAdapter()
        val rv = view.findViewById<RecyclerView>(R.id.rvRecentExpenses)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = recentAdapter
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(R.id.nav_add)
        }

        view.findViewById<TextView>(R.id.tvViewAll).setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(R.id.nav_history)
        }
        
        val btnAdmin = view.findViewById<Button>(R.id.btnAdminPortal)
        if (sessionManager.getUserRole() == "ADMIN") {
            btnAdmin.visibility = View.VISIBLE
            btnAdmin.setOnClickListener {
                startActivity(Intent(requireContext(), AdminActivity::class.java))
            }
        } else {
            btnAdmin.visibility = View.GONE
        }
    }

    private fun setupQuickAdd(view: View) {
        val quickAddItems = listOf(
            Triple(R.id.btnQuickCoffee, "Coffee", 150.0),
            Triple(R.id.btnQuickTransport, "Transport", 50.0),
            Triple(R.id.btnQuickLunch, "Lunch", 120.0),
            Triple(R.id.btnQuickSnacks, "Snacks", 80.0)
        )

        for ((viewId, title, amount) in quickAddItems) {
            view.findViewById<LinearLayout>(viewId).setOnClickListener {
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
                        Toast.makeText(requireContext(), "$title added! ₱${amount.toInt()}", Toast.LENGTH_SHORT).show()
                        view?.let { loadDashboardData(it) }
                    } else {
                        Toast.makeText(requireContext(), "Failed to add expense", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadDashboardData(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val summaryResponse = apiService.getDashboardSummary()
                val expensesResponse = apiService.getExpenses()

                withContext(Dispatchers.Main) {
                    if (summaryResponse.isSuccessful && summaryResponse.body()?.data != null) {
                        val summary = summaryResponse.body()!!.data!!
                        updateBudgetCard(view, summary.totalExpenses, summary.monthlyBudget, summary.remainingBudget)
                        updateStatistics(view, summary.avgDailySpending, summary.topCategory)
                    }

                    if (expensesResponse.isSuccessful && expensesResponse.body()?.data != null) {
                        val expenses = expensesResponse.body()!!.data!!
                        updateRecentExpenses(expenses)
                        updatePieChart(view, expenses)
                        updateBarChart(view, expenses)
                        updateHighestExpense(view, expenses)
                        updateSpendingInsight(view, expenses)
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardFragment", "Error loading data", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error loading dashboard", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateBudgetCard(view: View, totalExpenses: Double, monthlyBudget: Double, remaining: Double) {
        val tvTotalSpending = view.findViewById<TextView>(R.id.tvTotalSpending)
        val tvBudgetLimit = view.findViewById<TextView>(R.id.tvBudgetLimit)
        val tvBudgetStatus = view.findViewById<TextView>(R.id.tvBudgetStatus)
        val progressBudget = view.findViewById<ProgressBar>(R.id.progressBudget)

        tvTotalSpending.text = "₱${formatter.format(totalExpenses)}"
        tvBudgetLimit.text = " / ₱${formatter.format(monthlyBudget)}"

        if (monthlyBudget > 0) {
            val progress = ((totalExpenses / monthlyBudget) * 100).toInt().coerceAtMost(100)
            progressBudget.progress = progress

            if (totalExpenses > monthlyBudget) {
                val over = totalExpenses - monthlyBudget
                tvBudgetStatus.text = "₱${formatter.format(over)} over"
                tvBudgetStatus.setTextColor(requireContext().getColor(R.color.warning))
                progressBudget.progressDrawable = requireContext().getDrawable(R.drawable.progress_budget_exceeded)
            } else {
                tvBudgetStatus.text = "₱${formatter.format(remaining)} left"
                tvBudgetStatus.setTextColor(requireContext().getColor(R.color.primary))
                progressBudget.progressDrawable = requireContext().getDrawable(R.drawable.progress_budget)
            }
        }
    }

    private fun updateStatistics(view: View, avgDaily: Double, topCategory: String?) {
        view.findViewById<TextView>(R.id.tvAvgDaily).text = "₱${avgDaily.toInt()}"
        view.findViewById<TextView>(R.id.tvTopCategory).text = topCategory ?: "—"
    }

    private fun updateHighestExpense(view: View, expenses: List<Expense>) {
        val highest = expenses.maxByOrNull { it.amount }
        view.findViewById<TextView>(R.id.tvHighest).text = if (highest != null) "₱${highest.amount.toInt()}" else "₱0"
    }

    private fun updateSpendingInsight(view: View, expenses: List<Expense>) {
        val tvInsight = view.findViewById<TextView>(R.id.tvSpendingInsight)
        val tvLastMonth = view.findViewById<TextView>(R.id.tvLastMonth)

        val total = expenses.sumOf { it.amount }
        tvInsight.text = "Spending this month: ₱${formatter.format(total)}"
        tvLastMonth.text = "Last month: ₱0.00"
    }

    private fun updateRecentExpenses(expenses: List<Expense>) {
        val recent = expenses.sortedByDescending { it.expenseDate }.take(5)
        recentAdapter.updateData(recent)
    }

    private fun updatePieChart(view: View, expenses: List<Expense>) {
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
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
            Color.parseColor("#2da57f"),
            Color.parseColor("#fbbf24"),
            Color.parseColor("#60a5fa"),
            Color.parseColor("#ef4444"),
            Color.parseColor("#a78bfa")
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

    private fun updateBarChart(view: View, expenses: List<Expense>) {
        val barChart = view.findViewById<BarChart>(R.id.barChart)

        val weekTotals = FloatArray(4) { 0f }
        for (expense in expenses) {
            try {
                val date = LocalDate.parse(expense.expenseDate)
                val week = ((date.dayOfMonth - 1) / 7).coerceIn(0, 3)
                weekTotals[week] += expense.amount.toFloat()
            } catch (_: Exception) {
            }
        }

        val entries = weekTotals.mapIndexed { index, total ->
            BarEntry(index.toFloat(), total)
        }

        val dataSet = BarDataSet(entries, "Weekly").apply {
            color = Color.parseColor("#2da57f")
            valueTextSize = 10f
            valueTextColor = Color.parseColor("#1f2937")
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