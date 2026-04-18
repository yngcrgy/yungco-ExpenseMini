package edu.cit.yungco.expensemini.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("expense_mini_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
    }

    fun saveSession(token: String, userId: Long, email: String, firstName: String, lastName: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1)

    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun getFirstName(): String = prefs.getString(KEY_FIRST_NAME, "") ?: ""

    fun getLastName(): String = prefs.getString(KEY_LAST_NAME, "") ?: ""

    fun getFullName(): String {
        val first = getFirstName()
        val last = getLastName()
        return if (first.isNotEmpty() || last.isNotEmpty()) "$first $last".trim() else "User"
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
