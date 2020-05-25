package com.example.theorate

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.log

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

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Login:${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
