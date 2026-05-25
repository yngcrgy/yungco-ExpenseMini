package edu.cit.yungco.expensemini.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.AdminUser
import android.widget.ImageButton

class AdminUserAdapter(private val onDeleteClick: (Long) -> Unit) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    private var users = listOf<AdminUser>()

    fun updateData(newUsers: List<AdminUser>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View, val onDeleteClick: (Long) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvInitials: TextView = itemView.findViewById(R.id.tvUserInitials)
        private val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        private val tvRole: TextView = itemView.findViewById(R.id.tvUserRole)

        fun bind(user: AdminUser) {
            val fName = user.firstName ?: ""
            val lName = user.lastName ?: ""
            val initial = if (fName.isNotEmpty()) fName.take(1) else user.email.take(1).uppercase()
            
            tvInitials.text = initial
            tvName.text = "$fName $lName".trim().ifEmpty { "Unknown User" }
            tvEmail.text = user.email
            tvRole.text = user.role
            
            if (user.role == "ADMIN") {
                tvRole.setTextColor(Color.parseColor("#6D28D9"))
                tvRole.setBackgroundColor(Color.parseColor("#EDE9FE"))
            } else {
                tvRole.setTextColor(Color.parseColor("#4338CA"))
                tvRole.setBackgroundColor(Color.parseColor("#E0E7FF"))
            }

            val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteUser)
            if (user.role == "ADMIN") {
                btnDelete.visibility = View.GONE
            } else {
                btnDelete.visibility = View.VISIBLE
                btnDelete.setOnClickListener {
                    onDeleteClick(user.id)
                }
            }
        }
    }
}
