package com.example.theorate.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.theorate.R



class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        supportActionBar?.title = "Chat Log"
    }
}
