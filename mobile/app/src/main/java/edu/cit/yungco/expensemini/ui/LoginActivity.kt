package edu.cit.yungco.expensemini.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.models.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        tvCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple clicks
            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = LoginRequest(email, password)
                    val response = ApiClient.authService.login(request)

                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"

                        if (response.isSuccessful && response.body() != null) {
                            val authResponse = response.body()!!
                            // We have the JWT token inside authResponse.token
                            Toast.makeText(this@LoginActivity, "Welcome ${authResponse.firstName}!", Toast.LENGTH_LONG).show()
                            
                            // ToDo: Save the JWT Token
                            // ToDo: Navigate to Dashboard
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"
                        Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
