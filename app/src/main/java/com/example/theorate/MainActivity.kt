package com.example.theorate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button.setOnClickListener {
            performRegister()
        }
        already_have_account.setOnClickListener {
            Log.d("MainActivity", "Try to show login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }



    }
    private fun performRegister()
    {
        val email = Email_Register.text.toString()
        val password = Password_register.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please Enter your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("MainActivity", "Email is:"+email)
        Log.d("MainActivity", "password: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener
                Log.d("Main", "Successfully created user with uid:${it!!.result!!.user!!.uid}")
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user:${it.message}")
                Toast.makeText(this,"Failed to create user", Toast.LENGTH_SHORT).show()
            }
    }
}


