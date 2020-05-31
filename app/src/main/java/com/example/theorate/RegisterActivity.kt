package com.example.theorate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

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
        photo_login.setOnClickListener {
            Log.d("MainActivity", "try to show photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("MainActivity", "photo is selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_register.setImageBitmap(bitmap)
            photo_login.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)

            //photo_login.setBackgroundDrawable(bitmapDrawable)

        }
    }


    private fun performRegister() {
        val email = Email_Register.text.toString()
        val password = Password_register.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Enter your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("MainActivity", "Email is:$email")
        Log.d("MainActivity", "password: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("MainActivity", "Successfully created user with uid:${it!!.result!!.user!!.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user:${it.message}")
                Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity", "Successful image upload :${it.metadata?.path} ")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("MainActivity", "FileLocation: $it")
                    SaveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }

    private fun SaveUserToFirebaseDatabase(profileImageUrl:String) {
       val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid,
            username_register.text.toString(),
            profileImageUrl
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "saved user to firebase")
                val intent =Intent(this, LatestMessageActivity::class.java)
                intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("MainActivity","failed to save the user: ${it.message}")
            }
    }

}


class User(val uid: String , val username: String , val profileImageUrl: String){
    constructor() : this("","","")
}



