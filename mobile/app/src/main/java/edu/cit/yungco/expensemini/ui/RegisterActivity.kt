package edu.cit.yungco.expensemini.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.network.ApiClient
import edu.cit.yungco.expensemini.network.models.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etRegisterEmail)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        tvSignIn.setOnClickListener {
            finish() // Close RegisterActivity and return to LoginActivity
        }

        btnRegister.setOnClickListener {
            val fName = etFirstName.text.toString().trim()
            val lName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val cPassword = etConfirmPassword.text.toString().trim()

            if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != cPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Registering..."

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = RegisterRequest(fName, lName, email, password)
                    val response = ApiClient.authService.register(request)

                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Create Account"

                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Account Created Successfully!", Toast.LENGTH_LONG).show()
                            finish() // Send them back to Login Activity
                        } else {
                            // Generally returns 400 if email exists
                            Toast.makeText(this@RegisterActivity, "Registration Failed. Email may be in use.", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        btnRegister.text = "Create Account"
                        Toast.makeText(this@RegisterActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
