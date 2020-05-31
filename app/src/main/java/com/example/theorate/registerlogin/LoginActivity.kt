package com.example.theorate.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.theorate.R
import com.example.theorate.messages.LatestMessageActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        login_button.setOnClickListener {
            performLogin()
        }

        back_to_register.setOnClickListener {
            finish()
        }

    }


    private fun performLogin() {
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Enter Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("LoginActivity", "Succesfully Logged in:${it!!.result!!.user!!.uid}")
                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Login:${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
