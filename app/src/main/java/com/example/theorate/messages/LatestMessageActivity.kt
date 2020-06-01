package com.example.theorate.messages

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.theorate.R
import com.example.theorate.models.ChatMessage
import com.example.theorate.models.User
import com.example.theorate.registerlogin.RegisterActivity
import com.example.theorate.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageActivity : AppCompatActivity() {
    companion object{
        var currentUser : User?= null
        val TAG="LatestMessages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        fetchCurrentUser()
        listenForLatestMessages()
        VerifyUserLogin()
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG,"123")
            val intent =Intent(this,ChatLogActivity::class.java)
            val row=item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        recyclerview_latestmessage.adapter =adapter
        recyclerview_latestmessage.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL) )
    }


    val latestMessageMap =HashMap<String,ChatMessage>()
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun listenForLatestMessages()
    {
        val fromId =FirebaseAuth.getInstance().uid
        val ref =FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage =p0.getValue(ChatMessage::class.java)?:return
                latestMessageMap[p0.key!!] =chatMessage
                refreshRecyclerViewMessages()

            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {

                val chatMessage =p0.getValue(ChatMessage::class.java)?:return
                latestMessageMap[p0.key!!] =chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    val adapter=GroupAdapter<ViewHolder>()


    private fun fetchCurrentUser()
    {
        val uid =FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addListenerForSingleValueEvent(object:ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser=p0.getValue(User::class.java)
                Log.d("LatestMessages","CurrentUser${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
    private fun VerifyUserLogin()
    {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {

            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId)
        {
            R.id.new_message -> {
                val intent =Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}