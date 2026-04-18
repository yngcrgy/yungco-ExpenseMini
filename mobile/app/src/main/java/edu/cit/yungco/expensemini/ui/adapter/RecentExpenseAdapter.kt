package edu.cit.yungco.expensemini.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.models.Expense
import java.text.NumberFormat
import java.util.Locale

class RecentExpenseAdapter(
    private var expenses: List<Expense> = emptyList()
) : RecyclerView.Adapter<RecentExpenseAdapter.ViewHolder>() {

    private val categoryIcons = mapOf(
        "Food" to "🍔",
        "Transport" to "🚌",
        "Personal" to "🛍️",
        "School" to "📚",
        "Coffee" to "☕",
        "Other" to "📦"
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvExpenseIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvExpenseTitle)
        val tvDate: TextView = view.findViewById(R.id.tvExpenseDate)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvIcon.text = categoryIcons[expense.category] ?: "📦"
        holder.tvTitle.text = expense.title
        holder.tvDate.text = expense.expenseDate ?: ""

        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        holder.tvAmount.text = "-₱${formatter.format(expense.amount)}"
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
