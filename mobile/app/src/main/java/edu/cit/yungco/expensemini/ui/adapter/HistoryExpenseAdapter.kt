package edu.cit.yungco.expensemini.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.models.Expense
import java.text.NumberFormat
import java.util.Locale

class HistoryExpenseAdapter(
    private var expenses: List<Expense> = emptyList(),
    private val onEdit: (Expense) -> Unit,
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<HistoryExpenseAdapter.ViewHolder>() {

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
        val tvNotes: TextView = view.findViewById(R.id.tvExpenseNotes)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        holder.tvIcon.text = categoryIcons[expense.category] ?: "📦"
        holder.tvTitle.text = expense.title
        holder.tvDate.text = expense.expenseDate ?: ""
        holder.tvAmount.text = "-₱${formatter.format(expense.amount)}"

        if (!expense.notes.isNullOrEmpty()) {
            holder.tvNotes.text = expense.notes
            holder.tvNotes.visibility = View.VISIBLE
        } else {
            holder.tvNotes.visibility = View.GONE
        }

        holder.btnEdit.setOnClickListener { onEdit(expense) }
        holder.btnDelete.setOnClickListener { onDelete(expense) }
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
