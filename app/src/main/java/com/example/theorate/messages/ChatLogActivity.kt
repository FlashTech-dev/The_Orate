package com.example.theorate.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.theorate.R
import com.example.theorate.models.ChatMessage
import com.example.theorate.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG="ChatLog"
    }
    val adapter=GroupAdapter<ViewHolder>()
    var toUser:User? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chatlog.adapter=adapter

        toUser =intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username
       // setupDummtData()
        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG,"Attempted sending message")
            performSendMessage()
        }
    }
    private fun listenForMessages()
    {
        val ref =FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatMessage::class.java)
                if (chatMessage!=null){
                    Log.d(TAG,chatMessage.text)
                    if (chatMessage.fromId==FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    }
                    else{

                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage()
    {
       val text= edittext_chat_log.text.toString()
        val fromId =FirebaseAuth.getInstance().uid
        val user =intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user.uid
        if (fromId==null) return
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage= ChatMessage(reference.key!!, text ,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved chat message:${reference.key}")
            }
    }

}
class ChatFromItem(val text:String): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text=text

    }
    override fun getLayout(): Int
    {
        return R.layout.chat_from_row
    }
}
class ChatToItem(val text:String, val user: User): Item<ViewHolder>()
{
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text =text
        val uri =user.profileImageUrl
        val targetImageView =viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int
    {
        return R.layout.chat_to_row
    }
}